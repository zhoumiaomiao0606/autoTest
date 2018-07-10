package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecombinationVO<T> {

        private T info;

        private UniversalLoanFinancialPlanTempHisVO diff;

        private List<UniversalRelationCustomerVO> relations = new ArrayList<UniversalRelationCustomerVO>();

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

        private UniversalSupplementInfoVO supplement;

        private FinancialSchemeVO financial;

        private UniversalOverdueInfo overdue;

        private BankInterfaceSerialDO bankSerial;

        private LoanBaseInfoVO loanBaseInfo;

        private List<UniversalLoanRepaymentPlan>repayments = new ArrayList<UniversalLoanRepaymentPlan>();

        private List<UniversalCollectionRecord> collections = new ArrayList<UniversalCollectionRecord>();

        private List<String> relevances = new ArrayList<String>();

        private List<UniversalCreditInfoVO> credits = new ArrayList<UniversalCreditInfoVO>();

        private List<UniversalCustomerVO> customers = new ArrayList<UniversalCustomerVO>();

        private List<UniversalMaterialRecordVO> materials = new ArrayList<UniversalMaterialRecordVO>();

        List<LoanCustomerDO> emergencyContacts = Lists.newArrayList();

}
