package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

public enum  MultimediaUploadEnum {


    VIDEO_INTERVIEW((byte)54,"8888");


    @Getter
    @Setter
    private Byte key;

    @Getter
    @Setter
    private String value;

    MultimediaUploadEnum(byte key, String value) {
        this.key = key;
        this.value = value;
    }
}
