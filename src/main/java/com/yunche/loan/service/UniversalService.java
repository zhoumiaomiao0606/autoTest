package com.yunche.loan.service;

import java.util.Map;

public interface UniversalService {

    public Map customer(Long orderId);

    public Map customerFile(Long customerId);

    public Map materialRecord(Long orderId);

}
