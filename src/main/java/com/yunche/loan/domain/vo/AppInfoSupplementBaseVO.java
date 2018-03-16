package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
@Data
public class AppInfoSupplementBaseVO extends AppLoanOrderVO {

//    private Long customerId;
//
//    private String customerName;
//
//    private String idCard;
//
//    private String mobile;
//
//    private String partnerName;
//
//    private String salesmanName;
//    /**
//     * 业务单创建时间
//     */
//    private Date orderCreateDate;


    /**
     * 增补类型：1-电审增补;  2-资料审核增补;
     */
    private Integer supplementType;
}
