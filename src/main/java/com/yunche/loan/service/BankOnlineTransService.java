package com.yunche.loan.service;

public interface BankOnlineTransService {

    public void registerransStatus(Long orderId,String transCode,String status);

    public Boolean check(Long orderId,String transCode);

    public Void subActionTimes(Long orderId);

    public Void addActionTimes(Long orderId);

}
