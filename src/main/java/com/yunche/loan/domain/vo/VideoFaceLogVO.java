package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.VideoFaceLogDO;
import lombok.Data;

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
public class VideoFaceLogVO extends VideoFaceLogDO {

    private String orderId;

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
