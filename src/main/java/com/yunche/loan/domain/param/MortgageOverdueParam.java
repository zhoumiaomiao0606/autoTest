package com.yunche.loan.domain.param;

import com.yunche.loan.domain.query.BaseQuery;
import lombok.Data;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-07 10:04
 * @description:
 **/
@Data
public class MortgageOverdueParam  extends BaseQuery
{
    private  String startDate;
    private  String endDate;

    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;
}
