package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

public enum  RelationEnum {

    //与主贷人关系：0-本人;1-配偶;2-父母;3-子女;4-兄弟姐妹;5-亲戚;6-朋友;7-同学;8-同事;9-其它;10-反担保

    ONESELF(new Byte("0"),"本人"),
    SPOUSE(new Byte("1"),"配偶"),
    PARENT(new Byte("2"),"父母"),
    CHILDREN(new Byte("3"),"子女"),
    SIBLING(new Byte("4"),"兄弟姐妹"),
    RELATIVE(new Byte("5"),"亲戚"),
    FRIEND(new Byte("6"),"朋友"),
    CLASSMATE(new Byte("7"),"同学"),
    COLLEAGUE(new Byte("8"),"同事"),
    OTHER(new Byte("9"),"其他");





    @Getter
    @Setter
    private Byte key;
    @Getter
    @Setter
    private String value;


    RelationEnum(Byte key, String value) {
        this.key = key;
        this.value=value;
    }

    public static String getValueByKey(Byte key) {

        for (RelationEnum e : RelationEnum.values()) {
            if (e.key.toString().equals(key.toString())) {
                return e.value;
            }
        }
        return "其他";
    }

}
