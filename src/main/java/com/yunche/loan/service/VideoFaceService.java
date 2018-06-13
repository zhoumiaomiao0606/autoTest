package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
public interface VideoFaceService {

    ResultBean<Object> addRoom(Long customerId, Long orderId);
}
