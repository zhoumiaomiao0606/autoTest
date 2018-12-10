package com.yunche.loan.domain.query;

import lombok.Data;

@Data
public class FinanceErrQuery extends BaseQuery{



    //业务编号
    private String orderId;
    //客户姓名
    private String userName;
    //身份证号
    private String idCard;
    //业务日期
    private String businessDate;
    //业务团队
    private String partnerId;
    //车辆类型
    private String carType;//新车、二手车

    //贷款银行
    private String  bank;

    private String  businessDateStart;
    private String  businessDateEnd;


}
