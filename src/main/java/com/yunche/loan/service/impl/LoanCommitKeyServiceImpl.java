package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.LoanTelephoneVerifyParam;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.LoanCommitKeyService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.LoanTelephoneVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.yunche.loan.config.constant.LoanProcessEnum.COMMIT_KEY;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;

/**
 * @author liuzhe
 * @date 2018/11/7
 */
@Service
public class LoanCommitKeyServiceImpl implements LoanCommitKeyService
{

    private static final Byte UNCOLLECTEDKEY = 2;

    private static final Byte HASCOLLECTEDKEY = 1;


    @Autowired
    private LoanProcessService loanProcessService;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private LoanTelephoneVerifyService loanTelephoneVerifyService;


    @Override
    @Transactional
    public ResultBean<Void> riskUncollected(Long orderId) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");

        // 待收钥匙新增按钮“未收，风险100%”，点击后，视同待办完成，但订单的风险承担比例改为100%

        // 1、提交任务
        ApprovalParam approvalParam = new ApprovalParam();
        approvalParam.setOrderId(orderId);
        approvalParam.setTaskDefinitionKey(COMMIT_KEY.getCode());
        approvalParam.setAction(ACTION_PASS);
        approvalParam.setKeyCollected(UNCOLLECTEDKEY);
        ResultBean<Void> approvalResult = loanProcessService.approval(approvalParam);
        Preconditions.checkArgument(approvalResult.getSuccess(), approvalResult.getSuccess());


        // 2、订单的风险承担比例改为100%      partner -> risk_bear_rate           loan_apply_competition -> risk_taking_ratio

        // 基础风险分担比例
        PartnerDO partnerDO = partnerDOMapper.queryPartnerInfoByOrderId(orderId);
        Preconditions.checkNotNull(partnerDO, "当前订单所属合伙人不存在");
        BigDecimal riskBearRate = partnerDO.getRiskBearRate();
        Preconditions.checkNotNull(riskBearRate, "合伙人基础风险分担比例不能为空");
        Preconditions.checkArgument(riskBearRate.doubleValue() <= 100,
                "合伙人基础风险分担比例不能大于100%！当前：%s%", riskBearRate.doubleValue());

        // 风险分担加成
        double riskSharingAddition = 100 - riskBearRate.doubleValue();
        LoanTelephoneVerifyParam loanTelephoneVerifyDO = new LoanTelephoneVerifyParam();
        loanTelephoneVerifyDO.setOrderId(String.valueOf(orderId));
        loanTelephoneVerifyDO.setRiskSharingAddition(BigDecimal.valueOf(riskSharingAddition));

        ResultBean<Void> riskResult = loanTelephoneVerifyService.save(loanTelephoneVerifyDO);
        Preconditions.checkArgument(riskResult.getSuccess(), riskResult.getMsg());


        return ResultBean.ofSuccess(null, "成功");
    }
}
