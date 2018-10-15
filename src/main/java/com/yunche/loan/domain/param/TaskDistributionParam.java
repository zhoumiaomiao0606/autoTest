package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class TaskDistributionParam {
    @NotBlank
    private String taskId;

    @NotBlank
    private String taskKey;

    private Long orderId;
}
