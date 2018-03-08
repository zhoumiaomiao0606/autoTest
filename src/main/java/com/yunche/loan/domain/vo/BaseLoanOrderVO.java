package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * 业务单基本信息
 *
 * @author liuzhe
 * @date 2018/3/2
 */
@Data
public class BaseLoanOrderVO {
    /**
     * 业务单号
     */
    private String id;
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
