package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class LegworkReimbursementVO {
    private String id;
    private String transFee;
    private String hotelFee;
    private String eatFee;
    private String busiFee;
    private String otherFee;
    private String gmtUser;
    private String gmtUserName;
    private String gmtCreate;
    private String gmtUpdateTime;
    private String taskStatus;
}
