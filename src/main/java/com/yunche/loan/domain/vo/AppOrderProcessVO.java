package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/18
 */
@Data
public class AppOrderProcessVO {
    /**
     * 业务订单号
     */
    private String orderId;
    /**
     * 客户姓名
     */
    private String customerName;
    /**
     * 身份证号码
     */
    private String idCard;
    /**
     * 贷款额
     */
    private BigDecimal loanAmount;
    /**
     * 合伙人
     */
    private String partnerName;
    /**
     * 业务员
     */
    private String salesmanName;
    /**
     * 合伙人所属云车管辖部门
     */
    private String department;
    /**
     * 贷款银行
     */
    private String bank;
    /**
     * 当前任务KEY
     */
    private String taskDefinitionKey;
    /**
     * 是否能够弃单
     */
    private Boolean canCancelTask;
    /**
     * 任务进度
     */
    private List<Task> taskList;

    @Data
    public static class Task {
        /**
         * 任务节点
         */
        private String task;
        /**
         * 操作员角色 OR 合伙人团队名称
         */
        private String userGroup;
        /**
         * 审核员
         */
        private String auditor;
        /**
         * 办理时间
         */
        private Date approvalTime;
        /**
         * 审核备注
         */
        private String approvalInfo;
        /**
         * 任务状态
         */
        private Byte taskStatus;
        /**
         * 任务状态文本
         */
        private String taskStatusText;
    }
}
