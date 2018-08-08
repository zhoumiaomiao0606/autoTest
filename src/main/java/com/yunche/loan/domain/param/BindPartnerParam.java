package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BindPartnerParam {

    @NotNull
    private  Long id;
    @NotNull
    List<Long> partnerIds;
}
