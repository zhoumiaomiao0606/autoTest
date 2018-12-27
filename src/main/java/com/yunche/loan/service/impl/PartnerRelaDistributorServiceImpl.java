package com.yunche.loan.service.impl;

import com.yunche.loan.domain.entity.PartnerRelaDistributorDO;
import com.yunche.loan.mapper.PartnerRelaDistributorDOMapper;
import com.yunche.loan.service.PartnerRelaDistributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 合伙人-经销商
 */
@Service
public class PartnerRelaDistributorServiceImpl implements PartnerRelaDistributorService {


    @Autowired
    private PartnerRelaDistributorDOMapper partnerRelaDistributorDOMapper;
    @Override
    public List<PartnerRelaDistributorDO> listDistributorByPartner(Long partnerId) {
        return partnerRelaDistributorDOMapper.listDistributorByPartner(partnerId);
    }
}
