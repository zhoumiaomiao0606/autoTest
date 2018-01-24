package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
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
    private Department department;
    /**
     * 关联的城市
     */
    private Area area;

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

    @Data
    public static class Department {
        private Long id;
        private String name;
    }

    @Data
    public static class Area {
        private Long id;
        private String name;
    }
}
