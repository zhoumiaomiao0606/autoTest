package com.yunche.loan.service;

import com.yunche.loan.domain.param.InstallUpdateParam;


public interface AuxiliaryService {

    public void commit(String order_id);

    public void install(InstallUpdateParam param);

}
