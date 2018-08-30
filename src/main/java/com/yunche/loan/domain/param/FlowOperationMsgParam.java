package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @program: yunche-biz
 * @description: d
 * @author: Mr.WangGang
 * @create: 2018-08-28 15:03
 **/
@Data
public class FlowOperationMsgParam {

    @NotNull
    private Integer pageIndex;
    @NotNull
    private Integer pageSize;
    @NotNull

    Long readStatus;
    Long multipartType;
    Long maxGroupLevel;
    private List<Long> bizAreaIdList = Lists.newArrayList();//区域ID列表
    private List<String> bankList = Lists.newArrayList();//银行ID列表
    Set<String> juniorIds = Sets.newHashSet();

}
