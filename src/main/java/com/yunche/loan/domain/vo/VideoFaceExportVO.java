package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class VideoFaceExportVO {
    //单号
    private String taskId;
    //领取时间
    private String getTime;
    //合伙人编码
    private String partnerCode;
    //团队名
    private String pName;
    //客户名
    private String cName;
    //视频问题
    private String info;
    //是否通过
    private String result;
    //通过时间
    private String finishTime;
}
