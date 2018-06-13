package com.yunche.loan.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VideoFaceRoomDO {
    private Long id;

    private String name;

    private Long bankId;

    private String bankName;

    private Date gmtCreate;

    private Date gmtModify;
}