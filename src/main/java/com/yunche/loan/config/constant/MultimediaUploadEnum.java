package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

public enum  MultimediaUploadEnum {


    VIDEO_INTERVIEW(IDict.K_PIC_ID.VIDEO_INTERVIEW,"视频面签");


    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private String value;

    MultimediaUploadEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
