package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.VehicleOutboundDO;
import lombok.Data;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:43
 * @description:
 **/
@Data
public class VehicleOutboundVO
{
    private BaseCustomerInfoVO baseCustomerInfoVO;

    private VehicleOutboundDO vehicleOutboundDO;

    private VehicleInfoVO vehicleInfoVO;

    private List<UniversalCustomerVO> customers;
}
