package com.yunche.loan.domain.vo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;

/**
 * @author liuzhe
 * @date 2018/3/27
 */
@Data
public class AppTaskVO {

    private String id;

    private String salesmanId;
    private String customerId;
    private String customer;
    private String partnerId;
    private String partner;
    private String salesman;

    private String idCard;
    private String mobile;

    private String orderGmtCreate;

    private String overdueNum;
    private String taskStatus;
    private String bankId;
    private String bankName;
    private String carDetailId;
    private String carName;
    private String carPrice;
    // 贷款金额
    private String loanAmount;
    // 银行分期本金
    private String bankPeriodPrincipal;
    // 贷款比例
    private String loanRatio;

    private String currentTask;

    /**
     * 任务类型：1-未提交;  2-打回;
     */
    private String taskType;
    /**
     * 任务类型文本：1-未提交;  2-打回;
     */
    private String taskTypeText;

    /**
     * 是否可以发起【征信增补】
     */
    private Boolean canCreditSupplement;


    /**
     * 贷款比例计算
     *
     * @return
     */
    public String getLoanRatio() {

        if (StringUtils.isNotBlank(bankPeriodPrincipal) && StringUtils.isNotBlank(carPrice)) {

            double loanRatio = Double.valueOf(bankPeriodPrincipal) / Double.valueOf(carPrice);
            DecimalFormat df = new DecimalFormat("#0.00");
            String loanRatioStr = df.format(loanRatio);

            String[] ratioArr = loanRatioStr.split("\\.");
            return ratioArr[1];
        }

        return null;
    }

}
