package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class LoanCreditInfoVO {

    private Long id;

    private Byte result;

    private String info;

    private Byte type;

    private Long customerId;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
}
