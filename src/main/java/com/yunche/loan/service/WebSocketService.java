package com.yunche.loan.service;

import com.yunche.loan.domain.param.WebSocketParam;

/**
 * @author liuzhe
 * @date 2018/6/6
 */
public interface WebSocketService {

    void addQueue(WebSocketParam webSocketParam);

    void exitQueue(WebSocketParam webSocketParam);

    void call(WebSocketParam webSocketParam);
}
