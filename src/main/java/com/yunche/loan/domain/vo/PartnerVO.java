package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.PartnerBankAccountDO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
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

    private Long leaderId;

    private String leaderName;

    private String leaderMobile;

    private String leaderIdCard;

    private String leaderEmail;

    private String leaderDingDing;

    private String tel;

    private String fax;
    /**
     * 关联的部门详情
     */
    private List<BaseVO> department;
    /**
     * 部门负责人详情
     */
    private List<BaseVO> departmentLeader;
    /**
     * 关联的区域(城市)详情
     */
    private List<BaseVO> area;

    private Byte bizType;

    private Byte sign;

    private String cooperationScale;

    private BigDecimal execRate;

    private String cooperationInsuranceCompany;

    private Byte payMonth;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
    /**
     * 团队员工人数
     */
    private Integer employeeNum;
    /**
     * 风险承担比例
     */
    private BigDecimal riskBearRate;

    /**
     * 财务合作信息列表
     */
    private List<PartnerBankAccountDO> bankAccountList = Collections.EMPTY_LIST;

    private List<BaseAreaDO> hasApplyLicensePlateArea =Collections.EMPTY_LIST;
}
