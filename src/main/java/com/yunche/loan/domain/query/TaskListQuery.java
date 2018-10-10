package com.yunche.loan.domain.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

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

    Long employeeId;

    Long telephoneVerifyLevel;

    Long collectionLevel;

    Long financeLevel;

    Long maxGroupLevel;

    Long financeApplyLevel;

    Long refundApplyLevel;

    Long materialSupplementLevel;

    Set<String> juniorIds = Sets.newHashSet();

    @NotEmpty
    private String taskDefinitionKey;

    /**
     * ----------------------------------------------------------------------------------
     * 正常：
     * 1-已提交;  2-未提交;  3-已打回;     0-全部;
     * ----------------------------------------------------------------------------------
     * <p>
     * <p>
     * <p>
     * ----------------------------------------------------------------------------------
     * 资料流转：
     * 1-已提交;  [2-未提交 ===拆分为===>（21-待邮寄;  22-待接收;）]   3-已打回;     0-全部;
     * ----------------------------------------------------------------------------------
     */
    private Integer taskStatus;
    private Integer taskStatus_;

    /**
     * 订单总状态
     */
    private Integer orderStatus;

    private String transCode;

    //查询条件
    private boolean fuse = false;

    private boolean pull = true;


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

    private String carType;//车辆类型

    private String partnerId;//合伙人团队

    private String loanTime;//贷款期限

    private String startOrderGmtCreate;//创建时间

    private String endOrderGmtCreate;//创建时间

    private String startLoanGmtCreate;//贷款时间

    private String endLoanGmtCreate;//贷款时间

    private String startPrintGmtCreate;

    private String endPrintGmtCreate;

    private String startRemitGmtCreate;

    private String endRemitGmtCreate;
    // 弃单时间
    private String startCancelGmtCreate;
    private String endCancelGmtCreate;

    private String minRemitAmount;

    private String maxRemitAmount;

    private String serialStatus;

    private String bankOpenCardOrder;

    private String relevanceCustomerName;

    private String isStraighten;
    private String sendee;
    private String isRepayment;

    private String lendStatus;//出借状态 （用于第三方过桥资金）

    private String startLendDate;//借款时间

    private String endLendDate;//

    private String startRepayDate;//还款时间

    private String endRepayDate;

    /**
     * 区域合伙人ID列表
     */
    private List<Long> bizAreaIdList = Lists.newArrayList();
    /**
     * 银行name列表
     */
    private List<String> bankList = Lists.newArrayList();

    private List<Long> bankInterfaceSerialOrderidList = Lists.newArrayList();

    // 资料流转-节点类型
    private Byte dataFlowType;
    // 资料流转-类型列表
    private List<String> dataFlowTypeList = Lists.newArrayList();
    // 资料流转-节点列表
    private Set<String> dataFlowNodeSet = Sets.newHashSet();

    public String getIsStraighten() {
        if (StringUtils.isBlank(isStraighten)) {
            return null;
        }
        return isStraighten;
    }

    public String getSendee() {
        if (StringUtils.isBlank(sendee)) {
            return null;
        }
        return sendee;
    }

    public String getIsRepayment() {
        if (StringUtils.isBlank(isRepayment)) {
            return null;
        }
        return isRepayment;
    }

    public String getCustomer() {
        if (StringUtils.isBlank(customer)) {
            return null;
        }
        return customer;
    }

    public Integer getTaskStatus_() {
        if (null != taskStatus_) {
            return taskStatus_;
        }
        this.taskStatus_ = taskStatus;
        return taskStatus_;
    }
}
