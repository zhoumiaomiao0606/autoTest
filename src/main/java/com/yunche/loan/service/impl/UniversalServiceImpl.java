package com.yunche.loan.service.impl;

import com.yunche.loan.service.UniversalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
public class UniversalServiceImpl implements UniversalService {
    @Override
    public Map customer(Long orderId) {
        return null;
    }

    @Override
    public Map customerFile(Long customerId) {
        return null;
    }

    @Override
    public Map materialRecord(Long orderId) {
        return null;
    }
}
