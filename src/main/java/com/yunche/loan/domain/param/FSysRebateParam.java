package com.yunche.loan.domain.param;

import lombok.Data;

@Data
public class FSysRebateParam {
    private Long partnerId;
    private String periods;
    private byte type;
    private int pageIndex;
    private int pageSize;
}
