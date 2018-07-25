package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/20
 */
@Data
public class InfoSupplementVO {
    /**
     * 资料增补单ID
     */
    private Long supplementOrderId;
    /**
     * 单号
     */
    private String orderId;
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
     * 资料增补类型  (1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;6-;)
     */
    private Byte supplementType;
    /**
     * 增补类型文本值  (1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;6-;)
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
     * 增补单生成日期
     */
    private Date supplementStartDate;

    private String remark;
    /**
     * 增补文件列表
     */
    private String files;

    /**
     * 主贷人
     */
    private CustomerFile principalLender;
    /**
     * 共贷人列表
     */
//    private List<CustomerFile> commonLenderList;
    /**
     * 担保人列表
     */
//    private List<CustomerFile> guarantorList;

    /**
     * 紧急联系人列表
     */
//    private List<CustomerFile> emergencyContactList;

    @Data
    public static class CustomerFile {
        /**
         * 客户ID
         */
        private Long customerId;
        /**
         * 客户名称
         */
        private String customerName;
        /**
         * 文件分类 URL列表
         */
        private List<FileVO> files;
    }
}
