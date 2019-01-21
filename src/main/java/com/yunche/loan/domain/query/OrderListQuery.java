package com.yunche.loan.domain.query;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Created by zhouguoliang on 2018/2/8.
 */
@Data
public class OrderListQuery extends BaseQuery {

    private Long orderId;

    private String custName;

    private String custIdCard;

    /**
     * 合伙人权限
     */
    private List<Long> partnerIdList;
    /**
     * 银行权限
     */
    private List<String> bankNameList;
    /**
     * 业务员权限
     */
    private Set<String> salesmanIdList;
}
