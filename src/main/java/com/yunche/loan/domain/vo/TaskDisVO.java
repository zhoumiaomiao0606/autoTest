package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class TaskDisVO {
    private String sendee;

    private String sendeeName;

    /**
     * 1：未领取；2-已领取；
     */
    private String status;
}
