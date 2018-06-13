package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.VideoFaceCallVO;
import com.yunche.loan.domain.vo.VideoFaceCustomerVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
public interface VideoFaceRoomService {

    ResultBean<Void> addQueue(Integer type, Long bankId, Long customerId);

    ResultBean<Void> exitQueue(Integer type, Long bankId, Long customerId);

    ResultBean<List<VideoFaceCustomerVO>> listCustomerInQueue(Long bankId);

    ResultBean<VideoFaceCallVO> call(Long sendUserId, Long receiveUserId);

    ResultBean<List<VideoFaceCustomerVO>> listQueue(Long bankId);

    void sendMsgToQueueUser(String bankId);
}
