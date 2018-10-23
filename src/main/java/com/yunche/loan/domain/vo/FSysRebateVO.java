package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FSysRebateVO {

    private String name;
    private String leaderName;
    private int  periods;
    private BigDecimal amount;
    private Long partnerId;
    private Long orderId;
}
