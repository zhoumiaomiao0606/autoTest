package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuzhe
 * @date 2018/3/13
 */
public enum LoanInfoSupplementEnum {

    SUPPLEMENT_TELEPHONE_REVIEW((byte) 1, "电审增补"),

    SUPPLEMENT_DATA_REVIEW((byte) 2, "资料审核增补");

    @Getter
    @Setter
    private Byte type;

    @Getter
    @Setter
    private String name;

    LoanInfoSupplementEnum(Byte type, String name) {
        this.type = type;
        this.name = name;
    }
}
