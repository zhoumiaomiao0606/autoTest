package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.VehicleInformationDO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanCarInfoVO {
    private Long id;

    private Byte carType;

    private Integer gpsNum;

    private Byte carKey;
    /**
     * 车型信息
     * 父车型   ID层级列表
     */
    private List<BaseVO> carDetail;
    /**
     * 合伙人收款账户信息
     */
    private PartnerAccountInfo partnerAccountInfo;

    /**
     * 备注
     */
    private String info;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private String applyLicensePlateArea;

    private String licensePlateType;

    private String color;

    private String nowDrivingLicenseOwner;

    private Byte vehicleProperty;

    private Date firstRegisterDate;

    private Byte businessSource;

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
