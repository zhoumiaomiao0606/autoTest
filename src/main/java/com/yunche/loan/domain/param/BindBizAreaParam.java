package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BindBizAreaParam {
    @NotNull
    private Long id;

    @NotNull
    private List<Long> bizAreaIds;
}
