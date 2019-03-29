package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.mapper.LoanCreditInfoBankHisDOMapper;
import com.yunche.loan.mapper.LoanCreditInfoSocialHisDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.service.LoanCreditInfoHisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanAmountConst.EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W;
import static com.yunche.loan.config.constant.LoanCustomerConst.*;
import static com.yunche.loan.service.impl.LoanProcessApprovalCommonServiceImpl.AUTO_EMPLOYEE_ID;
import static com.yunche.loan.service.impl.LoanProcessApprovalCommonServiceImpl.AUTO_EMPLOYEE_NAME;

/**
 * @author liuzhe
 * @date 2018/9/30
 */
@Service
@Transactional
public class LoanCreditInfoHisServiceImpl implements LoanCreditInfoHisService {

    private static final Logger logger = LoggerFactory.getLogger(LoanCreditInfoHisServiceImpl.class);


    @Autowired
    private LoanCreditInfoBankHisDOMapper loanCreditInfoBankHisDOMapper;

    @Autowired
    private LoanCreditInfoSocialHisDOMapper loanCreditInfoSocialHisDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;


    @Override
    public void saveCreditInfoHis_CreditApply(Long principalCustId, Byte loanAmount) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");
        Preconditions.checkNotNull(loanAmount, "loanAmount不能为空");

        // 获取订单所有需要查询征信的客户--方法
        List<LoanCustomerDO> customers = getAllCustomer(principalCustId);

        if (CollectionUtils.isEmpty(customers)) {
            return;
        }


        EmployeeDO loginUser = getLoginUser();

        // 如果是第一次提交征信查询，直接创建历史记录  银行/社会(如果 > 13W)
        LoanCreditInfoBankHisDO last = loanCreditInfoBankHisDOMapper.lastByCustomerId(principalCustId);

        if (null == last) {

            // 第一次    无需过滤
            customers.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        createCreditInfoHis_CreditApply_First(e, loanAmount, loginUser);
                    });

        } else {

            // 小于13W   -->  大于等于13W
            LoanCreditInfoSocialHisDO lastSocialHisDO = loanCreditInfoSocialHisDOMapper.lastByCustomerId(principalCustId);
            if (null == lastSocialHisDO) {

                // 社会征信查询记录 (>=13万)
                if (loanAmount >= EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W) {

                    // 第一次    无需过滤
                    customers.stream()
                            .filter(Objects::nonNull)
                            .forEach(e -> {

                                // 社会征信查询记录 (>=13万)
                                if (loanAmount >= EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W) {

                                    LoanCreditInfoSocialHisDO newLoanCreditInfoSocialHisDO = new LoanCreditInfoSocialHisDO();

                                    newLoanCreditInfoSocialHisDO.setCustomerId(e.getId());
                                    newLoanCreditInfoSocialHisDO.setCreditApplyTime(new Date());
                                    newLoanCreditInfoSocialHisDO.setCreditApplyUserId(loginUser.getId());
                                    newLoanCreditInfoSocialHisDO.setCreditApplyUserName(loginUser.getName());

                                    createLoanCreditInfoSocialHisDO(newLoanCreditInfoSocialHisDO);
                                }

                            });
                }
            }


            // 客户征信打回类型
            final Byte[] enableType = {null};
            customers.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        if (BaseConst.K_YORN_YES.equals(e.getEnable()) && null != e.getEnableType()) {
                            enableType[0] = e.getEnableType();
                            return;
                        }
                    });

            // 第2+次    过滤出：当前正在查询银行/社会征信的客户
            if (enableType[0]!=null)
            {
                customers = filterCustomers_reject(customers, enableType[0], false);
            }

            if (!CollectionUtils.isEmpty(customers)) {

                customers.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {

//                            Byte enableType_ = e.getEnableType();
                            Byte enableType_ = enableType[0];

                            // 银行增信打回
                            if (ENABLE_TYPE_BANK.equals(enableType_)) {

                                // 银行征信查询记录
                                LoanCreditInfoBankHisDO newLoanCreditInfoBankHisDO = new LoanCreditInfoBankHisDO();

                                newLoanCreditInfoBankHisDO.setCustomerId(e.getId());
                                newLoanCreditInfoBankHisDO.setCreditApplyTime(new Date());
                                newLoanCreditInfoBankHisDO.setCreditApplyUserId(loginUser.getId());
                                newLoanCreditInfoBankHisDO.setCreditApplyUserName(loginUser.getName());

                                createLoanCreditInfoBankHisDO(newLoanCreditInfoBankHisDO);

                            }
                            // 社会增信打回
                            else if (ENABLE_TYPE_SOCIAL.equals(enableType_)) {

                                // 社会征信查询记录
                                LoanCreditInfoSocialHisDO newLoanCreditInfoSocialHisDO = new LoanCreditInfoSocialHisDO();

                                newLoanCreditInfoSocialHisDO.setCustomerId(e.getId());
                                newLoanCreditInfoSocialHisDO.setCreditApplyTime(new Date());
                                newLoanCreditInfoSocialHisDO.setCreditApplyUserId(loginUser.getId());
                                newLoanCreditInfoSocialHisDO.setCreditApplyUserName(loginUser.getName());

                                createLoanCreditInfoSocialHisDO(newLoanCreditInfoSocialHisDO);

                            }
                            // 增信增补打回
                            else if (ENABLE_TYPE_CREDIT_SUPPLEMENT.equals(enableType_)) {

                                createCreditInfoHis_CreditApply_First(e, loanAmount, loginUser);
                            }

                        });
            }

        }
    }

    /**
     * 获取订单所有客户 –> 需要查询征信的客户        过滤：无需查询征信的客户-[紧急联系人]
     *
     * @param principalCustId
     * @return
     */
    private List<LoanCustomerDO> getAllCustomer(Long principalCustId) {

        List<LoanCustomerDO> customers = loanCustomerDOMapper.listByPrincipalCustIdAndType(principalCustId, null, VALID_STATUS);

        // 过滤[紧急联系人]
        customers = filterCustomers_EmergencyContact(customers);

        return customers;
    }

    /**
     * 过滤[紧急联系人] and 特殊关联人
     *
     * @param customers
     * @return
     */
    private List<LoanCustomerDO> filterCustomers_EmergencyContact(List<LoanCustomerDO> customers) {

        if (!CollectionUtils.isEmpty(customers))
        {

            customers = customers.stream()
                    .filter(Objects::nonNull)
                    .filter(e -> !CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType()) && !CUST_TYPE_SPECIAL_CONTACT.equals(e.getCustType()))
                    .collect(Collectors.toList());
        }

        return customers;
    }

    /**
     * 创建征信查询历史记录   -->     customer-first
     *
     * @param loanCustomerDO
     * @param loanAmount
     * @param loginUser
     */
    private void createCreditInfoHis_CreditApply_First(LoanCustomerDO loanCustomerDO, Byte loanAmount, EmployeeDO loginUser) {

        // 银行征信查询记录
        LoanCreditInfoBankHisDO newLoanCreditInfoBankHisDO = new LoanCreditInfoBankHisDO();

        newLoanCreditInfoBankHisDO.setCustomerId(loanCustomerDO.getId());
        newLoanCreditInfoBankHisDO.setCreditApplyTime(new Date());
        newLoanCreditInfoBankHisDO.setCreditApplyUserId(loginUser.getId());
        newLoanCreditInfoBankHisDO.setCreditApplyUserName(loginUser.getName());

        createLoanCreditInfoBankHisDO(newLoanCreditInfoBankHisDO);


        // 社会征信查询记录 (>=13万)
        if (loanAmount >= EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W) {

            LoanCreditInfoSocialHisDO newLoanCreditInfoSocialHisDO = new LoanCreditInfoSocialHisDO();

            newLoanCreditInfoSocialHisDO.setCustomerId(loanCustomerDO.getId());
            newLoanCreditInfoSocialHisDO.setCreditApplyTime(new Date());
            newLoanCreditInfoSocialHisDO.setCreditApplyUserId(loginUser.getId());
            newLoanCreditInfoSocialHisDO.setCreditApplyUserName(loginUser.getName());

            createLoanCreditInfoSocialHisDO(newLoanCreditInfoSocialHisDO);
        }
    }


    @Override
    public void saveCreditInfoHis_BankCreditRecord(Long principalCustId) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");

        List<LoanCustomerDO> customers = getAllCustomer(principalCustId);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        // 过滤出：银行征信还未提交过的客户
        customers = filterCustomers_bank(customers);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        EmployeeDO loginUser = getLoginUser();

        // 银行征信 - 提交时间/人
        customers.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    LoanCreditInfoBankHisDO loanCreditInfoBankHisDO = new LoanCreditInfoBankHisDO();

                    loanCreditInfoBankHisDO.setCustomerId(e.getId());
                    loanCreditInfoBankHisDO.setBankCreditRecordUserId(loginUser.getId());
                    loanCreditInfoBankHisDO.setBankCreditRecordUserName(loginUser.getName());
                    loanCreditInfoBankHisDO.setBankCreditRecordTime(new Date());

                    updateLoanCreditInfoBankHisDOByCustomerId(loanCreditInfoBankHisDO);
                });

    }


    @Override
    public void saveCreditInfoHis_SocialCreditRecord(Long principalCustId) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");

        List<LoanCustomerDO> customers = getAllCustomer(principalCustId);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        // 过滤出：当前正在查询社会征信的客户
        customers = filterCustomers_social(customers);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        EmployeeDO loginUser = getLoginUser();

        // 社会征信 - 提交时间/人
        customers.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    LoanCreditInfoSocialHisDO loanCreditInfoSocialHisDO = new LoanCreditInfoSocialHisDO();

                    loanCreditInfoSocialHisDO.setCustomerId(e.getId());
                    loanCreditInfoSocialHisDO.setSocialCreditRecordUserId(loginUser.getId());
                    loanCreditInfoSocialHisDO.setSocialCreditRecordUserName(loginUser.getName());
                    loanCreditInfoSocialHisDO.setSocialCreditRecordTime(new Date());

                    updateLoanCreditInfoSocialHisDOByCustomerId(loanCreditInfoSocialHisDO);
                });
    }

    @Override
    public void saveCreditInfoHis_BankCreditReject(Long principalCustId, String info, boolean isAutoTask) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");

        List<LoanCustomerDO> customers = getAllCustomer(principalCustId);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        // 过滤出：被打回的客户
        customers = filterCustomers_reject(customers, ENABLE_TYPE_BANK, false);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        EmployeeDO loginUser = getLoginUser(isAutoTask);

        customers.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    LoanCreditInfoBankHisDO loanCreditInfoBankHisDO = new LoanCreditInfoBankHisDO();

                    // 银行征信打回
                    loanCreditInfoBankHisDO.setCustomerId(e.getId());
                    loanCreditInfoBankHisDO.setBankCreditRejectTime(new Date());
                    loanCreditInfoBankHisDO.setBankCreditRejectUserId(loginUser.getId());
                    loanCreditInfoBankHisDO.setBankCreditRejectUserName(loginUser.getName());
                    loanCreditInfoBankHisDO.setBankCreditRejectInfo(info);

                    updateLoanCreditInfoBankHisDOByCustomerId(loanCreditInfoBankHisDO);
                });
    }

    @Override
    public void saveCreditInfoHis_BankCreditReject_SingleCustomer(Long customerId, String info, boolean isAutoTask) {

        EmployeeDO loginUser = getLoginUser(isAutoTask);

        LoanCreditInfoBankHisDO loanCreditInfoBankHisDO = new LoanCreditInfoBankHisDO();

        // 银行征信打回
        loanCreditInfoBankHisDO.setCustomerId(customerId);
        loanCreditInfoBankHisDO.setBankCreditRejectTime(new Date());
        loanCreditInfoBankHisDO.setBankCreditRejectUserId(loginUser.getId());
        loanCreditInfoBankHisDO.setBankCreditRejectUserName(loginUser.getName());
        loanCreditInfoBankHisDO.setBankCreditRejectInfo(info);

        updateLoanCreditInfoBankHisDOByCustomerId(loanCreditInfoBankHisDO);
    }

    @Override
    public void saveCreditInfoHis_BankCreditResult(Long customerId, Byte creditResult) {

        LoanCreditInfoBankHisDO loanCreditInfoBankHisDO = new LoanCreditInfoBankHisDO();

        // 银行征信结果
        loanCreditInfoBankHisDO.setCustomerId(customerId);
        loanCreditInfoBankHisDO.setBankCreditResult(creditResult);

        updateLoanCreditInfoBankHisDOByCustomerId(loanCreditInfoBankHisDO);
    }

    @Override
    public void saveCreditInfoHis_SocialCreditResult(Long customerId, Byte creditResult) {

        LoanCreditInfoSocialHisDO loanCreditInfoSocialHisDO = new LoanCreditInfoSocialHisDO();

        // 社会征信结果
        loanCreditInfoSocialHisDO.setCustomerId(customerId);
        loanCreditInfoSocialHisDO.setSocialCreditResult(creditResult);

        updateLoanCreditInfoSocialHisDOByCustomerId(loanCreditInfoSocialHisDO);
    }

    @Override
    public void saveCreditInfoHis_SocialCreditReject(Long principalCustId, String info, boolean isAutoTask) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");

        List<LoanCustomerDO> customers = getAllCustomer(principalCustId);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        // 过滤出：被打回的客户
        customers = filterCustomers_reject(customers, ENABLE_TYPE_SOCIAL, false);
        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        EmployeeDO loginUser = getLoginUser(isAutoTask);

        EmployeeDO finalLoginUser = loginUser;
        customers.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    LoanCreditInfoSocialHisDO loanCreditInfoSocialHisDO = new LoanCreditInfoSocialHisDO();

                    // 社会征信打回
                    loanCreditInfoSocialHisDO.setCustomerId(e.getId());
                    loanCreditInfoSocialHisDO.setSocialCreditRejectTime(new Date());
                    loanCreditInfoSocialHisDO.setSocialCreditRejectUserId(finalLoginUser.getId());
                    loanCreditInfoSocialHisDO.setSocialCreditRejectUserName(finalLoginUser.getName());
                    loanCreditInfoSocialHisDO.setSocialCreditRejectInfo(info);

                    updateLoanCreditInfoSocialHisDOByCustomerId(loanCreditInfoSocialHisDO);
                });
    }

    /**
     * 过滤出：当前被打回至[征信查询]的客户
     *
     * @param customers
     * @param enableType 1-银行打回； 2-社会打回； 3-征信增补打回；
     * @param checkFirst 是否校验 是第一次查询
     * @return
     */
    private List<LoanCustomerDO> filterCustomers_reject(List<LoanCustomerDO> customers, Byte enableType, boolean checkFirst) {
        Preconditions.checkNotNull(enableType, "征信打回类型不能为空");

        // 需要校验是否第一次查询
        if (checkFirst) {

            List<LoanCustomerDO> customerDOS = loanCustomerDOMapper.listByPrincipalCustIdAndType(customers.get(0).getPrincipalCustId(), null, VALID_STATUS);

            boolean isFirst = false;
            if (!CollectionUtils.isEmpty(customerDOS)) {

                customerDOS = customerDOS.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> !(BaseConst.K_YORN_NO.equals(e.getEnable()) && null == e.getEnableType()))
                        .collect(Collectors.toList());

                // 均为：enable=0  EnableType=null
                if (CollectionUtils.isEmpty(customerDOS)) {
                    // 则为：第一次查询征信
                    isFirst = true;
                }
            }

            // 是第一次查询征信
            if (isFirst) {

                // 第一次     不过滤

            } else {

                // 第2+次     过滤出：当前正在查询银行征信的客户
                customers = customers.stream()
                        .filter(Objects::nonNull)
                        .filter(e -> enableType.equals(e.getEnableType()) && BaseConst.K_YORN_YES.equals(e.getEnable()))
                        .collect(Collectors.toList());
            }

        } else {

            // 第2+次     过滤出：当前正在查询银行征信的客户
            customers = customers.stream()
                    .filter(Objects::nonNull)
                    .filter(e -> enableType.equals(e.getEnableType()) && BaseConst.K_YORN_YES.equals(e.getEnable()))
                    .collect(Collectors.toList());
        }

        return customers;
    }

    /**
     * 过滤出：银行征信还未提交过的客户
     *
     * @param customers
     * @return
     */
    private List<LoanCustomerDO> filterCustomers_bank(List<LoanCustomerDO> customers) {

        if (!CollectionUtils.isEmpty(customers)) {

            List<LoanCustomerDO> filterCustomers = customers.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        Long customerId = e.getId();
                        LoanCreditInfoBankHisDO last = loanCreditInfoBankHisDOMapper.lastByCustomerId(customerId);
                        Preconditions.checkNotNull(last, "客户银行征信历史丢失！客户ID：" + customerId);

                        // 银行征信还未提交过
                        Date bankCreditRecordTime = last.getBankCreditRecordTime();
                        if (bankCreditRecordTime == null) {
                            return e;
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return filterCustomers;
        }

        return null;
    }

    /**
     * 过滤出：社会征信还未提交过的客户
     *
     * @param customers
     * @return
     */
    private List<LoanCustomerDO> filterCustomers_social(List<LoanCustomerDO> customers) {

        if (!CollectionUtils.isEmpty(customers)) {

            List<LoanCustomerDO> filterCustomers = customers.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        Long customerId = e.getId();
                        LoanCreditInfoSocialHisDO last = loanCreditInfoSocialHisDOMapper.lastByCustomerId(customerId);
                        Preconditions.checkNotNull(last, "客户社会征信历史丢失！客户ID：" + customerId);

                        // 社会征信还未提交过
                        Date socialCreditRecordTime = last.getSocialCreditRecordTime();
                        if (socialCreditRecordTime == null) {
                            return e;
                        }

                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return filterCustomers;
        }

        return null;
    }

    private EmployeeDO getLoginUser() {

        EmployeeDO loginUser = SessionUtils.getLoginUser();

        return loginUser;
    }

    private EmployeeDO getLoginUser(boolean isAutoTask) {

        EmployeeDO loginUser = null;

        if (isAutoTask) {

            loginUser = new EmployeeDO();

            loginUser.setId(AUTO_EMPLOYEE_ID);
            loginUser.setName(AUTO_EMPLOYEE_NAME);

        } else {

            loginUser = SessionUtils.getLoginUser();
        }

        return loginUser;
    }

    private void createLoanCreditInfoBankHisDO(LoanCreditInfoBankHisDO newLoanCreditInfoBankHisDO) {

        loanCreditInfoBankHisDOMapper.insertSelective(newLoanCreditInfoBankHisDO);
    }

    private void createLoanCreditInfoSocialHisDO(LoanCreditInfoSocialHisDO newLoanCreditInfoSocialHisDO) {

        loanCreditInfoSocialHisDOMapper.insertSelective(newLoanCreditInfoSocialHisDO);
    }

    private void updateLoanCreditInfoBankHisDOByCustomerId(LoanCreditInfoBankHisDO loanCreditInfoBankHisDO) {
        Preconditions.checkNotNull(loanCreditInfoBankHisDO, "customerId不能为空");
        Preconditions.checkNotNull(loanCreditInfoBankHisDO.getCustomerId(), "customerId不能为空");

        LoanCreditInfoBankHisDO last = loanCreditInfoBankHisDOMapper.lastByCustomerId(loanCreditInfoBankHisDO.getCustomerId());
        if (null != last) {

            loanCreditInfoBankHisDO.setId(last.getId());
            loanCreditInfoBankHisDOMapper.updateByPrimaryKeySelective(loanCreditInfoBankHisDO);
        }
    }

    private void updateLoanCreditInfoSocialHisDOByCustomerId(LoanCreditInfoSocialHisDO loanCreditInfoSocialHisDO) {
        Preconditions.checkNotNull(loanCreditInfoSocialHisDO, "customerId不能为空");
        Preconditions.checkNotNull(loanCreditInfoSocialHisDO.getCustomerId(), "customerId不能为空");

        LoanCreditInfoSocialHisDO last = loanCreditInfoSocialHisDOMapper.lastByCustomerId(loanCreditInfoSocialHisDO.getCustomerId());
        if (null != last) {

            loanCreditInfoSocialHisDO.setId(last.getId());
            loanCreditInfoSocialHisDOMapper.updateByPrimaryKeySelective(loanCreditInfoSocialHisDO);
        }
    }
}