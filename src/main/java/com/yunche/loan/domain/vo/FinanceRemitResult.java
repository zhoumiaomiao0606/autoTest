package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class FinanceRemitResult
{
    private List<FinanceRemitVO> datas;

    private String resultCode;

    private String message;
}
