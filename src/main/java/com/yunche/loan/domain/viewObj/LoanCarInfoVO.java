package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanCarInfoVO {
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

    private Date gmtCreate;

    private Date gmtModify;
}
