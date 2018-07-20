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
    HOME_VISIT_VIDEO((byte) 13, "家访视频"),
    CERTIFICATE_INCOME((byte) 14,"收入证明"),
    ACCORDING_INTERVIEW((byte) 15,"面签照"),
    HOME_VISIT_PHOTOS((byte) 16,"家访照片"),
    VEHICLE_PHOTO((byte) 17,"车辆照片"),
    OTHER_INFO((byte) 18,"其他资料"),
    INVOICE((byte) 19,"发票"),
    CERTIFICATE((byte) 20,"合格证/登记证书"),
    POLICY((byte) 21,"保单"),
    PICK_UP_CAR((byte) 22,"提车合影"),
    DRIVING_LICENSE((byte)23,"行驶证"),
    ASSESSMENT_DATA((byte)24,"评估资料"),
    PAYMENT_VOUCHER((byte)25,"打款凭证"),
    ZIP_PACK((byte)26,"zip包"),
<<<<<<< HEAD
    SPECIAL_QUOTA_APPLY((byte)27,"专项额度核定申请表"),
    OPEN_CARD_DATA((byte)28,"开卡申请表"),
=======
    SIGNATURE_VIDEO((byte)55,"签字视频"),
    INTERROGATION_VIDEO((byte)56,"问话视频"),
    CUSHION_INFORMATION((byte)57,"垫款资料"),
    ASSET_PACKAGING((byte)58,"资产包");
>>>>>>> v_1.1.4

    //0101【分期】注册登记证1
    REGISTRATION1((byte)30,"注册登记证1"),
    //0102【分期】注册登记证2
    REGISTRATION2((byte)31,"注册登记证2"),
    //0103【分期】注册登记证3
    REGISTRATION3((byte)32,"注册登记证3"),
    //0104【分期】注册登记证4
    REGISTRATION4((byte)33,"注册登记证4"),
    //0201【分期】【证件】证件本人正面
    SELF_CERTIFICATE_FRONT((byte)34,"证件本人正面"),
    //0202【分期】【证件】证件本人反面
    SELF_CERTIFICATE_REVERSE((byte)34,"证件本人反面"),
    //0203【分期】【证件】证件配偶正面
    SPOUSE_CERTIFICATE_FRONT((byte)36,"证件配偶正面"),
    //0204【分期】【证件】证件配偶反面
    SPOUSE_CERTIFICATE_REVERSE((byte)37,"证件配偶反面"),
    //0301【分期】【婚姻证明】结婚证
    MARRY_CERTIFICATE((byte)38,"结婚证"),
    //0302【分期】【婚姻证明】离婚证
    DIVORCE_CERTIFICATE((byte)39,"离婚证"),
    //0303【分期】【婚姻证明】单身证明
    BACHELORDOM_CERTIFICATE((byte)40,"单身证明"),
    //0401【分期】购车发票
    CAR_INVOICE((byte)41,"购车发票"),
    //0501【分期】上门照片1
    VISIT1((byte)42,"上门照片1"),
    //0502【分期】上门照片2
    VISIT2((byte)43,"上门照片2"),
    //0503【分期】上门照片3
    VISIT3((byte)44,"上门照片3"),
    //0601【分期】客户签字照片1
    CUSTOMER_SIGNATURE1((byte)45,"客户签字照片1"),
    //0602【分期】客户签字照片2
    CUSTOMER_SIGNATURE2((byte)46,"客户签字照片2"),
    //0701【分期】【户口本】户口本1
    HOUSEHOLD_REGISTER1((byte)47,"户口本1"),
    //0702【分期】【户口本】户口本2
    HOUSEHOLD_REGISTER2((byte)48,"户口本2"),
    //0703【分期】【户口本】户口本3
    HOUSEHOLD_REGISTER3((byte)49,"户口本3"),
    //0704【分期】【户口本】户口本4
    HOUSEHOLD_REGISTER4((byte)50,"户口本4"),
    //0801【分期】【收入证明】收入证明1
    INCOME_PROVE1((byte)51,"收入证明1"),
    //0802【分期】【收入证明】收入证明2
    INCOME_PROVE2((byte)52,"收入证明2"),
    //0901【分期】其他图片(zip包的格式)
    OTHER_ZIP((byte)53,"其他图片"),
    //0902【分期】视频面签视频资料
    VIDEO_INTERVIEW((byte)54,"视频面签视频资料"),

    SIGNATURE_VIDEO((byte)55,"签字视频"),
    INTERROGATION_VIDEO((byte)56,"问话视频"),
    CUSHION_INFORMATION((byte)57,"垫款资料"),
    ASSET_PACKAGING((byte)58,"资产包");
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

    public static String getNameByCode(byte type) {

        for (LoanFileEnum e : LoanFileEnum.values()) {
            if (e.type.equals(type)) {

                return e.name;
            }
        }
        return null;
    }
}
