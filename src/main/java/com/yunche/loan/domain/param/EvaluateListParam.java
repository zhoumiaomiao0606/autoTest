package com.yunche.loan.domain.param;

import com.google.common.collect.Sets;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class EvaluateListParam
{
    private Byte state; //0有效  --  1 无效

    private String carInfo; //0有效  --  1 无效

    @NotNull
    private Integer pageIndex = 0;
    @NotNull
    private Integer pageSize = 20;

    Set<String> juniorIds = Sets.newHashSet();

    Long maxGroupLevel;
}
