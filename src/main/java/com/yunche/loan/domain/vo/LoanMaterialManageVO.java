package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Data
public class LoanMaterialManageVO {

    private String orderId;

    private Long materialNum;

    private Date completeDate;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;
}
