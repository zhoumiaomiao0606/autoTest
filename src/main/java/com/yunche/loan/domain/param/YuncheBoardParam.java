package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.YuncheBoardDO;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class YuncheBoardParam extends YuncheBoardDO
{
    @NotNull
    private Integer pageIndex = 1;
    @NotNull
    private Integer pageSize = 20;
}
