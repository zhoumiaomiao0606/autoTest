package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;


public enum LoanCustomerEnum {
    /**
     * 客户类型:
     * 1-主贷人;
     * 2-共贷人;
     * 3-担保人;
     * 4-紧急联系人;
     */
    PRINCIPAL_LENDER((byte)1,"主贷人"),
    COMMON_LENDER((byte)2,"共贷人"),
    GUARANTOR((byte)3,"担保人"),
    EMERGENCY_CONTACT((byte)4,"紧急联系人");

    @Getter
    @Setter
    private Byte type;

    @Getter
    @Setter
    private String name;

    LoanCustomerEnum(Byte type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getNameByCode(byte type) {

        for (LoanCustomerEnum e : LoanCustomerEnum.values()) {
            if (e.type.equals(type)) {

                return e.name;
            }
        }
        return null;
    }
}
