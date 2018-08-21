/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 09:37
 * @description:
 **/
package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class TrailVehicleDetailVO
{
    //申请上门拖车时间
    private Date applyTrailVehicleDate;

    //拖车时间
    private Date trailVehicleDate;
    //拖车结果
    private String trailVehicleResult;
    //相关费用
    private String relationFee;
    //总代偿额-车辆回收金融


}
