package com.yunche.loan.service;

import java.util.Map;

public interface UniversalService {

    public Map customer(String order_id);

    public Map customerFile(String customer_id);

    public Map materialRecord(String order_id);

}
