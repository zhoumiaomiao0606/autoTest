package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

public enum BusitypeEnum {
    //所贷车辆类型：1-新车; 2-二手车; 3-不限;
    //0:一手车业务 907:二手车业务 909:租金业务
    NEW(new Byte("0"),"907"),
    SECOND(new Byte("1"),"909");
    //RENT(new Byte("999"),"909");


    @Getter
    @Setter
    private Byte key;
    @Getter
    @Setter
    private String value;


    BusitypeEnum(Byte key, String value) {
        this.key = key;
        this.value = value;
    }


    public static String getValueByKey(Byte key) {

        for (BusitypeEnum e : BusitypeEnum.values()) {
            if (e.getKey().toString().equals(key.toString())) {
                return e.value;
            }
        }
        return null;
    }
}
