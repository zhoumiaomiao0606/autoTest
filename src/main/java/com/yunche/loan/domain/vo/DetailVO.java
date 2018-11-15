package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class DetailVO
{
    private ChassisVO Chassis;

    private BasicVO Basic;

    private ElectricMotorVO ElectricMotor;

    private DrivingVO Driving;

    private TruckVO Truck;

    private BodyVO Body;

    private EngineVO Engine;

}
