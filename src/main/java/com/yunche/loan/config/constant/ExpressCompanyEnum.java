package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

public enum ExpressCompanyEnum {

    SHUNFENG(new Byte("1"),"顺丰"),
    YUANTONG(new Byte("2"),"圆通"),
    ZHONGTONG(new Byte("3"),"中通"),
    SHENTONG(new Byte("4"),"申通"),
    YUDA(new Byte("5"),"韵达"),
    BAISHI(new Byte("6"),"百世汇通"),
    TIANTIAN(new Byte("7"),"天天"),
    EMS(new Byte("8"),"EMS"),
    YOUZHEN(new Byte("9"),"中国邮政"),
    DEBANG(new Byte("10"),"德邦"),
    QUANFENFG(new Byte("11"),"全峰");



    @Getter
    @Setter
    private Byte key;
    @Getter
    @Setter
    private String value;


    ExpressCompanyEnum(Byte key, String value) {
        this.key = key;
        this.value = value;
    }


    public static String getValueByKey(Byte key) {

        for (ExpressCompanyEnum e : ExpressCompanyEnum.values()) {
            if (e.getKey().toString().equals(key.toString())) {
                return e.value;
            }
        }
        return null;
    }

    public static Byte getKeyByValue(String value) {

        for (ExpressCompanyEnum e : ExpressCompanyEnum.values()) {
            if (e.value.equals(value)) {
                return e.key;
            }
        }
        return null;
    }
}
