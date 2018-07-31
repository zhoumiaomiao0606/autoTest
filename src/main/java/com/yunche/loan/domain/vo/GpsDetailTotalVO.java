package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import lombok.Data;


import java.util.Date;
import java.util.List;

/**
 * @author jjq
 * @date 2018/7/16
 */
@Data
public class GpsDetailTotalVO<T> {
    private List<GpsVO> gpsNum;

    private GpsDetailVO gpsDetail;

    private T info;

    private List<UniversalCreditInfoVO> credits = Lists.newArrayList();

    private List<UniversalCustomerVO> customers = Lists.newArrayList();

}
