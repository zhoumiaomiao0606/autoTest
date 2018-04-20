package com.yunche.loan.domain.vo;

import com.sun.org.apache.xpath.internal.operations.String;
import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/14
 */
@Data
public class AppLoanCustomerVO {

    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 客户名称
     */
    private String customerName;
    /**
     * 身份证
     */
    private String idCard;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 合伙人
     */
    private String partnerName;
    /**
     * 业务员
     */
    private String salesmanName;
    /**
     * 业务单创建时间
     */
    private Date orderCreateTime;
    /**
     * 任务节点
     */
    private String taskProgress;

    private Byte guaranteeType;
}
