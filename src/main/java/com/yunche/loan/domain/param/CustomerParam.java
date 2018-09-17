package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class CustomerParam {

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

    private String companyAddress;

    private BigDecimal monthIncome;

    private Byte houseType;

    private Byte houseOwner;

    private Byte houseFeature;

    private String houseAddress;

    private String info;

    private Byte custType;

    private Long principalCustId;

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

    private Byte guaranteeType;

    private String residenceAddress;//户籍地址

    private String guaranteeRela;//担保人关系（与主担保人关系）

    private String namePinyin;//姓名拼音

    private Date checkInDate;//住房入住日期

    private Date enrollmentDate;//入职日期

    private String cardReceiveMode;//银行卡接收方式

    private String cardSendAddrType;//银行卡寄送地址类型

    private String balanceChangeRemind;//是否开通余额变动提醒

    private String openEmail;//是否开通邮件通知

    private String email;//邮件地址
    private String cprovince;

    private String ccity;

    private String ccounty;

    private String hprovince;

    private String hcity;

    private String hcounty;

    private String issuingDepartment;

    private String ctelzone;

    private Byte signatureType;//签单类型  1-夫妻单签  2-夫妻双签  3-其他

    private List<FileVO> files = Collections.EMPTY_LIST;

    private String occupation;


    private Byte bankCreditStatus;

    private String bankCreditDetail;

    private Byte socialCreditStatus;

    private String socialCreditDetail;
}
