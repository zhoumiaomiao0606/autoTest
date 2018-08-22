package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-21 23:57
 * @description:
 **/
@Data
public class CustomerOrderVO
{
    private String order_id;

    private String customer_name;

    private String customer_id_card;

    private String partner_name;

    private String salesman_name;
}
