package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import static com.yunche.loan.config.constant.VideoFaceConst.*;
import static com.yunche.loan.config.util.DateTimeFormatUtils.convertDateToLocalDateTime;
import static com.yunche.loan.config.util.DateTimeFormatUtils.formatter_yyyyMMdd_HHmmss;

/**
 * @author liuzhe
 * @date 2018/6/21
 */
@Data
public class VideoFaceLogVO {

    private Long id;

    private String orderId;

    private Long guaranteeCompanyId;

    private String guaranteeCompanyName;

    private Long customerId;

    private String customerName;

    private String customerIdCard;

    private String path;

    private Byte type;

    private Long auditorId;

    private String auditorName;
    /**
     * 1-通过; 2-不通过;
     */
    private Byte action;

    private String latlon;

    private String address;

    private Long carDetailId;

    private String carName;

    private BigDecimal carPrice;

    private BigDecimal expectLoanAmount;

    private BigDecimal photoSimilarityDegree;

    private Date gmtCreate;

    private Date gmtModify;

    private String info;


    private String typeVal;

    private String actionVal;

    private String gmtCreateStr;


    public String getTypeVal() {
        Byte type = getType();

        if (FACE_SIGN_TYPE_ARTIFICIAL.equals(type)) {
            return "人工面签";
        } else if (FACE_SIGN_TYPE_MACHINE.equals(type)) {
            return "机器面签";
        }

        return typeVal;
    }

    public String getActionVal() {
        Byte action = getAction();

        if (ACTION_PASS.equals(action)) {
            return "通过";
        } else if (ACTION_NOT_PASS.equals(action)) {
            return "不通过";
        }

        return actionVal;
    }

    public String getGmtCreateStr() {

        Date gmtCreate = getGmtCreate();

        LocalDateTime localDateTime = convertDateToLocalDateTime(gmtCreate);

        String gmtCreateStr = localDateTime.format(formatter_yyyyMMdd_HHmmss);

        return gmtCreateStr;
    }

}
