package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.util.List;

@Data
public class TelephoneVerifyParam {

    @NotBlank
    private String order_id;

    @NotBlank
    private String car_gps_num;

    @NotBlank
    private String car_key;

    private String financial_cash_deposit;

    private String financial_extra_fee;

    //审核状态列表
    private List<String> actionList;
    //合伙人团队列表
    private List<Long> partnerList;
    //贷款银行列表
    private List<String> bankList;

    private  String startDate;
    private  String endDate;

    private  String openCardOrder;

    private Byte signatureType;


}
