package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@Data
public class ContractOverDueParam
{
    private String customerName;

    private String loanBank;

    private String orderId;

    private Long partnerId;

    private String remitTimeStart;

    private String remitTimeEnd;


    private Long maxGroupLevel;

    private Set<String> juniorIds = Sets.newHashSet();

    //@NotEmpty
   // private String taskDefinitionKey;


    /**
     * 区域合伙人ID列表
     */
    private List<Long> bizAreaIdList = Lists.newArrayList();
    /**
     * 银行name列表
     */
    private List<String> bankList = Lists.newArrayList();


    private Integer pageIndex = 1;

    private Integer pageSize = 20;
}
