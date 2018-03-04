package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.*;
import com.yunche.loan.domain.dataObj.*;
import com.yunche.loan.domain.param.CustDetailParam;
import com.yunche.loan.domain.viewObj.*;
import com.yunche.loan.service.CustomerService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.yunche.loan.config.constant.CustomerConst.*;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private CustBaseInfoDOMapper custBaseInfoDOMapper;

    @Autowired
    private CustRelaPersonInfoDOMapper custRelaPersonInfoDOMapper;

    @Autowired
    private LoanProcessOrderDOMapper loanProcessOrderDOMapper;

    @Autowired
    private RuntimeService runtimeService;


    @Override
    public ResultBean<Long> createMainCust(CustBaseInfoVO custBaseInfoVO) {
        // 主贷人
        CustBaseInfoDO custBaseInfoDO = new CustBaseInfoDO();
        BeanUtils.copyProperties(custBaseInfoVO, custBaseInfoDO);
        custBaseInfoDOMapper.insert(custBaseInfoDO);

        List<CustRelaPersonInfoDO> custRelaPersonInfoDOList = custBaseInfoVO.getRelaPersonList();
        if (!CollectionUtils.isEmpty(custRelaPersonInfoDOList)) {
            for (CustRelaPersonInfoDO custRelaPersonInfoDO : custRelaPersonInfoDOList) {
                custRelaPersonInfoDO.setRelaCustId(custBaseInfoDO.getCustId());
                custRelaPersonInfoDOMapper.insert(custRelaPersonInfoDO);
            }
        }
        return ResultBean.ofSuccess(custBaseInfoDO.getCustId(), "创建主贷人成功");
    }

    @Override
    public ResultBean<Long> updateMainCust(CustBaseInfoVO custBaseInfoVO) {
        // 主贷人
        CustBaseInfoDO custBaseInfoDO = new CustBaseInfoDO();
        BeanUtils.copyProperties(custBaseInfoVO, custBaseInfoDO);
        custBaseInfoDOMapper.updateByPrimaryKeySelective(custBaseInfoDO);

        List<CustRelaPersonInfoDO> custRelaPersonInfoDOList = custBaseInfoVO.getRelaPersonList();
        if (!CollectionUtils.isEmpty(custRelaPersonInfoDOList)) {
            for (CustRelaPersonInfoDO custRelaPersonInfoDO : custRelaPersonInfoDOList) {
                custRelaPersonInfoDO.setRelaCustId(custBaseInfoDO.getCustId());
                custRelaPersonInfoDOMapper.updateByPrimaryKeySelective(custRelaPersonInfoDO);
            }
        }
        return ResultBean.ofSuccess(custBaseInfoDO.getCustId(), "修改主贷人成功");
    }

    @Override
    public ResultBean<Long> createRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO) {
        CustRelaPersonInfoDO custRelaPersonInfoDO = new CustRelaPersonInfoDO();
        BeanUtils.copyProperties(custRelaPersonInfoVO, custRelaPersonInfoDO);
        custRelaPersonInfoDOMapper.insert(custRelaPersonInfoDO);

        return ResultBean.ofSuccess(custRelaPersonInfoDO.getCustId(), "创建关联人成功");
    }

    @Override
    public ResultBean<Long> updateRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO) {
        CustRelaPersonInfoDO custRelaPersonInfoDO = new CustRelaPersonInfoDO();
        BeanUtils.copyProperties(custRelaPersonInfoVO, custRelaPersonInfoDO);
        custRelaPersonInfoDOMapper.updateByPrimaryKeySelective(custRelaPersonInfoDO);

        return ResultBean.ofSuccess(custRelaPersonInfoDO.getCustId(), "更新关联人成功");
    }

    @Override
    public ResultBean<Void> deleteRelaCust(Long custId) {
        Preconditions.checkArgument(custId != null, "custId");

        custRelaPersonInfoDOMapper.deleteByPrimaryKey(custId);

        return ResultBean.ofSuccess(null, "删除关联人成功");
    }

    @Override
    public ResultBean<Void> faceOff(String orderId, Long principalLenderId, Long commonLenderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单ID不能为空");
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
        commonLenderDO.setPrincipalCustId(null);
        commonLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(commonLenderDO);

        // 编辑所有关联人的 主贷人ID   TODO  修改时间
        loanCustomerDOMapper.updatePrincipalCustId(principalLenderId, commonLenderId);

        // 编辑业务单主贷人
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setId(orderId);
        loanProcessOrderDO.setLoanCustomerId(commonLenderId);
        loanProcessOrderDO.setGmtModify(new Date());
        loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);

        return ResultBean.ofSuccess(null, "主贷人和共贷人切换成功");
    }

    @Override
    public ResultBean<CustDetailVO> detailAll(String orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据orderId获取主贷人ID
        Long principalLenderId = loanProcessOrderDOMapper.getCustIdById(orderId);

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

        // 主贷人
        CustomerVO principalLenderVO = custDetailParam.getPrincipalLender();
        LoanCustomerDO principalLenderDO = new LoanCustomerDO();
        BeanUtils.copyProperties(principalLenderVO, principalLenderDO);
        principalLenderDO.setFiles(JSON.toJSONString(principalLenderVO.getFiles()));
        principalLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(principalLenderDO);

        // 共贷人列表
        List<CustomerVO> commonLenderVOList = custDetailParam.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderVOList)) {

            commonLenderVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        LoanCustomerDO commonLenderDO = new LoanCustomerDO();
                        BeanUtils.copyProperties(e, commonLenderDO);
                        commonLenderDO.setFiles(JSON.toJSONString(e.getFiles()));
                        commonLenderDO.setGmtModify(new Date());
                        loanCustomerDOMapper.updateByPrimaryKeySelective(commonLenderDO);
                    });
        }

        // 担保人列表
        List<CustomerVO> guarantorVOList = custDetailParam.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorVOList)) {

            guarantorVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        LoanCustomerDO guarantorDO = new LoanCustomerDO();
                        BeanUtils.copyProperties(e, guarantorDO);
                        guarantorDO.setFiles(JSON.toJSONString(e.getFiles()));
                        guarantorDO.setGmtModify(new Date());
                        loanCustomerDOMapper.updateByPrimaryKeySelective(guarantorDO);
                    });
        }

        // 紧急联系人列表
        List<CustomerVO> emergencyContactVOList = custDetailParam.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactVOList)) {

            emergencyContactVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        LoanCustomerDO emergencyContactDO = new LoanCustomerDO();
                        BeanUtils.copyProperties(e, emergencyContactDO);
                        emergencyContactDO.setFiles(JSON.toJSONString(e.getFiles()));
                        emergencyContactDO.setGmtModify(new Date());
                        loanCustomerDOMapper.updateByPrimaryKeySelective(emergencyContactDO);
                    });
        }

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }

    @Override
    public ResultBean<Long> create(String orderId, CustomerVO customerVO) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");
        Preconditions.checkNotNull(customerVO, "客户信息不能为空");
        Preconditions.checkNotNull(customerVO.getCustType(), "客户类型不能为空");
        if (!CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
            Preconditions.checkNotNull(customerVO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerVO, loanCustomerDO);
        loanCustomerDO.setFiles(JSON.toJSONString(customerVO.getFiles()));
        loanCustomerDO.setGmtCreate(new Date());
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.insertSelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "创建客户信息失败");

        // 开启流程单，并关联业务单
        startProcessAndRelaInstProcessOrder(orderId, loanCustomerDO.getId());

        return ResultBean.ofSuccess(loanCustomerDO.getId(), "创建客户信息成功");
    }

    /**
     * @param orderId
     * @param customerId
     */
    private void startProcessAndRelaInstProcessOrder(String orderId, Long customerId) {
        // 开启activiti流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dev_loan_process");

        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.selectByPrimaryKey(orderId, null);
        // 开启本地业务单流程：业务单不存在，则新建
        if (null == loanProcessOrderDO) {
            loanProcessOrderDO = new LoanProcessOrderDO();
            loanProcessOrderDO.setProcessInstId(processInstance.getProcessInstanceId());
            loanProcessOrderDO.setId(orderId);
            loanProcessOrderDO.setLoanCustomerId(customerId);
            loanProcessOrderDO.setGmtCreate(new Date());
            loanProcessOrderDO.setGmtModify(new Date());
            int count = loanProcessOrderDOMapper.insertSelective(loanProcessOrderDO);
            Preconditions.checkArgument(count > 0, "新建业务单失败");
        } else {
            // 已存在，则直接关联
            loanProcessOrderDO.setProcessInstId(processInstance.getProcessInstanceId());
            loanProcessOrderDO.setLoanCustomerId(customerId);
            loanProcessOrderDO.setGmtModify(new Date());
            loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
        }
    }

    @Override
    public ResultBean<Long> update(CustomerVO customerVO) {
        Preconditions.checkNotNull(customerVO, "客户信息不能为空");
        Preconditions.checkNotNull(customerVO.getId(), "客户ID不能为空");
        Preconditions.checkNotNull(customerVO.getCustType(), "客户类型不能为空");
        if (!CUST_TYPE_PRINCIPAL.equals(customerVO.getCustType())) {
            Preconditions.checkNotNull(customerVO.getPrincipalCustId(), "主贷人ID不能为空");
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerVO, loanCustomerDO);
        loanCustomerDO.setFiles(JSON.toJSONString(customerVO.getFiles()));
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "编辑客户信息失败");

        return ResultBean.ofSuccess(loanCustomerDO.getId(), "编辑客户信息成功");
    }

    @Override
    public ResultBean<CustomerVO> getById(Long id) {
        Preconditions.checkNotNull(id, "客户ID不能为空");

        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(id, null);
        CustomerVO customerVO = new CustomerVO();
        BeanUtils.copyProperties(loanCustomerDO, customerVO);
        customerVO.setFiles(JSON.parseArray(loanCustomerDO.getFiles(), CustomerVO.File.class));

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
                        principalLender.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        custDetailVO.setPrincipalLender(principalLender);
                    }
                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {
                        CustomerVO commonLender = new CustomerVO();
                        BeanUtils.copyProperties(e, commonLender);
                        commonLender.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        commonLenderList.add(commonLender);
                    }
                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {
                        CustomerVO guarantor = new CustomerVO();
                        BeanUtils.copyProperties(e, guarantor);
                        guarantor.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        guarantorList.add(guarantor);
                    }
                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {
                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);
                        emergencyContact.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        emergencyContactList.add(emergencyContact);
                    }
                });

        custDetailVO.setCommonLenderList(commonLenderList);
        custDetailVO.setGuarantorList(guarantorList);
        custDetailVO.setEmergencyContactList(emergencyContactList);
    }
}
