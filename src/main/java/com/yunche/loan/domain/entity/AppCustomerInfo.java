package com.yunche.loan.domain.entity;

import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class AppCustomerInfo {

    /**
     * 业务单单号
     */
    private Long orderId;

    /**
     * 主贷人
     */
    private CustomerInfo principalLender;
    /**
     * 共贷人列表
     */
    private List<CustomerInfo> commonLenderList = Collections.EMPTY_LIST;
    /**
     * 担保人列表
     */
    private List<CustomerInfo> guarantorList = Collections.EMPTY_LIST;
    /**
     * 紧急联系人列表
     */
    private List<EmergencyContact> emergencyContactList = Collections.EMPTY_LIST;

    @Data
    public static class CustomerInfo {

        private Long id;

        private String name;
        /**
         * 与主贷人关系：0-本人;1-配偶;2-父母;3-子女;4-兄弟姐妹;5-亲戚;6-朋友;7-同学;8-同事;9-其它;
         */
        private Byte custRelation;

        private Byte sex;

        private Byte age;

        private String idCard;

        private String mobile;

        private Byte marry;

        private Byte education;
        /**
         * 现住地址
         */
        private String address;
        /**
         * 身份证地址
         */
        private String identityAddress;
        /**
         * 手机号归属地
         */
        private String mobileArea;

        private String companyName;

        private String companyAddress;

        private String companyPhone;
        /**
         * 月收入
         */
        private Integer monthIncome;
        /**
         * 房产情况:1-自有商品房有贷款;2-自有商品房无贷款;
         */
        private Byte houseType;
        /**
         * 房产所有人:1-本人所有;2-夫妻共有;
         */
        private Byte houseOwner;
        /**
         * 房产性质：1-商品房有贷款;2-商品房无贷款;
         */
        private Byte houseFeature;
        /**
         * 房产地址
         */
        private String houseAddress;
        /**
         * 银行征信结果
         */
        private Byte bankCreditResult;
        /**
         * 银行征信备注
         */
        private String bankCreditInfo;
        /**
         * 社会征信结果
         */
        private Byte socialCreditResult;
        /**
         * 社会征信备注
         */
        private String socialCreditInfo;
        /**
         * 附件列表
         */
        private List<FileVO> files = Collections.EMPTY_LIST;

//        private String nation;
//
//        private Date birth;
//
//        private Date identityValidity;
//
//        private Date applyDate;
//
//        private Byte custType;
//
//        private String info;
//
//        private Long principalCustId;
//
//
//        private Date gmtCreate;
//
//        private Date gmtModify;
//
//        private Byte status;
    }

    @Data
    public static class EmergencyContact {

        private Long id;

        private String name;

        private String mobile;
        /**
         * 与主贷人关系：0-本人;1-配偶;2-父母;3-子女;4-兄弟姐妹;5-亲戚;6-朋友;7-同学;8-同事;9-其它;
         */
        private Byte custRelation;
    }
}
