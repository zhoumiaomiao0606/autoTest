package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.VehicleHandleDO;
import lombok.Data;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:38
 * @description:
 **/
@Data
public class VehicleHandleVO
{
    private BaseCustomerInfoVO baseCustomerInfoVO;

    private VehicleHandleDO vehicleHandleDO;

    private VehicleInfoVO vehicleInfoVO;

    private List<UniversalCustomerVO> customers;

/* 执行人/组
拖回日期
拖回地点
入库时间
入库地点（三级+详细地址）
客户情况
发动机号
底盘号
公里数
车辆外观
车钥匙
行驶证
登记证书
购车发票
车辆其他资料
车辆其他物品
购置税
欠养路费
养路费备注
违章费
违章费备注
车况备注
拖车费用
车辆维修费
其他费用
清收成本*/

}
