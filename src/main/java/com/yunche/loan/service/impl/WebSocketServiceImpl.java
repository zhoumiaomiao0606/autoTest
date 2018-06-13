package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.queue.VideoFaceRoomQueue;
import com.yunche.loan.config.util.MapSortUtils;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.param.WebSocketParam;
import com.yunche.loan.domain.vo.CustomerVO;
import com.yunche.loan.domain.vo.WebSocketMsgVO;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.WebSocketService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_APP;
import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_PC;
import static com.yunche.loan.config.queue.VideoFaceRoomQueue.SEPARATOR;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * 使用SimpMessagingTemplate 向浏览器发送消息
     */
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private VideoFaceRoomQueue videoFaceRoomQueue;

    @Autowired
    private LoanCustomerService loanCustomerService;


    @Override
    public void addQueue(@NonNull WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(), "bankId不能为空");
        Preconditions.checkNotNull(webSocketParam.getUserId(), "userId不能为空");
        Preconditions.checkNotNull(webSocketParam.getType(), "type不能为空");

        // webSocket 会话ID
        String wsSessionId = SessionUtils.getWebSocketSessionId();
        System.out.println(wsSessionId);

        // 加入队列排队
        videoFaceRoomQueue.addQueue(webSocketParam.getBankId(), webSocketParam.getUserId(), webSocketParam.getType(), wsSessionId);

        // 推送     -> APP /  PC
        sendMsg(webSocketParam.getBankId());

        simpMessagingTemplate.convertAndSend("/queue/wait/test/sendTo", JSON.toJSONString(webSocketParam));

        simpMessagingTemplate.convertAndSendToUser(wsSessionId, "/queue/wait/test/sendToUser", JSON.toJSONString(webSocketParam));


        // 心跳，则nothing
    }

    @Override
    public void exitQueue(@NonNull WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(), "bankId不能为空");
        Preconditions.checkNotNull(webSocketParam.getUserId(), "userId不能为空");

        // webSocket 会话ID
        String wsSessionId = SessionUtils.getWebSocketSessionId();

        // 退出队列排队
        videoFaceRoomQueue.exitQueue(webSocketParam.getBankId(), webSocketParam.getUserId(), webSocketParam.getType(), wsSessionId);

        // 推送    -> APP /  PC
        sendMsg(webSocketParam.getBankId());
    }

    @Override
    public void call(@NonNull WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getSendUserId(), "sendUserId不能为空");
        Preconditions.checkNotNull(webSocketParam.getReceiveUserId(), "receiveUserId不能为空");

        // 生成roomId
        int randomNum = new Random().nextInt(1000);
        String roomId = webSocketParam.getSendUserId() + "" + webSocketParam.getReceiveUserId() + "" + randomNum;

        // 消息对象
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setRoomId(roomId);
        webSocketMsgVO.setSendUserId(webSocketParam.getSendUserId());
        webSocketMsgVO.setReceiveUserId(webSocketParam.getReceiveUserId());

        // 推送Msg 给APP
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(webSocketParam.getReceiveUserId()),
                "/queue/call/app", JSON.toJSONString(webSocketMsgVO));

        // 推送Msg 给PC
        // 这里destination不同是为了区别userId   -->   这里的userId，来自两个不同的表：employeeId 、customerId
        simpMessagingTemplate.convertAndSendToUser(String.valueOf(webSocketParam.getSendUserId()),
                "/queue/call/pc", JSON.toJSONString(webSocketMsgVO));
    }

    /**
     * 推送Msg
     *
     * @param bankId
     */
    private void sendMsg(Long bankId) {

        // 排队客户ID列表
        List<Long> customerIdList = Lists.newArrayList();

        // APP
        sendMsgToApp(bankId, customerIdList);

        // PC
        sendMsgToPC(bankId, customerIdList);
    }

    /**
     * 推送Msg给APP
     *
     * @param bankId
     * @param customerIdList
     */
    private void sendMsgToApp(Long bankId, List<Long> customerIdList) {

        // 获取所有app端 排队用户列表        循环发送消息

        // sessionId:customerId - startTime
        Map<String, Long> sessionIdStartTimeMap_APP = videoFaceRoomQueue.listSessionInQueue(bankId, TYPE_APP);

        if (!CollectionUtils.isEmpty(sessionIdStartTimeMap_APP)) {

            // 按val排序
            Map<String, Long> sortByValueMap = MapSortUtils.sortMapByValue(sessionIdStartTimeMap_APP);

            int size = sortByValueMap.size();
            final int[] rank = {0};

            sortByValueMap.forEach((k, v) -> {
                int rankNum = rank[0]++;

                WebSocketMsgVO msg = new WebSocketMsgVO();
                msg.setRank(rankNum);
                msg.setTotalNum(size);

                // sessionId:customerId
                String[] userMsgArr = k.split(SEPARATOR);
                String sessionId = userMsgArr[0];
                String customerId = userMsgArr[1];

                // 收集客户ID列表
                customerIdList.add(Long.valueOf(customerId));

                // APP   -> 排名、总排队数          -> 点对点 推送URL：   /user/{user}/queue/addQueue
                simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/info/app", JSON.toJSONString(msg));
            });

        }
    }

    /**
     * 推送Msg给PC
     *
     * @param bankId
     * @param customerIdList 已经按照排队时间排过序了
     */
    private void sendMsgToPC(Long bankId, List<Long> customerIdList) {

        // 排队客户列表
        List<CustomerVO> customerVOList = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(customerIdList)) {

            customerIdList.stream()
                    .filter(Objects::nonNull)
                    .forEach(customerId -> {

                                // 排队客户列表
                                CustomerVO customerVO = loanCustomerService.getById(Long.valueOf(customerId));
                                customerVOList.add(customerVO);
                            }
                    );
        }

        // 获取所有pc端  列表               循环发送消息
        Map<String, Long> sessionIdStartTimeMap_PC = videoFaceRoomQueue.listSessionInQueue(bankId, TYPE_PC);

        if (!CollectionUtils.isEmpty(sessionIdStartTimeMap_PC)) {

            sessionIdStartTimeMap_PC.forEach((k, v) -> {

                // sessionId:userId
                String[] userMsgArr = k.split(SEPARATOR);
                String sessionId = userMsgArr[0];
                String userId = userMsgArr[1];

                // PC    -> 排队用户列表detail
                simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/info/pc", JSON.toJSONString(customerVOList));
            });
        }
    }

}
