package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.Date;

@Data
public class DistributorParam {
    /**
     *  "carBrand": "string",
     "carDetail": "string",
     "carModel": "string",
     "carPrice": 0,
     "createDate": "2019-01-04T02:18:28.195Z",
     "createDateStr": "string",
     "creditOperatorName": "string",
     "curPage": 0,
     "id": 0,
     "idCard": "string",
     "loanTime": "string",
     "modelYear": "string",
     "name": "string",
     "orderId": "string",
     "orderStatus": 0,
     "pageSize": 0,
     "partnerId": "string",
     "partnerName": "string",
     "tenantId": "string",
     "tenantRebate": 0,
     "total": 0
     */
    private String orderId;
    private String name;
    private String idCard;
    private String creditOperatorName;
    private String partnerName;
    private Date createDate;
    private Date loanTime;
    private String orderStatus;
    private String carDetail;
    private String carModel;
    private String carBrand;
    private String carPrice;
    private String tenantRebate;
    private String tenantId;
    private String partnerId;
}
