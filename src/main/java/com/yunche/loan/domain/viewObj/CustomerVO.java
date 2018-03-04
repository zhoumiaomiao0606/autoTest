package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/25
 */
@Data
public class CustomerVO {

    private Long id;

    private String name;

    private String nation;

    private Date birth;

    private Date identityValidity;

    private String idCard;

    private String mobile;

    private Byte age;

    private Byte sex;

    private Date applyDate;

    private String address;

    private Byte marry;

    private String identityAddress;

    private String mobileArea;

    private Byte education;

    private String companyName;

    private String companyPhone;

    private Integer monthIncome;

    private Byte houseType;

    private Byte houseOwner;

    private Byte houseFeature;

    private String houseAddress;

    private String info;

    private Byte bankCreditStatus;

    private String bankCreditDetail;

    private Byte socialCreditStatus;

    private String socialCreditDetail;

    private Byte custType;

    private Long principalCustId;

    private Byte custRelation;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private List<File> files = Collections.EMPTY_LIST;

    @Data
    public static class File {
        /**
         * 文件名称
         */
        private String name;
        /**
         * 文件存储路径
         */
        private String url;
        /**
         * 是否增补:  0-否; 1-是;
         */
        private Byte isSupplement = 0;
        /**
         * 文件类型
         */
//        private Byte type;
    }
}