package com.yunche.loan.service;


import com.yunche.loan.domain.param.VehicleInformationUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

public interface VehicleInformationService {

    public RecombinationVO detail(Long orderId);

    public void update(VehicleInformationUpdateParam param);

}
