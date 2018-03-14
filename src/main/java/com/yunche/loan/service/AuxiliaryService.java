package com.yunche.loan.service;

import com.yunche.loan.domain.param.InstallUpdateParam;


public interface AuxiliaryService {

    public void commit(Long orderId);

    public void install(InstallUpdateParam param);

}
