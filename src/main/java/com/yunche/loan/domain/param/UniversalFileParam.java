package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UniversalFileParam {
    @NotBlank
    private String type;
    @NotNull
    private List<String> urls;
}
