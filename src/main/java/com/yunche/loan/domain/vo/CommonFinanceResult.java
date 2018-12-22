package com.yunche.loan.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonFinanceResult<T> implements Serializable
{
    private T datas;

    private String resultCode;

    private String message;
}
