package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppLoanCarInfoParam {

    /**
     * 业务单ID
     */
    private Long orderId;

    private Long id;

    private Byte carType;

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

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

    private Long carDetailId;

    private String carDetailName;

    private Long partnerId;

    private String partnerName;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private Byte payMonth;

    private String feature;

    private Date firstRegisterDate;

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
