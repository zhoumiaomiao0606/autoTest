package com.yunche.loan.domain.query;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


import javax.validation.constraints.NotNull;


@Data
public class TaskListQuery {
    /**
     * 当前页数  默认值：1
     */
    @NotNull
    private Integer pageIndex = 1;
    /**
     * 页面大小  默认值：10
     */
    @NotNull
    private Integer pageSize = 10;

    @NotBlank
    private String taskDefinitionKey;

    @NotNull
    private Integer taskStatus;


    private Integer level;

    private String orderId;//业务编号

    private String customer;//客户姓名

    private String mobile;//移动电话

    private String idCard;//身份证

    private String salesmanId;//业务员

    private String bizAreaId;//业务区域

    private String startCreditGmtCreate;//征信申请时间

    private String endCreditGmtCreate;//征信结束时间

    private String departmentName;//业务部门

    private String loanBank;//贷款银行

    private String supplementType;//增补类型

    private String loanAmountType;//贷款金额档次 1 10w以下 2 10-30w 3 30w+

    private String signType;//执行利率

    private String loanGmtCreate;//贷款时间

    private String carType;//车辆类型

    private String partnerId;//合伙人团队

    private String loanTime;//贷款期限
}
