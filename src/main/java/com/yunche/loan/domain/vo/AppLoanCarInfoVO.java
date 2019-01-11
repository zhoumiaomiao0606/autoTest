package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanCarInfoVO {

    private Long id;

    private String salemanName;

    private Byte carType;

    private Byte evaluationType;

    private Integer gpsNum;

    private Byte carKey;
    /**
     * 车型信息
     */
    private BaseVO carDetail;
    /**
     * 合伙人收款账户信息
     */
    private PartnerAccountInfo partnerAccountInfo;
    /**
     * 备注
     */
    private String info;

    private String applyLicensePlateArea;

    private String licensePlateType;

    private String color;

    private String nowDrivingLicenseOwner;

    private Byte vehicleProperty;

    private Date firstRegisterDate;

    private Byte businessSource;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private Long applyLicensePlateAreaId;

    private String  carCategory;

    private String  vin;

    private Long second_hand_car_evaluate_id;
    private String distributorName;

    private List<LoanCarInfoVO.BaseArea> ableApplyLicensePlateAreaList = Collections.EMPTY_LIST;


    private String mileage;

    private Long cityId;

    private String cityName;



    @Data
    public static class PartnerAccountInfo {
        private Long partnerId;

        private String partnerName;

        private String openBank;

        private String accountName;

        private String bankAccount;

        private Byte payMonth;
    }
}
