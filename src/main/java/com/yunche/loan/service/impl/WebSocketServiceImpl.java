package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.constant.LoanAmountGradeEnum;
import com.yunche.loan.config.queue.VideoFaceQueue;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.MapSortUtils;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.WebSocketParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.FaceSignConst.FACE_SIGN_MACHINE;
import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_APP;
import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_PC;
import static com.yunche.loan.config.queue.VideoFaceQueue.SEPARATOR;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    /**
     * 使用SimpMessagingTemplate 向浏览器发送消息
     */
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private VideoFaceQueue videoFaceQueue;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private CarService carService;


    @Override
    public void waitTeam(@NonNull WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(bankCache), "bank不能为空");
        Preconditions.checkNotNull(webSocketParam.getLoanAmount(), "loanAmount不能为空");
        Preconditions.checkNotNull(webSocketParam.getUserId(), "userId不能为空");
        Preconditions.checkNotNull(webSocketParam.getType(), "type不能为空");
        Preconditions.checkNotNull(webSocketParam.getAnyChatUserId(), "anyChatUserId不能为空");
        if (TYPE_APP.equals(webSocketParam.getType())) {
            Preconditions.checkNotNull(webSocketParam.getOrderId(), "orderId不能为空");
        }

        // webSocket 会话ID
        String wsSessionId = SessionUtils.getWebSocketSessionId();

        // 前置判断：是否需要排队
        boolean needWaitTeam = needWaitTeam(webSocketParam, wsSessionId);
        if (!needWaitTeam) {
            return;
        }

        // 加入队列排队
        videoFaceQueue.addQueue(webSocketParam.getBankId(), webSocketParam.getUserId(), webSocketParam.getType(),
                webSocketParam.getAnyChatUserId(), webSocketParam.getOrderId(), wsSessionId);

        // 推送     -> APP /  PC
        sendMsg(webSocketParam.getBankId());
    }

    @Override
    public void exitTeam(@NonNull WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(bankCache), "bank不能为空");
        Preconditions.checkNotNull(webSocketParam.getUserId(), "userId不能为空");
        Preconditions.checkNotNull(webSocketParam.getType(), "type不能为空");
        Preconditions.checkNotNull(webSocketParam.getAnyChatUserId(), "anyChatUserId不能为空");
        if (TYPE_APP.equals(webSocketParam.getType())) {
            Preconditions.checkNotNull(webSocketParam.getOrderId(), "orderId不能为空");
        }

        // webSocket 会话ID
        String wsSessionId = SessionUtils.getWebSocketSessionId();

        // 退出队列排队
        videoFaceQueue.exitQueue(webSocketParam.getBankId(), webSocketParam.getUserId(), webSocketParam.getType(),
                webSocketParam.getAnyChatUserId(), webSocketParam.getOrderId(), wsSessionId);

        // 推送    -> APP /  PC
        sendMsg(webSocketParam.getBankId());
    }

    @Override
    public void call(@NonNull WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(bankCache), "bank不能为空");
        Preconditions.checkNotNull(webSocketParam.getAppAnyChatUserId(), "appAnyChatUserId不能为空");
        Preconditions.checkNotNull(webSocketParam.getPcAnyChatUserId(), "pcAnyChatUserId不能为空");

        // 生成roomId
        int randomNum = new Random().nextInt(1000);
        String roomId = webSocketParam.getPcAnyChatUserId() + "" + webSocketParam.getAppAnyChatUserId() + "" + randomNum;

        // 消息对象
        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setRoomId(roomId);
        webSocketMsgVO.setPcAnyChatUserId(webSocketParam.getPcAnyChatUserId());
        webSocketMsgVO.setAppAnyChatUserId(webSocketParam.getAppAnyChatUserId());

        // wsSessionId
        String wsSessionId_app = videoFaceQueue.getWsSessionIdByAnyChatUserId(webSocketParam.getBankId(),
                webSocketParam.getAppAnyChatUserId(), TYPE_APP);
        String wsSessionId_pc = SessionUtils.getWebSocketSessionId();

        // 推送Msg 给APP
        simpMessagingTemplate.convertAndSendToUser(wsSessionId_app,
                "/queue/call/app", JSON.toJSONString(ResultBean.ofSuccess(webSocketMsgVO)));

        // 推送Msg 给PC
        simpMessagingTemplate.convertAndSendToUser(wsSessionId_pc,
                "/queue/call/pc", JSON.toJSONString(ResultBean.ofSuccess(webSocketMsgVO)));
    }

    @Override
    public void livePhotoPath(WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(bankCache), "bank不能为空");
        Preconditions.checkNotNull(webSocketParam.getPcAnyChatUserId(), "pcAnyChatUserId不能为空");
//        Preconditions.checkNotNull(webSocketParam.getLivePhotoPath(), "livePhotoPath不能为空");

        // 存储到面签记录表


        // PC端 wsSessionId
        String wsSessionId_pc = videoFaceQueue.getWsSessionIdByAnyChatUserId(webSocketParam.getBankId(),
                webSocketParam.getPcAnyChatUserId(), TYPE_PC);

        // 转发给pc端
        simpMessagingTemplate.convertAndSendToUser(wsSessionId_pc,
                "/queue/livePhoto/path/pc", JSON.toJSONString(ResultBean.ofSuccess(webSocketParam)));
    }

    @Override
    public void latlon(WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(bankCache), "bank不能为空");
        Preconditions.checkNotNull(webSocketParam.getPcAnyChatUserId(), "pcAnyChatUserId不能为空");
//        Preconditions.checkNotNull(webSocketParam.getLatlon(), "latlon不能为空");

        // 存储到面签记录表


        // PC端 wsSessionId
        String wsSessionId_pc = videoFaceQueue.getWsSessionIdByAnyChatUserId(webSocketParam.getBankId(),
                webSocketParam.getPcAnyChatUserId(), TYPE_PC);

        // 转发给pc端
        simpMessagingTemplate.convertAndSendToUser(wsSessionId_pc,
                "/queue/latlon/pc", JSON.toJSONString(ResultBean.ofSuccess(webSocketParam)));
    }

    /**
     * 是否需要排队
     *
     * @param webSocketParam
     * @param wsSessionId
     * @return
     */
    private boolean needWaitTeam(WebSocketParam webSocketParam, String wsSessionId) {

        // 若贷款银行为杭州城站支行，则进入人工面签
        if ("杭州城站支行".equals(webSocketParam.getBankName())) {

            // nothing  -> 正常排队
        }

        // 若贷款银行为台州路桥支行，则判断：
        else if ("台州路桥支行".equals(webSocketParam.getBankName())) {

            double loanAmount = webSocketParam.getLoanAmount().doubleValue();

            // a、若银行分期本金小于10万，进入机器面签
            if (loanAmount < 100000) {

                // 机器面签
                machineFace(wsSessionId);

                // 退出排队
                exitTeam(webSocketParam);

                return false;
            }

            // b、若银行分期本金大于等于10万且小于30万，进入人工面签，人工面签等待1min后，若无应答，自动转入机器面签
            else if (loanAmount >= 100000 && loanAmount < 300000) {

                // 排队时间
                long waitTime = videoFaceQueue.getWaitTime(webSocketParam.getBankId(), webSocketParam.getUserId(), webSocketParam.getType(),
                        webSocketParam.getAnyChatUserId(), webSocketParam.getOrderId(), wsSessionId);

                // 60s
                if (waitTime >= 60000) {

                    // 机器面签
                    machineFace(wsSessionId);

                    // 退出排队
                    exitTeam(webSocketParam);

                    return false;
                }

                // nothing  -> 正常排队
            }

            // c、若银行分期本金大于30万，进入人工面签，若无人应答，一直处于排队中
            if (loanAmount >= 300000) {

                // nothing  -> 正常排队
            }
        }

        return true;
    }

    /**
     * 推送Msg
     *
     * @param bankId
     */
    private void sendMsg(Long bankId) {

        // customerId-orderId  排队客户ID-订单ID 映射
        List<String> anyChatUserId_wsSessionId_userId_orderId_List = Lists.newArrayList();

        // APP
        sendMsgToApp(bankId, anyChatUserId_wsSessionId_userId_orderId_List);

        // PC
        sendMsgToPC(bankId, anyChatUserId_wsSessionId_userId_orderId_List);
    }

    /**
     * 推送Msg给APP
     *
     * @param bankId
     * @param anyChatUserId_wsSessionId_userId_orderId_List
     */
    private void sendMsgToApp(Long bankId, List<String> anyChatUserId_wsSessionId_userId_orderId_List) {

        // 获取所有app端 排队用户列表        循环发送消息

        // anyChatUserId:wsSessionId:userId:order_id - startTime
        Map<String, Long> sessionIdStartTimeMap_APP = videoFaceQueue.listSessionInQueue(bankId, TYPE_APP);

        if (!CollectionUtils.isEmpty(sessionIdStartTimeMap_APP)) {

            // 按val排序
            Map<String, Long> sortByValueMap = MapSortUtils.sortMapByValue(sessionIdStartTimeMap_APP);

            int size = sortByValueMap.size();
            final int[] rank = {1};

            sortByValueMap.forEach((k, v) -> {
                int rankNum = rank[0]++;

                WebSocketMsgVO msg = new WebSocketMsgVO();
                msg.setRank(rankNum);
                msg.setTotalNum(size);

                // anyChatUserId:wsSessionId:userId:order_id
                String[] userMsgArr = k.split(SEPARATOR);
                String anyChatUserId = userMsgArr[0];
                String wkSessionId = userMsgArr[1];
                String customerId = userMsgArr[2];
                String orderId = userMsgArr[3];

                // 收集anyChatUserId:wsSessionId:userId:order_id
                anyChatUserId_wsSessionId_userId_orderId_List.add(k);

                // APP   -> 排名、总排队数          -> 点对点 推送URL：   /user/{user}/queue/team/info/app       {user} -> wkSessionId
                simpMessagingTemplate.convertAndSendToUser(wkSessionId,
                        "/queue/team/info/app", JSON.toJSONString(ResultBean.ofSuccess(msg)));
            });

        }
    }

    /**
     * 推送Msg给PC
     *
     * @param bankId
     * @param anyChatUserId_wsSessionId_userId_orderId_List 已经按照排队时间排过序了
     */
    private void sendMsgToPC(Long bankId, List<String> anyChatUserId_wsSessionId_userId_orderId_List) {

        // 排队客户列表
        List<VideoFaceCustomerVO> customerVOList = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(anyChatUserId_wsSessionId_userId_orderId_List)) {

            anyChatUserId_wsSessionId_userId_orderId_List.stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(e -> {

                        String[] userMsgArr = e.split(SEPARATOR);
                        Long anyChatUserId = Long.valueOf(userMsgArr[0]);
                        String wkSessionId = userMsgArr[1];
                        Long customerId = Long.valueOf(userMsgArr[2]);
                        Long orderId = Long.valueOf(userMsgArr[3]);

                        VideoFaceCustomerVO videoFaceCustomerVO = new VideoFaceCustomerVO();

                        // anyChatUserId
                        videoFaceCustomerVO.setAnyChatUserId(anyChatUserId);

                        // customer info
                        CustomerVO customerVO = loanCustomerService.getById(customerId);
                        if (null != customerVO) {
                            BeanUtils.copyProperties(customerVO, videoFaceCustomerVO);
                        }

                        // order
                        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
                        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

                        // loanAmount
                        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
                        videoFaceCustomerVO.setExpectLoanAmount(LoanAmountGradeEnum.getNameByLevel(loanBaseInfoDO.getLoanAmount()));

                        // carPrice
                        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
                        if (null != loanFinancialPlanDO) {
                            videoFaceCustomerVO.setCarPrice(loanFinancialPlanDO.getCarPrice());
                        }

                        // carInfo
                        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
                        if (null != loanCarInfoDO) {
                            videoFaceCustomerVO.setCarDetailId(loanCarInfoDO.getCarDetailId());
                            String carFullName = carService.getFullName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL);
                            videoFaceCustomerVO.setCarName(carFullName);
                        }

                        // photo
//                        videoFaceCustomerVO.setIdCardPhotoPath("xxx");
//                        videoFaceCustomerVO.setLivePhotoPath("xxx");

                        // latlon
//                        videoFaceCustomerVO.setLatlon("xxx");
//                        videoFaceCustomerVO.setLocation("xxx");

                        customerVOList.add(videoFaceCustomerVO);
                    });
        }

        // 获取所有pc端  列表               循环发送消息
        Map<String, Long> sessionIdStartTimeMap_PC = videoFaceQueue.listSessionInQueue(bankId, TYPE_PC);

        if (!CollectionUtils.isEmpty(sessionIdStartTimeMap_PC)) {

            sessionIdStartTimeMap_PC.forEach((k, v) -> {

                // anyChatUserId:wsSessionId::userId
                String[] userMsgArr = k.split(SEPARATOR);
                String anyChatUserId = userMsgArr[0];
                String wkSessionId = userMsgArr[1];
                String userId = userMsgArr[2];

                // PC    -> 排队用户列表detail
                simpMessagingTemplate.convertAndSendToUser(wkSessionId,
                        "/queue/team/info/pc", JSON.toJSONString(ResultBean.ofSuccess(customerVOList)));
            });
        }
    }

    /**
     * 机器面签
     *
     * @param wsSessionId
     */
    private void machineFace(String wsSessionId) {

        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setFaceSign(FACE_SIGN_MACHINE);

        simpMessagingTemplate.convertAndSendToUser(wsSessionId, "/queue/faceSign/machine",
                JSON.toJSONString(ResultBean.ofSuccess(webSocketMsgVO)));
    }

}
