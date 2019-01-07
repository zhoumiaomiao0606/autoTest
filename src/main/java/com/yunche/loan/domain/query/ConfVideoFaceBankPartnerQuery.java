package com.yunche.loan.domain.query;

import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2019/1/4
 */
@Data
public class ConfVideoFaceBankPartnerQuery extends BaseQuery {

    private Long partnerId;

    private List<Long> partnerIdList;

    private String partnerName;

    private Long bankId;
}
