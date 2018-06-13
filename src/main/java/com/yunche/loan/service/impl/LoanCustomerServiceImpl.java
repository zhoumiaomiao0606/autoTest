package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.param.AllCustDetailParam;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.domain.vo.CustDetailVO;
import com.yunche.loan.domain.vo.CustomerVO;
import com.yunche.loan.domain.vo.FileVO;
import com.yunche.loan.domain.vo.LoanRepeatVO;
import com.yunche.loan.mapper.LoanCreditInfoDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanProcessDOMapper;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.LoanFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CustomerConst.*;
import static com.yunche.loan.config.constant.GuaranteeRelaConst.GUARANTOR_PERSONAL;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.ORDER_STATUS_CANCEL;

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

    @Autowired
    LoanProcessDOMapper loanProcessDOMapper;


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
    public ResultBean<CustDetailVO> detailAll(Long orderId, Byte fileUploadType) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据orderId获取主贷人ID
        Long principalLenderId = loanOrderDOMapper.getCustIdById(orderId);

        // 根据主贷人ID获取客户详情列表
        List<LoanCustomerDO> loanCustomerDOList = loanCustomerDOMapper.listByPrincipalCustIdAndType(principalLenderId, null, VALID_STATUS);

        CustDetailVO custDetailVO = new CustDetailVO();
        if (!CollectionUtils.isEmpty(loanCustomerDOList)) {
            // 填充客户详情信息
            fillCustInfo(custDetailVO, loanCustomerDOList, fileUploadType);
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
        if(CUST_TYPE_GUARANTOR.equals(loanCustomerDO.getCustType())){
            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.listByPrincipalCustIdAndType(loanCustomerDO.getPrincipalCustId(), CUST_TYPE_GUARANTOR, VALID_STATUS);
            if(CollectionUtils.isEmpty(loanCustomerDOS)){
                if(!String.valueOf(GUARANTOR_PERSONAL).equals(loanCustomerDO.getGuaranteeRela())){
                    Preconditions.checkArgument(false,"您选择的担保人与主担保人关系有误，请核查");
                }
            }else{
                if(String.valueOf(GUARANTOR_PERSONAL).equals(loanCustomerDO.getGuaranteeRela())){
                    Preconditions.checkArgument(false,"您选择的担保人与主担保人关系有误，请核查");
                }
            }
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

        // 无files

        return ResultBean.ofSuccess(customerVO);
    }

    @Override
    public ResultBean<LoanRepeatVO> checkRepeat(String idCard, Long orderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(idCard), "身份证号不能为空");

        LoanRepeatVO loanRepeatVO = new LoanRepeatVO();

        List<Long> principalCustIdList = loanCustomerDOMapper.listPrincipalCustIdByIdCard(idCard);

        if (!CollectionUtils.isEmpty(principalCustIdList)) {

            List<String> orderIdList = principalCustIdList.parallelStream()
                    .filter(Objects::nonNull)
                    .map(customerId -> {

                        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByCustomerId(customerId);
                        if (null != loanOrderDO) {
                            //过滤状态为弃单的订单
                            LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(loanOrderDO.getId());
                            if (null != loanProcessDO && !ORDER_STATUS_CANCEL.equals(loanProcessDO.getOrderStatus())) {
                                return String.valueOf(loanOrderDO.getId());
                            }
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());


            if (null != orderId && !CollectionUtils.isEmpty(orderIdList)) {
                orderIdList.remove(String.valueOf(orderId));
            }
            loanRepeatVO.setOrderIdList(orderIdList);
        }

        return ResultBean.ofSuccess(loanRepeatVO, "成功");
    }

    @Override
    public ResultBean<CustDetailVO> customerDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据主贷人ID获取客户详情列表
        ResultBean<CustDetailVO> resultBean = detailAll(orderId, null);
        return resultBean;
    }

    @Override
    @Transactional
    public ResultBean<Void> updateCustomer(AllCustDetailParam allCustDetailParam) {
        Preconditions.checkNotNull(allCustDetailParam, "客户信息不能为空");

        updateAll(allCustDetailParam);

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> addRelaCustomer(CustomerParam customerParam) {
        // convert
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        convertLoanCustomer(customerParam, loanCustomerDO);

        ResultBean<Long> resultBean = create(loanCustomerDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        // 文件信息保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(resultBean.getData(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        return ResultBean.ofSuccess(resultBean.getData(), "创建关联人成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> delRelaCustomer(Long customerId) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        loanCustomerDO.setId(customerId);
        loanCustomerDO.setStatus(INVALID_STATUS);
        ResultBean<Void> resultBean = update(loanCustomerDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        return ResultBean.ofSuccess(null, "删除关联人成功");
    }

    private void convertLoanCustomer(CustomerParam customerParam, LoanCustomerDO loanCustomerDO) {
        if (null != customerParam) {
            BeanUtils.copyProperties(customerParam, loanCustomerDO);
        }
    }


    /**
     * 填充客户详情信息
     *
     * @param custDetailVO
     * @param loanCustomerDOList
     * @param fileUploadType
     */
    private void fillCustInfo(CustDetailVO custDetailVO, List<LoanCustomerDO> loanCustomerDOList, Byte fileUploadType) {

        List<CustomerVO> commonLenderList = Lists.newArrayList();
        List<CustomerVO> guarantorList = Lists.newArrayList();
        List<CustomerVO> emergencyContactList = Lists.newArrayList();

        loanCustomerDOList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 主贷人
                    if (CUST_TYPE_PRINCIPAL.equals(e.getCustType())) {
                        CustomerVO principalLender = new CustomerVO();
                        BeanUtils.copyProperties(e, principalLender);

                        // fillFiles
                        fillFiles(principalLender, fileUploadType);

                        // fillCredit
                        fillCredit(principalLender, e.getId());

                        custDetailVO.setPrincipalLender(principalLender);
                    }

                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {
                        CustomerVO commonLender = new CustomerVO();
                        BeanUtils.copyProperties(e, commonLender);

                        // fillFiles
                        fillFiles(commonLender, fileUploadType);

                        // fillCredit
                        fillCredit(commonLender, e.getId());

                        commonLenderList.add(commonLender);
                    }
                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {
                        CustomerVO guarantor = new CustomerVO();
                        BeanUtils.copyProperties(e, guarantor);

                        // fillFiles
                        fillFiles(guarantor, fileUploadType);

                        // fillCredit
                        fillCredit(guarantor, e.getId());

                        guarantorList.add(guarantor);
                    }
                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {
                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);

                        // fillFiles
                        fillFiles(emergencyContact, fileUploadType);

                        // fillCredit
                        fillCredit(emergencyContact, e.getId());

                        emergencyContactList.add(emergencyContact);
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

    private void fillFiles(CustomerVO customerVO, Byte fileUploadType) {
        ResultBean<List<FileVO>> fileResultBean = loanFileService.listByCustomerId(customerVO.getId(), fileUploadType);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());
        customerVO.setFiles(fileResultBean.getData());
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

            // 文件信息保存
            ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(customerParam.getId(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
            Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());
        }
    }

    /**
     * 新增用户信息
     *
     * @param customerParam
     * @return
     */
    private Long createLoanCustomer(CustomerParam customerParam) {
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerParam, loanCustomerDO);
        ResultBean<Long> createCustomerResult = create(loanCustomerDO);
        Preconditions.checkArgument(createCustomerResult.getSuccess(), "创建客户信息失败");

        // 文件信息保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(customerParam.getId(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        // 返回客户ID
        return createCustomerResult.getData();
    }
}
