package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class PartnerVO {

    private Long id;

    private String name;

    private String leaderName;

    private String leaderMobile;

    private String tel;

    private String fax;
    /**
     * 关联的部门
     */
    private BaseVO department;
    /**
     * 关联的区域(城市)
     */
    private BaseVO area;

    private Byte bizType;

    private Byte sign;

    private String cooperationScale;

    private BigDecimal execRate;

    private String cooperationInsuranceCompany;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private String openBankTwo;

    private String accountNameTwo;

    private String bankAccountTwo;

    private Byte payMonth;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
}
