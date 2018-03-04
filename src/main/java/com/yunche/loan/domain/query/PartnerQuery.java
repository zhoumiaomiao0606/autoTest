package com.yunche.loan.domain.query;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class PartnerQuery extends BaseQuery {

    private String name;

    private String leaderName;

    private String leaderMobile;

    private String tel;

    private String fax;
    /**
     * 关联的部门ID
     */
    private Long departmentId;
    /**
     * 关联的城市ID
     */
    private Long areaId;

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

    /**
     * 所有子级areaId（含自身）
     */
    private List<Long> cascadeChildAreaIdList;
}
