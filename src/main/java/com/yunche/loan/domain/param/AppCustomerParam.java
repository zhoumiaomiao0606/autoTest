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
public class AppCustomerParam {

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

    private String companyAddress;

    private String companyPhone;
    /**
     * 月收入
     */
    private BigDecimal monthIncome;

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
    /**
     * 邮编
     */
    private String postcode;
    /**
     * 工作年限
     */
    private Long workingYears;
    /**
     * 职务
     */
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

    private Long floorSpace;

    private Byte houseOwnerRelation;

    private String bankCardTransmitAddress;

    private String bankCardTransmitPostcode;

    private Long familyPersonNum;

    private BigDecimal debtRatio;

    private Byte houseCertificateType;

    private Byte bankCardTransmitAddressType;

    private String companyPostcode;

    private String elementarySchool;

    private String professional;

    private List<FileVO> files = Collections.EMPTY_LIST;
}
