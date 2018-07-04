package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

public enum TermFileEnum {
    //0101【分期】注册登记证1
    REGISTRATION1((byte)30,"0101"),
    //0102【分期】注册登记证2
    REGISTRATION2((byte)31,"0102"),
    //0103【分期】注册登记证3
    REGISTRATION3((byte)32,"0103"),
    //0104【分期】注册登记证4
    REGISTRATION4((byte)33,"0104"),
    //0201【分期】【证件】证件本人正面
    SELF_CERTIFICATE_FRONT((byte)34,"0201"),
    //0202【分期】【证件】证件本人反面
    SELF_CERTIFICATE_REVERSE((byte)34,"0202"),
    //0203【分期】【证件】证件配偶正面
    SPOUSE_CERTIFICATE_FRONT((byte)36,"0203"),
    //0204【分期】【证件】证件配偶反面
    SPOUSE_CERTIFICATE_REVERSE((byte)37,"0204"),
    //0301【分期】【婚姻证明】结婚证
    MARRY_CERTIFICATE((byte)38,"0301"),
    //0302【分期】【婚姻证明】离婚证
    DIVORCE_CERTIFICATE((byte)39,"0302"),
    //0303【分期】【婚姻证明】单身证明
    BACHELORDOM_CERTIFICATE((byte)40,"0303"),
    //0401【分期】购车发票
    CAR_INVOICE((byte)41,"0401"),
    //0501【分期】上门照片1
    VISIT1((byte)42,"0501"),
    //0502【分期】上门照片2
    VISIT2((byte)43,"0502"),
    //0503【分期】上门照片3
    VISIT3((byte)44,"0503"),
    //0601【分期】客户签字照片1
    CUSTOMER_SIGNATURE1((byte)45,"0601"),
    //0602【分期】客户签字照片2
    CUSTOMER_SIGNATURE2((byte)46,"0602"),
    //0701【分期】【户口本】户口本1
    HOUSEHOLD_REGISTER1((byte)47,"0701"),
    //0702【分期】【户口本】户口本2
    HOUSEHOLD_REGISTER2((byte)48,"0702"),
    //0703【分期】【户口本】户口本3
    HOUSEHOLD_REGISTER3((byte)49,"0703"),
    //0704【分期】【户口本】户口本4
    HOUSEHOLD_REGISTER4((byte)50,"0704"),
    //0801【分期】【收入证明】收入证明1
    INCOME_PROVE1((byte)51,"0801"),
    //0802【分期】【收入证明】收入证明2
    INCOME_PROVE2((byte)52,"0802"),
    //0901【分期】其他图片(zip包的格式)
    OTHER_ZIP((byte)53,"0901"),
    //0902【分期】视频面签视频资料
    VIDEO_INTERVIEW((byte)54,"0902");

    @Getter
    @Setter
    private Byte key;

    @Getter
    @Setter
    private String value;

    TermFileEnum(byte key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValueByKey(byte key) {

        for (TermFileEnum e : TermFileEnum.values()) {
            if (e.key.equals(key)) {

                return e.value;
            }
        }
        return null;
    }
}
