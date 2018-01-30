package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
     * 关联的部门详情
     */
    private List<Long> department;
    /**
     * 部门负责人详情
     */
    private List<Long> departmentLeader;
    /**
     * 关联的区域(城市)详情
     */
    private List<Long> area;

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
    /**
     * 团队员工人数
     */
    private Integer employeeNum;

//    private Department department;

//    @Data
//    public static class Department {
//        private Long id;
//
//        private String name;
//
//        private Integer level;
//
//        private Department child;
//    }
}
