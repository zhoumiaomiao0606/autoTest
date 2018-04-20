package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/13
 */
@Data
public class AppInfoSupplementVO {
    /**
     * 资料增补单ID
     */
    private Long supplementOrderId;
    /**
     * 单号
     */
    private String orderId;
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
     * 银行
     */
    private String bank;
    /**
     * 贷款额
     */
    private String loanAmount;
    /**
     * 按揭期限
     */
    private Integer loanTime;
    /**
     * 车型
     */
    private String carName;
    /**
     * 资料增补类型  (1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;)
     */
    private Byte supplementType;
    /**
     * 增补类型文本值  (1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;)
     */
    private String supplementTypeText;
    /**
     * 要求增补内容
     */
    private String supplementContent;
    /**
     * 增补说明
     */
    private String supplementInfo;
    /**
     * 要求增补人员
     */
    private String initiator;
    /**
     * 要求增补部门
     */
    private String initiatorUnit;
    /**
     * 增补开始日期
     */
    private Date supplementStartDate;
    /**
     * 增补完毕日期
     */
    private Date supplementEndDate;
    /**
     * 文件分类 URL列表
     */
    private List<FileVO> files;
}
