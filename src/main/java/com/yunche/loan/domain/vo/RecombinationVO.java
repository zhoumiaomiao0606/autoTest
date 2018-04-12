package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.LoanTelephoneVerifyDO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecombinationVO<T> {

        private T info;

        private List<UniversalRelationCustomerVO> relations = new ArrayList<UniversalRelationCustomerVO>();

        private UniversalApprovalInfo current_msg;

        private UniversalApprovalInfo channel_msg;

        private UniversalApprovalInfo telephone_msg;

        private LoanTelephoneVerifyDO telephone_des;

        private UniversalLoanInfoVO loan;

        private UniversalCarInfoVO car;

        private UniversalCostDetailsVO cost;

        private UniversalRemitDetails remit;

        private UniversalHomeVisitInfoVO home;

        private UniversalSupplementInfoVO supplement;

        private List<Long> relevances = new ArrayList<Long>();

        private List<UniversalCreditInfoVO> credits = new ArrayList<UniversalCreditInfoVO>();

        private List<UniversalCustomerVO> customers = new ArrayList<UniversalCustomerVO>();

        private List<UniversalMaterialRecordVO> materials = new ArrayList<UniversalMaterialRecordVO>();

}
