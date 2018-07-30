package com.yunche.loan.service;

import com.yunche.loan.domain.param.WebSocketParam;

/**
 * @author liuzhe
 * @date 2018/6/6
 */
public interface WebSocketService {

    void waitTeam(WebSocketParam webSocketParam);

    void exitTeam(WebSocketParam webSocketParam);

    void call(WebSocketParam webSocketParam);

    void livePhotoPath(WebSocketParam webSocketParam);

    void latlon(WebSocketParam webSocketParam);
}
