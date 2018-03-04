package com.yunche.loan.domain.viewObj;

import com.yunche.loan.domain.dataObj.CustRelaPersonInfoDO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class CustBaseInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long custId;

    private String custName;

    private String identityType;

    private String identityNumber;

    private String phone;

    private String marry;

    private Date birth;

    private String sex;

    private Integer age;

    private String nation;

    private String education;

    private String address;

    private String identityAddress;

    private Date identityValidity;

    private String companyName;

    private String companyPhone;

    private Integer bankCreditStatus;

    private String bankCreditDetail;

    private Integer socialCreditStatus;

    private String socialCreditDetail;

    private String income;

    private String houseType;

    private String houseOwner;

    private String houseFeature;

    private String houseAddress;

    private Date gmtCreate;

    private Date gmtModify;

    private String feature;

    /**
     * 共贷人/担保人/反担保人
     */
    private List<CustRelaPersonInfoDO> relaPersonList;
}