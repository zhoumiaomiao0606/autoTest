package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.mapper.LoanCreditInfoDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.AllCustDetailParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.LoanFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
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
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Autowired
    private LoanFileService loanFileService;


    @Override
    public ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkNotNull(principalLenderId, "主贷人ID不能为空");
        Preconditions.checkNotNull(commonLenderId, "共贷人ID不能为空");

        // 编辑原主贷人
        LoanCustomerDO principalLenderDO = new LoanCustomerDO();
        principalLenderDO.setId(principalLenderId);
        principalLenderDO.setCustType(CUST_TYPE_COMMON);
        principalLenderDO.setPrincipalCustId(commonLenderId);
        principalLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(principalLenderDO);

        // 编辑原共贷人
        LoanCustomerDO commonLenderDO = new LoanCustomerDO();
        commonLenderDO.setId(commonLenderId);
        commonLenderDO.setCustType(CUST_TYPE_PRINCIPAL);
        commonLenderDO.setPrincipalCustId(commonLenderId);
        commonLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(commonLenderDO);

        // 编辑所有(其他)关联人的 主贷人ID
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
        List<LoanCustomerDO> loanCustomerDOList = loanCustomerDOMapper.listByPrincipalCustIdAndType(principalLenderId, null, VALID_STATUS);

        CustDetailVO custDetailVO = new CustDetailVO();
        if (!CollectionUtils.isEmpty(loanCustomerDOList)) {
            // 填充客户详情信息
            fillCustInfo(custDetailVO, loanCustomerDOList);
        }

        return ResultBean.ofSuccess(custDetailVO);
    }


    @Override
    public ResultBean<Long> updateAll(AllCustDetailParam allCustDetailParam) {
        Preconditions.checkNotNull(allCustDetailParam, "客户信息不能为空");

        updateOrInsertLoanCustomer(allCustDetailParam);

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }


    @Override
    public ResultBean<Long> create(LoanCustomerDO loanCustomerDO) {
        Preconditions.checkNotNull(loanCustomerDO, "客户信息不能为空");
        Preconditions.checkNotNull(loanCustomerDO.getCustType(), "客户类型不能为空");
        if (!CUST_TYPE_PRINCIPAL.equals(loanCustomerDO.getCustType())) {
            Preconditions.checkNotNull(loanCustomerDO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        loanCustomerDO.setStatus(VALID_STATUS);
        loanCustomerDO.setGmtCreate(new Date());
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.insertSelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "创建客户信息失败");

        if (CUST_TYPE_PRINCIPAL.equals(loanCustomerDO.getCustType())) {
            LoanCustomerDO customerDO = new LoanCustomerDO();
            customerDO.setId(loanCustomerDO.getId());
            customerDO.setPrincipalCustId(loanCustomerDO.getId());
            int updateCount = loanCustomerDOMapper.updateByPrimaryKeySelective(customerDO);
            Preconditions.checkArgument(updateCount > 0, "设置主贷人ID失败");
        }

        return ResultBean.ofSuccess(loanCustomerDO.getId(), "创建客户信息成功");
    }

    @Override
    public ResultBean<Void> update(LoanCustomerDO loanCustomerDO) {
        Preconditions.checkNotNull(loanCustomerDO, "客户信息不能为空");
        Preconditions.checkNotNull(loanCustomerDO.getId(), "客户ID不能为空");

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

                        // fillCredit
                        fillCredit(principalLender, e.getId());
                    }

                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {
                        CustomerVO commonLender = new CustomerVO();
                        BeanUtils.copyProperties(e, commonLender);

                        // fillFiles
                        fillFiles(commonLender);
                        commonLenderList.add(commonLender);

                        // fillCredit
                        fillCredit(commonLender, e.getId());
                    }
                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {
                        CustomerVO guarantor = new CustomerVO();
                        BeanUtils.copyProperties(e, guarantor);

                        // fillFiles
                        fillFiles(guarantor);
                        guarantorList.add(guarantor);

                        // fillCredit
                        fillCredit(guarantor, e.getId());
                    }
                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {
                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);

                        // fillFiles
                        fillFiles(emergencyContact);
                        emergencyContactList.add(emergencyContact);

                        // fillCredit
                        fillCredit(emergencyContact, e.getId());
                    }
                });

        List<CustomerVO> sortedCommonLenderList = commonLenderList.parallelStream().sorted(Comparator.comparing(CustomerVO::getId)).collect(Collectors.toList());
        List<CustomerVO> sortedGuarantorList = guarantorList.parallelStream().sorted(Comparator.comparing(CustomerVO::getId)).collect(Collectors.toList());
        List<CustomerVO> sortedEmergencyContactList = emergencyContactList.parallelStream().sorted(Comparator.comparing(CustomerVO::getId)).collect(Collectors.toList());

        custDetailVO.setCommonLenderList(sortedCommonLenderList);
        custDetailVO.setGuarantorList(sortedGuarantorList);
        custDetailVO.setEmergencyContactList(sortedEmergencyContactList);
    }

    /**
     * 填充征信信息
     *
     * @param principalLender
     * @param customerId
     */
    private void fillCredit(CustomerVO principalLender, Long customerId) {
        List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(customerId, null);
        if (!CollectionUtils.isEmpty(loanCreditInfoDOS)) {
            loanCreditInfoDOS.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        if (CREDIT_TYPE_BANK.equals(e.getType())) {
                            principalLender.setBankCreditResult(e.getResult());
                            principalLender.setBankCreditInfo(e.getInfo());
                        } else if (CREDIT_TYPE_SOCIAL.equals(e.getType())) {
                            principalLender.setSocialCreditResult(e.getResult());
                            principalLender.setSocialCreditInfo(e.getInfo());
                        }
                    });
        }
    }

    private void fillFiles(CustomerVO customerVO) {
        ResultBean<List<FileVO>> fileResultBean = loanFileService.listByCustomerId(customerVO.getId());
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        List<FileVO> fileVOS = fileResultBean.getData();
        List<FileVO> files = fileVOS.parallelStream()
                .filter(Objects::nonNull)
                .map(f -> {
                    FileVO file = new FileVO();
                    BeanUtils.copyProperties(f, file);
                    return file;
                })
                .collect(Collectors.toList());

        customerVO.setFiles(files);
    }

    private void updateOrInsertLoanCustomer(AllCustDetailParam allCustDetailParam) {

        // 主贷人
        CustomerParam principalLender = allCustDetailParam.getPrincipalLender();
        updateOrInsertCustomer(principalLender);

        // 共贷人列表
        List<CustomerParam> commonLenderList = allCustDetailParam.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderList)) {

            commonLenderList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }

        // 担保人列表
        List<CustomerParam> guarantorList = allCustDetailParam.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorList)) {

            guarantorList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }

        // 紧急联系人列表
        List<CustomerParam> emergencyContactList = allCustDetailParam.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactList)) {

            emergencyContactList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        updateOrInsertCustomer(e);
                    });
        }
    }

    private void updateOrInsertCustomer(CustomerParam customerParam) {
        if (null == customerParam) {
            return;
        }

        if (null == customerParam.getId()) {
            // insert
            createLoanCustomer(customerParam);
        } else {
            // update
            LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
            BeanUtils.copyProperties(customerParam, loanCustomerDO);
            ResultBean<Void> resultBean = update(loanCustomerDO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

            // TODO file
//            ResultBean<Void> updateFileResultBean = loanFileService.update(customerParam.getId(), customerParam.getFiles());
//            Preconditions.checkArgument(updateFileResultBean.getSuccess(), updateFileResultBean.getMsg());
        }
    }

    private Long createLoanCustomer(CustomerParam customerParam) {
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerParam, loanCustomerDO);
        ResultBean<Long> createCustomerResult = create(loanCustomerDO);
        Preconditions.checkArgument(createCustomerResult.getSuccess(), "创建客户信息失败");

        //  TODO 文件上传
//        ResultBean<Void> createFileResultBean = loanFileService.create(createCustomerResult.getData(), customerParam.getFiles());
//        Preconditions.checkArgument(createFileResultBean.getSuccess(), "创建文件信息失败");

        // 返回客户ID
        return createCustomerResult.getData();
    }

}
