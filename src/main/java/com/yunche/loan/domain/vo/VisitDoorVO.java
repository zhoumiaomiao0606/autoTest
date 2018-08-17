package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.*;
import lombok.Data;

import java.util.List;

@Data
public class VisitDoorVO<T> {
    private UniversalCarInfoVO car;

    private FinancialSchemeVO financial;

    private List<UniversalCustomerVO> customers = Lists.newArrayList();

    private T result;

    private VisitDoorDO visitDoorDO;

    private CollectionRecordVO collectionRecordVO;

    private int collectionNum;

    private CollectionNewInfoDO collectionNewInfoDO;

    private LitigationStateDO litigationStateDO;

    private List<UniversalCollectionRecord> collections = Lists.newArrayList();

    private List<UniversalLoanRepaymentPlan> repayments = Lists.newArrayList();

    private List<LoanApplyCompensationDO> loanApplyCompensation = Lists.newArrayList();
}
