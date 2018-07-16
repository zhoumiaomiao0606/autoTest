package com.yunche.loan.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BankFileListRecordDO extends BankFileListRecordDOKey {
    private Long orderId;//点单编号

    private String areaId;//地区号

    private String platNo;//平台编号

    private String guarantyUnit;//担保单位编号

    private Date opencardDate;//开卡日期

    private String cardNumber;//卡号

    private String name;//姓名

    private String cardType;//证件类型

    private String credentialNo;//证件号码

    private String hairpinFlag;//发卡标志

    private String accountStatement;//对账单日

    private String repayDate;//还款日

    private Date gmtGreate;//创建时间

    private Byte status;

    private BigDecimal cardBalance;//卡余额

    private BigDecimal optimalReturn;//最优还款额

    private Integer cumulativeBreachNumber;//累计违约次数

    private Integer consecutiveBreachNumber;//连续违约次数

    private String runBank;//经办支行

    private String instalmentTypes;//分期业务种类

    private BigDecimal instalmentAmount;//分期金额

    private Integer sumNumber;//汇总笔数

    private BigDecimal sumAmount;//汇总金额

    private String reminders;//催缴情况

    private Byte isCustomer;//是否系统客户

    private Date batchDate;//文件批次日期


}