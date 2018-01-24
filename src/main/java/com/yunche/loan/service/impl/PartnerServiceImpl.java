package com.yunche.loan.service.impl;

import com.yunche.loan.dao.mapper.PartnerDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Service
@Transactional
public class PartnerServiceImpl {

    @Autowired
    private PartnerDOMapper partnerDOMapper;


}
