package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class InstalmentUpdateParam {
    @NotEmpty
    private String order_id;
    @NotEmpty
    private BigDecimal appraisal;
    @NotNull
    private List<UniversalFileParam> files;
}
