package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/6/10
 */
@Data
public class WebSocketMsgVO {

    private String roomId;

    private Long appAnyChatUserId;

    private Long pcAnyChatUserId;
    /**
     * 排名
     */
    private Integer rank;
    /**
     * 队列总大小
     */
    private Integer totalNum;

    /**
     * 面签方式：1-人工面签;2-机器面签;
     */
    private Integer faceSign;
}
