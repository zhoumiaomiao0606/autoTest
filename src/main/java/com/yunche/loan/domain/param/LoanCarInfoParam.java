package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.BaseVO;
import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class LoanCarInfoParam {
    /**
     * 业务单ID
     */
    private Long orderId;

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

    private Long id;

    private Long carDetailId;

    private String carDetailName;

    private Byte carType;

    private Integer gpsNum;

    private Byte carKey;

    private Long partnerId;

    private String partnerName;

    private String openBank;

    private String accountName;

    private String bankAccount;

    private Byte payMonth;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;

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
