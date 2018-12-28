package com.yunche.loan.service;

import com.yunche.loan.domain.entity.PartnerRelaDistributorDO;

import java.util.List;

public interface PartnerRelaDistributorService {

    List<PartnerRelaDistributorDO> listDistributorByPartner(Long partnerId);
}
