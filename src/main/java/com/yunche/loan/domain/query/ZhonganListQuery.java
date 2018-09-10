package com.yunche.loan.domain.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @program: yunche-biz
 * @description: dD
 * @author: Mr.WangGang
 * @create: 2018-08-30 10:38
 **/
@Data
public class ZhonganListQuery {
    @NotNull
    private Integer pageIndex;
    @NotNull
    private Integer pageSize;
    Long maxGroupLevel;
    private List<Long> bizAreaIdList = Lists.newArrayList();//区域合伙人ID列表
    private List<String> bankList = Lists.newArrayList();//银行name列表
    Set<String> juniorIds = Sets.newHashSet();

    private Long partnerId;
}
