package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class LoanCustomerDO {

    private Long id;

    private String name;

    private String nation;

    private Date birth;

    private String identityValidity;

    private String lendCard;

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
    /**
     * 单位地址
     */
    private String companyAddress;
    /**
     * 月收入
     */
    private BigDecimal monthIncome;

    private Byte houseType;

    private Byte houseOwner;

    private Byte houseFeature;

    private String houseAddress;

    private String info;

    /**
     * 客户类型: 1-主贷人;2-共贷人;3-担保人;4-紧急联系人;
     */
    private Byte custType;

    private Long principalCustId;

    /**
     * 与主贷人关系：0-本人;1-配偶;2-父母;3-子女;4-兄弟姐妹;5-亲戚;6-朋友;7-同学;8-同事;9-其它;
     */
    private Byte custRelation;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

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

    /**
     * 担保类型：1-银行担保;  2-内部担保;
     */
    private Byte guaranteeType;

    private String residenceAddress;

    private String paddingCompany;

    private String playCompany;
    /**
     * 担保关系
     */
    private String guaranteeRela;

    private String namePinyin;

    private Date checkInDate;

    private Date enrollmentDate;

    private String cardReceiveMode;

    private String cardSendAddrType;

    private String balanceChangeRemind;

    private String openEmail;

    private String email;

    private String occupation;

    private String issuingDepartment;

    private String masterCardTel;

    private String bellTel;

    private String balanceChangeTel;

    private String openCardStatus;

    private String openCardCurrStatus;

    private String billSendType;

    private String billSendAddr;

    private String cprovince;

    private String ccity;

    private String ccounty;

    private String hprovince;

    private String hcity;

    private String hcounty;

    private String openCardOrder;

    // 单位电话区号 默认'0'
    private String ctelzone;

    private Byte signatureType;

    /**
     * 征信图片标志    1：未导出 2：已导出
     */
    private Byte creditExpFlag;

    /**
     * 是否被征信打回      0-否；1-是；        默认：否
     * <p>
     * 银行/社会征信
     */
    private Byte enable;
    /**
     * 征信打回类型   1-银行征信打回； 2-社会征信打回；3-增信增补打回；
     */
    private Byte enableType;

    /**
     * 银行征信打回（拒绝）标记：0-否；1-是；
     */
    private Byte bankCreditReject;
}