package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CreateExpensesDetailParam {

    @NotNull(message = "请先选择")
    private List<Long> ids;
}
