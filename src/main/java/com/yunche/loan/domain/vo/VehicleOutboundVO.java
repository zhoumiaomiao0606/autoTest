package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.VehicleOutboundDO;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:43
 * @description:
 **/
@Data
public class VehicleOutboundVO
{
    private BaseCustomerInfoVO baseCustomerInfoVO =new BaseCustomerInfoVO();

    private VehicleOutboundInfo vehicleOutboundInfo =new VehicleOutboundInfo();

    private VehicleInfoVO vehicleInfoVO = new VehicleInfoVO();

    private List<UniversalCustomerVO> customers;
}
