package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanOrderVO {
    /**
     * 业务单号
     */
    private Long id;
    /**
     * 业务员
     */
    private BaseVO salesman;
    /**
     * 客户【主贷人】
     */
    private BaseVO customer;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 当前任务节点审核状态:  0-全部;   1-已提交(审核);   2-未提交(审核);
     */
    private Byte taskStatus;
}
