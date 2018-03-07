package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.CustDetailParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.LoanFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.CustomerConst.*;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
@Service
public class LoanCustomerServiceImpl implements LoanCustomerService {

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanFileService loanFileService;


    @Override
    public ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");
        Preconditions.checkNotNull(principalLenderId, "主贷人ID不能为空");
        Preconditions.checkNotNull(commonLenderId, "共贷人ID不能为空");

        // 编辑原主贷人
        LoanCustomerDO principalLenderDO = new LoanCustomerDO();
        principalLenderDO.setId(principalLenderId);
        principalLenderDO.setCustType(CUST_TYPE_COMMON);
//        principalLenderDO.setPrincipalCustId(commonLenderId);
        principalLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(principalLenderDO);

        // 编辑原共贷人
        LoanCustomerDO commonLenderDO = new LoanCustomerDO();
        commonLenderDO.setId(commonLenderId);
        commonLenderDO.setCustType(CUST_TYPE_PRINCIPAL);
//        commonLenderDO.setPrincipalCustId(null);
        commonLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(commonLenderDO);

        // 编辑所有关联人的 主贷人ID   TODO  修改时间
        loanCustomerDOMapper.updatePrincipalCustId(principalLenderId, commonLenderId);

        // 编辑业务单主贷人
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(orderId);
        loanOrderDO.setLoanCustomerId(commonLenderId);
        loanOrderDO.setGmtModify(new Date());
        loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

        return ResultBean.ofSuccess(null, "主贷人和共贷人切换成功");
    }

    @Override
    public ResultBean<CustDetailVO> detailAll(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据orderId获取主贷人ID
        Long principalLenderId = loanOrderDOMapper.getCustIdById(orderId);

        // 根据主贷人ID获取客户详情列表
        List<LoanCustomerDO> loanCustomerDOList = loanCustomerDOMapper.getListById(principalLenderId);

        CustDetailVO custDetailVO = new CustDetailVO();
        if (!CollectionUtils.isEmpty(loanCustomerDOList)) {
            // 填充客户详情信息
            fillCustInfo(custDetailVO, loanCustomerDOList);
        }

        return ResultBean.ofSuccess(custDetailVO);
    }


    @Override
    public ResultBean<Long> updateAll(CustDetailParam custDetailParam) {
        Preconditions.checkNotNull(custDetailParam, "客户信息不能为空");

        updateOrInsertLoanCustomer(custDetailParam);

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }



    @Override
    public ResultBean<Long> create(CustomerVO customerVO) {
        Preconditions.checkNotNull(customerVO, "客户信息不能为空");
        Preconditions.checkNotNull(customerVO.getCustType(), "客户类型不能为空");
        if (!CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
            Preconditions.checkNotNull(customerVO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerVO, loanCustomerDO);
        loanCustomerDO.setGmtCreate(new Date());
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.insertSelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "创建客户信息失败");

        return ResultBean.ofSuccess(loanCustomerDO.getId(), "创建客户信息成功");
    }

    @Override
    public ResultBean<Void> update(CustomerVO customerVO) {
        Preconditions.checkNotNull(customerVO, "客户信息不能为空");
        Preconditions.checkNotNull(customerVO.getId(), "客户ID不能为空");
        Preconditions.checkNotNull(customerVO.getCustType(), "客户类型不能为空");
        if (!CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
            Preconditions.checkNotNull(customerVO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerVO, loanCustomerDO);
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "编辑客户信息失败");

        return ResultBean.ofSuccess(null, "编辑客户信息成功");
    }

    @Override
    public ResultBean<CustomerVO> getById(Long id) {
        Preconditions.checkNotNull(id, "客户ID不能为空");

        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(id, null);
        CustomerVO customerVO = new CustomerVO();
        BeanUtils.copyProperties(loanCustomerDO, customerVO);
//        customerVO.setFiles(JSON.parseArray(loanCustomerDO.getFiles(), CustomerVO.File.class));

        return ResultBean.ofSuccess(customerVO);
    }

    /**
     * 填充客户详情信息
     *
     * @param custDetailVO
     * @param loanCustomerDOList
     */
    private void fillCustInfo(CustDetailVO custDetailVO, List<LoanCustomerDO> loanCustomerDOList) {

        List<CustomerVO> commonLenderList = Lists.newArrayList();
        List<CustomerVO> guarantorList = Lists.newArrayList();
        List<CustomerVO> emergencyContactList = Lists.newArrayList();

        loanCustomerDOList.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 主贷人
                    if (CUST_TYPE_PRINCIPAL.equals(e.getCustType())) {
                        CustomerVO principalLender = new CustomerVO();
                        BeanUtils.copyProperties(e, principalLender);

                        // fillFiles
                        fillFiles(principalLender);
                        custDetailVO.setPrincipalLender(principalLender);
                    }

                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {
                        CustomerVO commonLender = new CustomerVO();
                        BeanUtils.copyProperties(e, commonLender);
                        // fillFiles
                        fillFiles(commonLender);
                        commonLenderList.add(commonLender);
                    }
                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {
                        CustomerVO guarantor = new CustomerVO();
                        BeanUtils.copyProperties(e, guarantor);
                        // fillFiles
                        fillFiles(guarantor);
                        guarantorList.add(guarantor);
                    }
                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {
                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);
                        // fillFiles
                        fillFiles(emergencyContact);
                        emergencyContactList.add(emergencyContact);
                    }
                });

        custDetailVO.setCommonLenderList(commonLenderList);
        custDetailVO.setGuarantorList(guarantorList);
        custDetailVO.setEmergencyContactList(emergencyContactList);
    }

    private void fillFiles(CustomerVO customerVO) {
        ResultBean<List<FileVO>> fileResultBean = loanFileService.listByCustomerId(customerVO.getId());
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        List<FileVO> fileVOS = fileResultBean.getData();
        List<FileVO> files = fileVOS.stream()
                .filter(Objects::nonNull)
                .map(f -> {
                    FileVO file = new FileVO();
                    BeanUtils.copyProperties(f, file);
                    return file;
                })
                .collect(Collectors.toList());

        customerVO.setFiles(files);
    }

    private void updateOrInsertLoanCustomer(CustDetailParam custDetailParam) {

        // 主贷人
        CustomerVO principalLender = custDetailParam.getPrincipalLender();
        updateOrInsertCustomer(principalLender);

        // 共贷人列表
        List<CustomerVO> commonLenderVOList = custDetailParam.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderVOList)) {

            commonLenderVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }

        // 担保人列表
        List<CustomerVO> guarantorVOList = custDetailParam.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorVOList)) {

            guarantorVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }

        // 紧急联系人列表
        List<CustomerVO> emergencyContactVOList = custDetailParam.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactVOList)) {

            emergencyContactVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }
    }

    private void updateOrInsertCustomer(CustomerVO customerVO) {
        if (null == customerVO) {
            return;
        }

        if (null == customerVO.getId()) {
            // insert
            createLoanCustomer(customerVO);
        } else {
            // update
            ResultBean<Void> resultBean = update(customerVO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

            // file
            ResultBean<Void> updateFileResultBean = loanFileService.update(customerVO.getId(), customerVO.getFiles());
            Preconditions.checkArgument(updateFileResultBean.getSuccess(), updateFileResultBean.getMsg());
        }
    }

    private Long createLoanCustomer(CustomerVO customerVO) {
        ResultBean<Long> createCustomerResult = create(customerVO);
        Preconditions.checkArgument(createCustomerResult.getSuccess(), "创建客户信息失败");

        // 文件上传
        ResultBean<Void> createFileResultBean = loanFileService.create(createCustomerResult.getData(), customerVO.getFiles());
        Preconditions.checkArgument(createFileResultBean.getSuccess(), "创建文件信息失败");

        // 返回客户ID
        return createCustomerResult.getData();
    }

}
