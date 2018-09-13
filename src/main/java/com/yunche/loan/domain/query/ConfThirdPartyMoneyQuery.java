package com.yunche.loan.domain.query;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/9/5
 */
@Data
public class ConfThirdPartyMoneyQuery extends BaseQuery {


    private Long id;

    private String name;

    private String contact;

    private String mobile;

    private BigDecimal cautionMoney;

    private BigDecimal yearRate;

    private BigDecimal singleCost;

    private Integer loanTime;

    private Long bankId;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
}
