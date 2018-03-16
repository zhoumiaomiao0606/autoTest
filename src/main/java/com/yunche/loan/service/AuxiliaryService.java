package com.yunche.loan.service;

import com.yunche.loan.domain.param.InstallUpdateParam;
import com.yunche.loan.domain.vo.GpsVO;

import java.util.List;


public interface AuxiliaryService {

    public void commit(Long orderId);

    public void install(InstallUpdateParam param);

    public List<GpsVO> query(Long orderId);

}
