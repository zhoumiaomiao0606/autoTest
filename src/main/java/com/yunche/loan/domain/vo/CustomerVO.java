package com.yunche.loan.domain.vo;

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

    private Byte custType;

    private String info;

    private Long principalCustId;

    private Byte custRelation;

    private Byte bankCreditResult;

    private String bankCreditInfo;

    private Byte socialCreditResult;

    private String socialCreditInfo;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private List<FileVO> files = Collections.EMPTY_LIST;
}