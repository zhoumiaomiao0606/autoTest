package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

/**
 * 资料增补单
 *
 * @author liuzhe
 * @date 2018/3/25
 */
@Data
public class LoanInfoSupplementDO {
    /**
     * 增补单ID
     */
    private Long id;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 资料增补类型(1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;6-垫款资料缺少;)
     */
    private Byte type;
    /**
     * 资料增补内容
     */
    private String content;
    /**
     * 资料增补说明
     */
    private String info;
    /**
     * 增补源头任务节点
     */
    private String originTask;
    /**
     * 增补单状态(默认值0-未执行到此节点;1-已提交;2-未提交;)
     */
    private Byte status;
    /**
     * 发起人ID
     */
    private Long initiatorId;
    /**
     * 发起人姓名
     */
    private String initiatorName;
    /**
     * 发起增补时间
     */
    private Date startTime;
    /**
     * 增补人ID
     */
    private Long supplementerId;
    /**
     * 增补人name
     */
    private String supplementerName;
    /**
     * 增补提交时间
     */
    private Date endTime;
    /**
     * 备注
     */
    private String remark;
}