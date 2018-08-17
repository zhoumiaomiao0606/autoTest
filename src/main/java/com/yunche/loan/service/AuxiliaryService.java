package com.yunche.loan.service;

import com.yunche.loan.domain.param.InstallUpdateParam;
import com.yunche.loan.domain.vo.GpsJimiInfoVO;
import com.yunche.loan.domain.vo.GpsVO;
import com.yunche.loan.domain.vo.GpsDetailTotalVO;

import java.util.List;


public interface AuxiliaryService {

    public void commit(Long orderId);

    public void install(InstallUpdateParam param);

    public List<GpsVO> query(Long orderId);

    public GpsDetailTotalVO detail(Long orderId);

    public GpsDetailTotalVO appDetail(Long orderId);

    public List<GpsJimiInfoVO> queryJimi(String partnerName);

    public List<GpsJimiInfoVO> queryOther(String partnerName);

    String getGpsAddress(String gpsCode);
}

