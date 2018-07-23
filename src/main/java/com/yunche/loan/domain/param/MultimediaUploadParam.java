package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class MultimediaUploadParam {
    @NotEmpty
    private String orderId;
}
