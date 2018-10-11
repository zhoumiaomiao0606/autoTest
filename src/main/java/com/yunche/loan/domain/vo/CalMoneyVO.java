package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class CalMoneyVO {
    //利息
    String interest;
    //手续费
    String poundage;
    //天数
    String timeNum;
    //单笔利率
    String singleRate;
    //银行放款日期
    String bankDate;
}
