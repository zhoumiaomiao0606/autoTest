package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanCarInfoParam {
    /**
     * 业务单ID
     */
    private Long orderId;

    private Long id;

    private Long carModelId;

    private Byte carType;

    private Long partnerId;

    private Integer gpsNum;

    private Byte carKey;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private Byte payMonth;

    private String info;

    private Byte status;
}
