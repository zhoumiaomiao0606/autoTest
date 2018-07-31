package com.yunche.loan.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 资料增补
 *
 * @author liuzhe
 * @date 2018/7/26
 */
@Data
public class InfoSupplementVO2 {
    /**
     * 增补单ID
     */
    private Long supplementOrderId;
    /**
     * 订单ID
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
     * 手机号
     */
    private String mobile;
    /**
     * 业务员ID
     */
    private Long salesmanId;
    /**
     * 业务员名称
     */
    private String salesmanName;
    /**
     * 合伙人ID
     */
    private Long partnerId;
    /**
     * 合伙人名称
     */
    private String partnerName;
    /**
     * 合伙人-部门ID
     */
    private Long departmentId;
    /**
     * 合伙人-部门名称
     */
    private String departmentName;

    /**
     * 贷款银行
     */
    private String bankName;
    /**
     * 实际贷款金额
     */
    private BigDecimal loanAmount;
    /**
     * 贷款期数
     */
    private Integer loanTime;

    /**
     * 车型ID
     */
    private Long carDetailId;


    /**
     * 发起人-部门
     */
    private String initiatorUnit;


    /**
     * 资料增补类型(1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;6-垫款资料缺少;)
     */
    private Byte type;
    private String typeText;
    /**
     * 资料增补内容
     */
    private String content;
    /**
     * 资料增补说明
     */
    private String info;
    /**
     * 备注
     */
    private String remark;
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
     * 增补人ID
     */
    private Long supplementerId;
    /**
     * 增补人name
     */
    private String supplementerName;
    /**
     * 发起增补时间
     */
    private String startTime;
    /**
     * 增补提交时间
     */
    private String endTime;


    @JsonIgnore
    private transient Long fileId;
    @JsonIgnore
    private transient Byte fileType;
    @JsonIgnore
    private transient String fileTypeText;
    @JsonIgnore
    private transient String filePath;

    /**
     * 文件路径列表
     */
    private List<FileVO2> files;
}
