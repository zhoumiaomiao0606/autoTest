package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.BaseAreaDO;
import lombok.Data;

import java.util.Collections;
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

    //合伙人关联的上牌地
//    private List<BaseAreaDO> ableApplyLicensePlateAreaList;

    private String licensePlateType;

    private String color;

    private String nowDrivingLicenseOwner;

    private Byte vehicleProperty;

    private Date firstRegisterDate;

    private Byte businessSource;

    private BaseAreaDO hasApplyLicensePlateArea;

    private List<BaseArea> ableApplyLicensePlateAreaList = Collections.EMPTY_LIST;

    @Data
    public static class BaseArea{
        private Long provAreaId;
        private String provName;
        List<BaseAreaDO> cityList =Collections.EMPTY_LIST;
    }

    /**
     * 业务员
     */
    private String salesManName;

    /**
     * 合伙人团队
     */
    private String partnerName;
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
