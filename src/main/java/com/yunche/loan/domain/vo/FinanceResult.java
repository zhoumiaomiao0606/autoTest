package com.yunche.loan.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FinanceResult implements Serializable
{
    private FinanceReturnFee datas;

    private String resultCode;

    private String message;
}
