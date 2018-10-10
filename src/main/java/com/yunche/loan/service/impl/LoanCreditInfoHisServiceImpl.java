package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanCreditInfoHisDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.mapper.LoanCreditInfoHisDOMapper;
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

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
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
    private LoanCreditInfoHisDOMapper loanCreditInfoHisDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;


    @Override
    public void save(LoanCreditInfoHisDO loanCreditInfoHisDO) {
        Preconditions.checkNotNull(loanCreditInfoHisDO, "customerId不能为空");
        Preconditions.checkNotNull(loanCreditInfoHisDO.getCustomerId(), "customerId不能为空");

        if (null == loanCreditInfoHisDO.getId()) {

            // insert
            loanCreditInfoHisDOMapper.insertSelective(loanCreditInfoHisDO);

        } else {

            // update
            loanCreditInfoHisDOMapper.updateByPrimaryKeySelective(loanCreditInfoHisDO);
        }
    }

    @Override
    public void create(LoanCreditInfoHisDO loanCreditInfoHisDO) {

        loanCreditInfoHisDOMapper.insertSelective(loanCreditInfoHisDO);
    }

    @Override
    public void update(LoanCreditInfoHisDO loanCreditInfoHisDO) {
        Preconditions.checkNotNull(loanCreditInfoHisDO, "ID不能为空");
        Preconditions.checkNotNull(loanCreditInfoHisDO.getId(), "ID不能为空");

        loanCreditInfoHisDOMapper.updateByPrimaryKeySelective(loanCreditInfoHisDO);
    }

    @Override
    public void updateByCustomerId(LoanCreditInfoHisDO loanCreditInfoHisDO) {
        Preconditions.checkNotNull(loanCreditInfoHisDO, "customerId不能为空");
        Preconditions.checkNotNull(loanCreditInfoHisDO.getCustomerId(), "customerId不能为空");

        LoanCreditInfoHisDO loanCreditInfoHisDO_ = loanCreditInfoHisDOMapper.lastByCustomerId(loanCreditInfoHisDO.getCustomerId());
        if (null != loanCreditInfoHisDO_) {
            loanCreditInfoHisDO.setId(loanCreditInfoHisDO_.getId());
            loanCreditInfoHisDOMapper.updateByPrimaryKeySelective(loanCreditInfoHisDO);
        }
    }

    @Override
    public void saveCreditInfoHis_CreditApply(Long principalCustId) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");

        List<Long> customerIdList = loanCustomerDOMapper.listIdByPrincipalCustIdAndType(principalCustId, null, VALID_STATUS);

        if (CollectionUtils.isEmpty(customerIdList)) {
            return;
        }

        EmployeeDO loginUser = SessionUtils.getLoginUser();

        customerIdList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 征信申请
                    LoanCreditInfoHisDO loanCreditInfoHisDO = new LoanCreditInfoHisDO();

                    loanCreditInfoHisDO.setCustomerId(e);
                    loanCreditInfoHisDO.setCreditApplyTime(new Date());
                    loanCreditInfoHisDO.setCreditApplyUserId(loginUser.getId());
                    loanCreditInfoHisDO.setCreditApplyUserName(loginUser.getName());

                    create(loanCreditInfoHisDO);
                });
    }

    @Override
    public void saveCreditInfoHis_BankCreditRecord(List<LoanCustomerDO> customers) {

        if (CollectionUtils.isEmpty(customers)) {
            return;
        }

        EmployeeDO loginUser = SessionUtils.getLoginUser();

        // 银行征信查询(推送)时间
        customers.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    LoanCreditInfoHisDO loanCreditInfoHisDO = new LoanCreditInfoHisDO();

                    loanCreditInfoHisDO.setCustomerId(e.getId());
                    loanCreditInfoHisDO.setBankCreditRecordUserId(loginUser.getId());
                    loanCreditInfoHisDO.setBankCreditRecordUserName(loginUser.getName());
                    loanCreditInfoHisDO.setBankCreditRecordTime(new Date());

                    updateByCustomerId(loanCreditInfoHisDO);
                });
    }

    @Override
    public void saveCreditInfoHis_SocialCreditRecord(Long principalCustId) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");

        List<Long> customerIdList = loanCustomerDOMapper.listIdByPrincipalCustIdAndType(principalCustId, null, VALID_STATUS);

        if (CollectionUtils.isEmpty(customerIdList)) {
            return;
        }

        EmployeeDO loginUser = SessionUtils.getLoginUser();

        customerIdList.stream()
                .filter(Objects::nonNull)
                .forEach(customerId -> {

                    LoanCreditInfoHisDO loanCreditInfoHisDO = new LoanCreditInfoHisDO();

                    // 社会征信查询
                    loanCreditInfoHisDO.setCustomerId(customerId);
                    loanCreditInfoHisDO.setSocialCreditRecordTime(new Date());
                    loanCreditInfoHisDO.setSocialCreditRecordUserId(loginUser.getId());
                    loanCreditInfoHisDO.setSocialCreditRecordUserName(loginUser.getName());

                    updateByCustomerId(loanCreditInfoHisDO);
                });
    }

    @Override
    public void saveCreditInfoHis_BankCreditReject(Long principalCustId, String info, boolean isAutoTask) {
        Preconditions.checkNotNull(principalCustId, "主贷人ID不能为空");

        List<Long> customerIdList = loanCustomerDOMapper.listIdByPrincipalCustIdAndType(principalCustId, null, VALID_STATUS);

        if (CollectionUtils.isEmpty(customerIdList)) {
            return;
        }

        EmployeeDO loginUser = null;
        if (isAutoTask) {
            loginUser.setId(AUTO_EMPLOYEE_ID);
            loginUser.setName(AUTO_EMPLOYEE_NAME);
        } else {
            loginUser = SessionUtils.getLoginUser();
        }

        EmployeeDO finalLoginUser = loginUser;
        customerIdList.stream()
                .filter(Objects::nonNull)
                .forEach(customerId -> {

                    LoanCreditInfoHisDO loanCreditInfoHisDO = new LoanCreditInfoHisDO();

                    // 银行征信打回
                    loanCreditInfoHisDO.setCustomerId(customerId);
                    loanCreditInfoHisDO.setBankCreditRejectTime(new Date());
                    loanCreditInfoHisDO.setBankCreditRejectUserId(finalLoginUser.getId());
                    loanCreditInfoHisDO.setBankCreditRejectUserName(finalLoginUser.getName());
                    loanCreditInfoHisDO.setBankCreditRejectInfo(info);

                    updateByCustomerId(loanCreditInfoHisDO);
                });
    }

    @Override
    public void saveCreditInfoHis_BankCreditResult(Long customerId, Byte creditResult) {

        LoanCreditInfoHisDO loanCreditInfoHisDO = new LoanCreditInfoHisDO();

        // 银行征信结果
        loanCreditInfoHisDO.setCustomerId(customerId);
        loanCreditInfoHisDO.setBankCreditResult(creditResult);

        updateByCustomerId(loanCreditInfoHisDO);
    }

    @Override
    public void saveCreditInfoHis_SocialCreditResult(Long customerId, Byte creditResult) {

        LoanCreditInfoHisDO loanCreditInfoHisDO = new LoanCreditInfoHisDO();

        // 社会征信结果
        loanCreditInfoHisDO.setCustomerId(customerId);
        loanCreditInfoHisDO.setSocialCreditResult(creditResult);

        updateByCustomerId(loanCreditInfoHisDO);
    }
}