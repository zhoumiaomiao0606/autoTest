package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

public enum  CreditEnum {
    /*
001:通过；
003:不通过；
099:退回，由于资料不全等原因退回


征信结果: 0-不通过;1-通过;2-关注;
*/

    PASS("001",new Byte("1")),
    NOPASS("003",new Byte("0")),
    BACK("099",new Byte("3"));

    @Getter
    @Setter
    private  String key;

    @Getter
    @Setter
    private  Byte value;

    CreditEnum(String key, Byte value) {
        this.key = key;
        this.value = value;

    }


    public static Byte getValueByKey(String key) {

        for (CreditEnum e : CreditEnum.values()) {
            if (e.key.equals(key)) {

                return e.value;
            }
        }
        return null;
    }
}
