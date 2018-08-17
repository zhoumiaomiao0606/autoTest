package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.*;
import lombok.Data;

import java.util.List;

@Data
public class LawWorksVO<T> {

    private T result;

    private List<LitigationDO> list = Lists.newArrayList();

    private ForceDO forceDO;

    private FeeRegisterDO feeRegisterDO;

    private FileInfoDO fileInfoDO;

    private List<UniversalCustomerVO> customers = Lists.newArrayList();

    private UniversalCarInfoVO car;

    private FinancialSchemeVO financial;

    private LitigationStateDO litigationStateDO;
}
