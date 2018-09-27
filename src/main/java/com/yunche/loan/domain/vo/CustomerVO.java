package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
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

    private String identityValidity;

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

    private String companyAddress;

    private BigDecimal monthIncome;

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

    private String postcode;

    private Long workingYears;

    private String duty;

    private Byte householdNature;

    private String childrenNum;

    private String reserveMobile;

    private Byte companyNature;

    private Byte industryCategory;

    private String incomeCertificateCompanyName;

    private String incomeCertificateCompanyAddress;

    private String familyAddress;

    private Long familyMobile;

    private BigDecimal familyMonthlyIncome;

    private Long feedingNum;

    private BigDecimal floorSpace;

    private Byte houseOwnerRelation;

    private String bankCardTransmitAddress;

    private String bankCardTransmitPostcode;

    private Byte houseCertificateType;

    private Byte bankCardTransmitAddressType;

    private String companyPostcode;

    private String elementarySchool;

    private String professional;

    private Long familyPersonNum;

    private BigDecimal debtRatio;

    private Byte guaranteeType;

    private String residenceAddress;//户籍地址

    private String guaranteeRela;//担保人关系

    private String namePinyin;

    private Date checkInDate;

    private Date enrollmentDate;

    private String cardReceiveMode;

    private String cardSendAddrType;

    private String balanceChangeRemind;

    private String openEmail;

    private String email;

    private String occupation;

    private String cprovince;

    private String ccity;

    private String ccounty;

    private String hprovince;

    private String hcity;

    private String hcounty;

    private String issuingDepartment;

    private List<FileVO> files = Collections.EMPTY_LIST;

    private String bankCreditNote;

    private String bankCreditStatus;

    private String ctelzone;

    /**
     * 签单类型  1-夫妻单签  2-夫妻双签  3-其他
     */
    private Byte signatureType;

    /**
     * 是否可编辑：0-否;1-是;   默认：否
     */
    private Byte enable;
}