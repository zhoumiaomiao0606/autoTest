package com.yunche.loan.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Data
public class YuncheBoardDO {
    private Integer id;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    //@DateTimeFormat(pattern="yyyy-MM-dd hh:mm:ss")
    private Date applyTime;

    private String applyMan;

    private String applyPartment;

    private String bank;

    private String level;

    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date effectiveTime;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private String urls;

    private List<String> appUrls;

    private List<String> appBanks;

    private Byte status;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Date publishTime;
}