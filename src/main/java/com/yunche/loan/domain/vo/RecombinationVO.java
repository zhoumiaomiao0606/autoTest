package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import lombok.Data;

import java.util.List;

@Data
public class RecombinationVO<T> {

    private T info;

    private UniversalLoanFinancialPlanTempHisVO diff;

    private UniversalLoanRefundApplyVO refund;

    private UniversalApprovalInfo current_msg;

    private UniversalApprovalInfo channel_msg;

    private UniversalApprovalInfo telephone_msg;

    private UniversalApprovalInfo loanreview_msg;

    private UniversalApprovalInfo businessreview_msg;

    private LoanTelephoneVerifyDO telephone_des;

    private UniversalLoanInfoVO loan;

    private UniversalCarInfoVO car;

    private UniversalCostDetailsVO cost;

    private UniversalRemitDetails remit;

    private UniversalHomeVisitInfoVO home;
    /**
     * 增补单
     */
    private UniversalSupplementInfoVO supplement;
    /**
     * 金融方案
     */
    private FinancialSchemeVO financial;

    private UniversalOverdueInfo overdue;

    private BankInterfaceSerialVO bankSerial;

    private LoanBaseInfoVO loanBaseInfo;
    /**
     * 资料流转
     */
    private UniversalDataFlowDetailVO dataFlow;

    /**
     * 合同归档
     */
    private UniversalMaterialManageVO materialManage;


    private List<UniversalRelationCustomerVO> relations = Lists.newArrayList();

    private List<UniversalLoanRepaymentPlan> repayments = Lists.newArrayList();

    private List<UniversalCollectionRecord> collections = Lists.newArrayList();

    private List<String> relevances = Lists.newArrayList();

    private List<UniversalCreditInfoVO> credits = Lists.newArrayList();

    private List<LoanCustomerDO> emergencyContacts = Lists.newArrayList();

    private List<UniversalCustomerVO> customers = Lists.newArrayList();

    private List<UniversalMaterialRecordVO> materials = Lists.newArrayList();

    private List<UniversalInsuranceVO> insuranceDetail = Lists.newArrayList();
}
