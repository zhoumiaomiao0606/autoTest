package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.YuncheBoardDO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class YuncheBoardParam extends YuncheBoardDO
{
    @NotNull
    private Integer pageIndex = 1;
    @NotNull
    private Integer pageSize = 20;

    Long maxGroupLevel;

    /**
     * 银行name列表
     */
    private List<String> bankList = Lists.newArrayList();
}
