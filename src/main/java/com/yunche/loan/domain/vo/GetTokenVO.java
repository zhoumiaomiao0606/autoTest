package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author jjq
 * @date
 */
@Data
public class GetTokenVO<T> {
    private String code;

    private String message;

    private T result;


}
