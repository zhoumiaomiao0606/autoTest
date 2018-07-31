package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author jjq
 * @date
 */
@Data
public class CarLoanResultVO<T> {
    private String success;

    private String msg;

    private T data;


}
