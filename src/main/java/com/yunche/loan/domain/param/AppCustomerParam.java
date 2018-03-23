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

    private List<FileVO> files = Collections.EMPTY_LIST;
}
