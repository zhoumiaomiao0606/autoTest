package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.queue.VideoFaceRoomQueue;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.CustomerVO;
import com.yunche.loan.domain.vo.VideoFaceCallVO;
import com.yunche.loan.domain.vo.VideoFaceCustomerVO;
import com.yunche.loan.service.VideoFaceRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_APP;
import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_PC;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
@Service
public class VideoFaceRoomServiceImpl implements VideoFaceRoomService {

    @Autowired
    private VideoFaceRoomQueue videoFaceRoomQueue;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @Override
    public ResultBean<Void> addQueue(Integer type, Long bankId, Long customerId) {
        Preconditions.checkNotNull(type, "类型不能为空");
        Preconditions.checkNotNull(bankId, "银行ID不能为空");
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        if (TYPE_PC.equals(type)) {

            // nothing          -坐席属消费方 不用排队

        } else if (TYPE_APP.equals(type)) {

            // app             -客户属生产者  排队
//            videoFaceRoomQueue.addQueue(bankId, customerId);
        }

        return ResultBean.ofSuccess(null, "进入房间成功");
    }

    @Override
    public ResultBean<Void> exitQueue(Integer type, Long bankId, Long customerId) {
        Preconditions.checkNotNull(type, "类型不能为空");
        Preconditions.checkNotNull(bankId, "银行ID不能为空");
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

//        videoFaceRoomQueue.exitQueue(bankId, customerId);

        return ResultBean.ofSuccess(null, "退出房间成功");
    }

    @Override
    public ResultBean<List<VideoFaceCustomerVO>> listCustomerInQueue(Long bankId) {
        Preconditions.checkNotNull(bankId, "银行ID不能为空");

//        List<CustomerVO> customerVOList = videoFaceRoomQueue.listCustomerInQueue(bankId);

//        return ResultBean.ofSuccess(customerVOList);
        return ResultBean.ofSuccess(null);
    }

    @Override
    public ResultBean<VideoFaceCallVO> call(Long sendUserId, Long receiveUserId) {
        Preconditions.checkNotNull(sendUserId, "发起方ID不能为空");
        Preconditions.checkNotNull(receiveUserId, "接收方ID不能为空");

        VideoFaceCallVO videoFaceCallVO = new VideoFaceCallVO();

        // roomId 策略：    sendUserId + "" + receiveUserId
        Long roomId = Long.valueOf(sendUserId + "" + receiveUserId);

        // 发送给 PC


        // 发送给 APP


        videoFaceCallVO.setRoomId(roomId);
        videoFaceCallVO.setSendUserId(sendUserId);
        videoFaceCallVO.setReceiverUserId(receiveUserId);

        return ResultBean.ofSuccess(videoFaceCallVO);
    }

    @Override
    public ResultBean<List<VideoFaceCustomerVO>> listQueue(Long bankId) {


        return null;
    }

    @Override
    public void sendMsgToQueueUser(String bankId) {

        // 获取当前队列下 所有websocket(user)

        // data：每个user的排名、总排队数

        // 发送data
        simpMessagingTemplate.convertAndSendToUser("userId", "/queue/app", "data");

        // 广播给发送给PC   data：排队列表变更
        simpMessagingTemplate.convertAndSend("/queue/pc/" + bankId, "data");

    }

}