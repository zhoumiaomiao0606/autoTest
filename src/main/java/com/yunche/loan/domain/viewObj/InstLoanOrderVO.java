package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class InstLoanOrderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private String orderNbr;

    private Long custId;

    private Long prodId;

    private String processInstId;

    private Long carId;

    private Integer carType;

    private Integer carPrice;

    private Boolean carKey;

    private Integer gpsNum;

    private Integer interestRate;

    private Integer loanAmount;

    private Integer firstPayAmount;

    private Integer loanRate;

    private Integer loanStage;

    private Integer bankPrincipalAmount;

    private Integer bankChargeAmount;

    private Integer totalAmount;

    private Integer firstMonthPayAmount;

    private Integer eachMonthPayAmount;

    private Long partnerId;

    private String partnerAccountName;

    private String partnerBank;

    private String partnerAccountNum;

    private Integer partnerPayType;

    private Long insuId;

    private Long salesmanId;

    private String salesmanName;

    /**
     * 贷款额度档次: 1 - 13W以下;  2 - 13至20W;  3 - 20W以上;
     */
    private Integer amountGrade;

    private Long areaId;

    private String prov;

    private String city;

    private String feature;

    private String bank;

    private Long investigatorId;

    private String investigatorName;

    private String investigateAddress;

    private String investigateContent;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    private String action;

    //    private CustBaseInfoVO custBaseInfoVO;
    private CustomerVO customerVO;

    private List<InstProcessNodeVO> processRecordList;

    private Map<String, String> todoProcessMap;
}