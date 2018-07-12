package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BankRecordVO {

    private Long bankFileListId;

    private Long customerId;

    private Long orderId;

    private String areaId;

    private String platNo;

    private String guarantyUnit;

    private Date opencardDate;

    private String cardNumber;

    private String name;

    private String cardType;

    private String credentialNo;

    private String hairpinFlag;

    private String accountStatement;

    private String repayDate;

    private Date gmtGreate;

    private Byte status;

    private BigDecimal cardBalance;

    private BigDecimal optimalReturn;

    private Integer cumulativeBreachNumber;

    private Integer consecutiveBreachNumber;

    private String runBank;

    private String instalmentTypes;

    private BigDecimal instalmentAmount;

    private Integer sumNumber;

    private BigDecimal sumAmount;

    private String reminders;

    private Byte isCustomer;

    private Date batchDate;
}
