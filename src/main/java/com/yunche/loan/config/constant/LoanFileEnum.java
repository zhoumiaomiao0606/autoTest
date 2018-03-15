package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuzhe
 * @date 2018/3/14
 */
public enum LoanFileEnum {
    /**
     * 文件类型：
     * 1-身份证;2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;
     * 6-驾驶证;7- 户口本;8- 银行流水;9-结婚证;10-房产证;
     * 11-定位照;12-合影;13-房子照片;14-家访视频
     */
    ID_CARD((byte) 1, "身份证"),
    ID_CARD_FRONT((byte) 2, "身份证正面"),
    ID_CARD_BACK((byte) 3, "身份证反面"),
    AUTH_BOOK((byte) 4, "授权书"),
    AUTH_BOOK_SIGN_PHOTO((byte) 5, "授权书签字照"),
    DRIVER_LICENSE((byte) 6, "驾驶证"),
    HOUSE_HOLD_BOOK((byte) 7, "户口本"),
    BANK_FLOW((byte) 8, "银行流水"),
    MARRIAGE_CERTIFICATE((byte) 9, "结婚证"),
    HOUSE_CERTIFICATE((byte) 10, "房产证"),
    LOCATION_PHOTO((byte) 11, "定位照"),
    FAMILY_PHOTO((byte) 12, "合影"),
    HOUSE_PHOTO((byte) 13, "房子照片"),
    HOME_VISIT_VIDEO((byte) 14, "家访视频");

    @Getter
    @Setter
    private Byte type;

    @Getter
    @Setter
    private String name;

    LoanFileEnum(Byte type, String name) {
        this.type = type;
        this.name = name;
    }
}
