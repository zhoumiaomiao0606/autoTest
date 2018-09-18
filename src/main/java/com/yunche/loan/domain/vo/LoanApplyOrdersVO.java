package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-17 17:13
 * @description:
 **/
@Data
public class LoanApplyOrdersVO
{
    private Long orderId;

    private String task_definition_key;

    private Date create_time;
}
