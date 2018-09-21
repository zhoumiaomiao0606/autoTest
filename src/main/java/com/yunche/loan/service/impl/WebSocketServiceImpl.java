package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.queue.VideoFaceQueue;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.MapSortUtils;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import com.yunche.loan.domain.param.WebSocketParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static com.yunche.loan.config.constant.BankConst.*;
import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.CarConst.CAR_MODEL;
import static com.yunche.loan.config.constant.FaceSignConst.FACE_SIGN_MACHINE;
import static com.yunche.loan.config.constant.VideoFaceConst.*;
import static com.yunche.loan.config.queue.VideoFaceQueue.SEPARATOR;
import static com.yunche.loan.config.util.DateTimeFormatUtils.formatter_yyyyMMdd_HHmmss;

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
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private ConfVideoFaceTimeDOMapper confVideoFaceTimeDOMapper;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private CarService carService;

    @Autowired
    private DictService dictService;


    @Override
    public void waitTeam(@NonNull WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(bankCache), "bank不能为空");
        Preconditions.checkNotNull(webSocketParam.getUserId(), "userId不能为空");
        Preconditions.checkNotNull(webSocketParam.getType(), "type不能为空");
        Preconditions.checkNotNull(webSocketParam.getAnyChatUserId(), "anyChatUserId不能为空");
        if (TYPE_APP.equals(webSocketParam.getType())) {
            Preconditions.checkNotNull(webSocketParam.getOrderId(), "orderId不能为空");
            Preconditions.checkNotNull(webSocketParam.getBankPeriodPrincipal(), "bankPeriodPrincipal不能为空");
        }

        // webSocket 会话ID
        String wsSessionId = SessionUtils.getWebSocketSessionId();

        // 前置判断：是否需要排队
        boolean needWaitTeam = needWaitTeam(webSocketParam, wsSessionId);

        // 无需排队 --> 机器面签
        if (!needWaitTeam) {
            // 机器面签
            doMachineFace(webSocketParam, wsSessionId);
            // 结束
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
            Preconditions.checkNotNull(webSocketParam.getBankPeriodPrincipal(), "bankPeriodPrincipal不能为空");
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

        // 生成roomId     -> 9位随机数
        int roomId = new Random().nextInt(1000000000);

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

        // PC端 wsSessionId
        String wsSessionId_pc = videoFaceQueue.getWsSessionIdByAnyChatUserId(webSocketParam.getBankId(),
                webSocketParam.getPcAnyChatUserId(), TYPE_PC);

        // 转发给pc端
        simpMessagingTemplate.convertAndSendToUser(wsSessionId_pc,
                "/queue/latlon/pc", JSON.toJSONString(ResultBean.ofSuccess(webSocketParam)));
    }

    @Override
    public void network(WebSocketParam webSocketParam) {
        Preconditions.checkNotNull(webSocketParam.getBankId(bankCache), "bank不能为空");
        Preconditions.checkNotNull(webSocketParam.getPcAnyChatUserId(), "pcAnyChatUserId不能为空");

        // PC端 wsSessionId
        String wsSessionId_pc = videoFaceQueue.getWsSessionIdByAnyChatUserId(webSocketParam.getBankId(),
                webSocketParam.getPcAnyChatUserId(), TYPE_PC);

        // 转发给pc端
        simpMessagingTemplate.convertAndSendToUser(wsSessionId_pc,
                "/queue/network/pc", JSON.toJSONString(ResultBean.ofSuccess(webSocketParam)));
    }

    /**
     * 是否需要排队
     *
     * @param webSocketParam
     * @param wsSessionId
     * @return
     */
    private boolean needWaitTeam(WebSocketParam webSocketParam, String wsSessionId) {

        // PC端直接排队
        if (TYPE_PC.equals(webSocketParam.getType())) {
            return true;
        }

        return doWaitTeam_ICBC(webSocketParam, wsSessionId);
    }

    /**
     * 面签排队     -杭州城站支行
     *
     * @param webSocketParam
     * @param wsSessionId
     * @return
     */
    private boolean doWaitTeam_ICBC(WebSocketParam webSocketParam, String wsSessionId) {

        Long bankId = webSocketParam.getBankId();
        List<ConfVideoFaceTimeDO> confVideoFaceTimeDOS = confVideoFaceTimeDOMapper.listByBankId(bankId);

        // 无配置
        if (CollectionUtils.isEmpty(confVideoFaceTimeDOS)) {

            return doWaitTeam_ICBC_default(webSocketParam, wsSessionId);
        }

        // 有配置
        else {

            return doWaitTeam_ICBC_conf(webSocketParam, wsSessionId, confVideoFaceTimeDOS);
        }
    }

    /**
     * 排队-conf
     *
     * @param webSocketParam
     * @param wsSessionId
     * @param confVideoFaceTimeDOS
     * @return
     */
    private boolean doWaitTeam_ICBC_conf(WebSocketParam webSocketParam,
                                         String wsSessionId,
                                         List<ConfVideoFaceTimeDO> confVideoFaceTimeDOS) {

        List<ConfVideoFaceTimeDO> type_work_list = Lists.newArrayList();
        List<ConfVideoFaceTimeDO> type_weekend_list = Lists.newArrayList();
        List<ConfVideoFaceTimeDO> type_holiday_list = Lists.newArrayList();

        confVideoFaceTimeDOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Byte type = e.getType();

                    if (CONF_TYPE_WORK.equals(type)) {

                        type_work_list.add(e);

                    } else if (CONF_TYPE_WEEKEND.equals(type)) {

                        type_weekend_list.add(e);

                    } else if (CONF_TYPE_HOLIDAY.equals(type)) {

                        type_holiday_list.add(e);
                    }

                });

        return doWaitTeam_ICBC_conf(webSocketParam, wsSessionId, type_work_list, type_weekend_list, type_holiday_list);
    }

    /**
     * 排队-default
     *
     * @param webSocketParam
     * @param wsSessionId
     * @return
     */
    private boolean doWaitTeam_ICBC_default(WebSocketParam webSocketParam, String wsSessionId) {

        Long bankId = webSocketParam.getBankId();

        if (BANK_ID_ICBC_HangZhou_City_Station_Branch.equals(bankId)) {

            return doWaitTeam_ICBC_HangZhou_default(webSocketParam, wsSessionId);

        } else if (BANK_ID_ICBC_TaiZhou_LuQiao_Branch.equals(bankId)
                || BANK_ID_ICBC_TaiZhou_LuQiao__Branch_TEST.equals(webSocketParam.getBankId())) {

            return doWaitTeam_ICBC_TaiZhou_LuQiao_default(webSocketParam, wsSessionId);
        }

        // 无限排队
        return true;
    }

    /**
     * 节假日设置判断
     *
     * @param webSocketParam
     * @param wsSessionId
     * @param type_work_list
     * @param type_weekend_list
     * @param type_holiday_list
     * @return
     */
    private boolean doWaitTeam_ICBC_conf(WebSocketParam webSocketParam,
                                         String wsSessionId,
                                         List<ConfVideoFaceTimeDO> type_work_list,
                                         List<ConfVideoFaceTimeDO> type_weekend_list,
                                         List<ConfVideoFaceTimeDO> type_holiday_list) {


        // 是否存在节假日设置

        // 存在
        if (!CollectionUtils.isEmpty(type_holiday_list)) {

            return doWaitTeam_holiday(webSocketParam, wsSessionId, type_work_list, type_weekend_list, type_holiday_list);
        }

        // 不存在
        else {

            return doWaitTeam_work_weekend(webSocketParam, wsSessionId, type_work_list, type_weekend_list);
        }
    }

    /**
     * 节假日-111
     *
     * @param webSocketParam
     * @param wsSessionId
     * @param type_work_list
     * @param type_weekend_list
     * @param type_holiday_list
     * @return
     */
    private boolean doWaitTeam_holiday(WebSocketParam webSocketParam,
                                       String wsSessionId,
                                       List<ConfVideoFaceTimeDO> type_work_list,
                                       List<ConfVideoFaceTimeDO> type_weekend_list,
                                       List<ConfVideoFaceTimeDO> type_holiday_list) {

        // 是
        if (match_holiday(webSocketParam, type_holiday_list)) {

            // (无限)排队：人工面签
            return true;
        }
        // 否
        else {

            return doWaitTeam_work_weekend(webSocketParam, wsSessionId, type_work_list, type_weekend_list);
        }
    }

    /**
     * 是否为：holiday
     *
     * @param webSocketParam
     * @param type_holiday_list
     * @return
     */
    private boolean match_holiday(WebSocketParam webSocketParam, List<ConfVideoFaceTimeDO> type_holiday_list) {

        final boolean[] result = {false};

        double bankPeriodPrincipal = webSocketParam.getBankPeriodPrincipal().doubleValue();
        LocalDateTime now = LocalDateTime.now();

        type_holiday_list.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    double startLoanAmount = e.getStartLoanAmount().doubleValue();
                    double endLoanAmount = e.getEndLoanAmount().doubleValue();
                    LocalDateTime startDateTime = LocalDateTime.parse(e.getStartTime(), formatter_yyyyMMdd_HHmmss);
                    LocalDateTime endDateTime = LocalDateTime.parse(e.getEndTime(), formatter_yyyyMMdd_HHmmss);

                    // 匹配：节假日时间段
                    boolean match_dateTime = now.isAfter(startDateTime) && now.isBefore(endDateTime);
                    // 匹配：金额
                    boolean match_loan = bankPeriodPrincipal >= startLoanAmount && bankPeriodPrincipal <= endLoanAmount;
                    boolean match_loan_ = bankPeriodPrincipal >= startLoanAmount && endLoanAmount == -1;

                    boolean match = match_dateTime && (match_loan || match_loan_);
                    if (match) {

                        // (无限)排队：人工面签
                        result[0] = true;
                        return;
                    }
                });

        return result[0];
    }


    /**
     * work / weekend
     *
     * @param webSocketParam
     * @param wsSessionId
     * @param type_work_list
     * @param type_weekend_list
     */
    private boolean doWaitTeam_work_weekend(WebSocketParam webSocketParam,
                                            String wsSessionId,
                                            List<ConfVideoFaceTimeDO> type_work_list,
                                            List<ConfVideoFaceTimeDO> type_weekend_list) {

        boolean result = false;
        int week = LocalDateTime.now().getDayOfWeek().getValue();

        // 工作日
        if (week >= 1 && week <= 5) {

            result = doWaitTeam_work_weekend(webSocketParam, wsSessionId, type_work_list);
        }

        // 周末
        else if (week >= 6 && week <= 7) {

            result = doWaitTeam_work_weekend(webSocketParam, wsSessionId, type_weekend_list);
        }

        return result;
    }

    /**
     * -
     *
     * @param webSocketParam
     * @param wsSessionId
     * @param type_work_weekend_list
     */
    private boolean doWaitTeam_work_weekend(WebSocketParam webSocketParam,
                                            String wsSessionId,
                                            List<ConfVideoFaceTimeDO> type_work_weekend_list) {

        double bankPeriodPrincipal = webSocketParam.getBankPeriodPrincipal().doubleValue();
        LocalTime nowTime = LocalTime.now();

        // 有配置
        if (!CollectionUtils.isEmpty(type_work_weekend_list)) {

            final boolean[] result = {false};

            type_work_weekend_list.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        LocalTime startTime = LocalTime.parse(e.getStartTime());
                        LocalTime endTime = LocalTime.parse(e.getEndTime());

                        double startLoanAmount = e.getStartLoanAmount().doubleValue();
                        double endLoanAmount = e.getEndLoanAmount().doubleValue();

                        // 匹配：时间段
                        boolean match_time = nowTime.isAfter(startTime) && nowTime.isBefore(endTime);
                        // 匹配：金额
                        boolean match_loan = bankPeriodPrincipal >= startLoanAmount && bankPeriodPrincipal <= endLoanAmount;
                        boolean match_loan_ = bankPeriodPrincipal >= startLoanAmount && endLoanAmount == -1;

                        boolean match = match_time && (match_loan || match_loan_);
                        if (match) {

                            // (无限)排队：人工面签
                            result[0] = true;
                            return;
                        }
                    });

            return result[0];
        }

        // 无配置 -> 走默认
        else {

            return doWaitTeam_ICBC_default(webSocketParam, wsSessionId);
        }
    }


    /**
     * 面签排队     -杭州城站支行
     *
     * @param webSocketParam
     * @param wsSessionId
     * @return
     */
    private boolean doWaitTeam_ICBC_HangZhou_default(WebSocketParam webSocketParam, String wsSessionId) {

        // 银行分期本金
        double bankPeriodPrincipal = webSocketParam.getBankPeriodPrincipal().doubleValue();

        // 城站工行的配置规则为：

        // 1、金额≥30万，全天，无限等待 -> 只能走人工面签
        if (bankPeriodPrincipal >= 300000) {

            // 正常排队
            return true;
        }

        // 2、金额＜30万时，每天08:30~12:00 以及 14:00~17:30  等待时长10分钟后，自动接通机器面签; 剩余时间，等待0分钟后，走机器面签
        else {

            LocalTime now_time = LocalTime.now();

            LocalTime start_time_8_30 = LocalTime.of(8, 30);
            LocalTime end_time_12_00 = LocalTime.of(12, 00);
            LocalTime start_time_14_00 = LocalTime.of(14, 00);
            LocalTime end_time_17_30 = LocalTime.of(17, 30);

            // 每天08:30~12:00 以及 14:00~17:30
            boolean match_time = (now_time.isAfter(start_time_8_30) && now_time.isBefore(end_time_12_00)) ||
                    (now_time.isAfter(start_time_14_00) && now_time.isBefore(end_time_17_30));

            if (match_time) {

                // 排队时间
                Long startWaitTime = videoFaceQueue.getWaitTime(webSocketParam.getBankId(), webSocketParam.getUserId(), webSocketParam.getType(),
                        webSocketParam.getAnyChatUserId(), webSocketParam.getOrderId(), wsSessionId);

                if (null != startWaitTime) {

                    // 排队时间
                    long waitTime = System.currentTimeMillis() - startWaitTime;

                    // 等待时长10分钟后，自动接通机器面签
                    if (waitTime >= 10 * 60 * 1000) {

                        // 进行机器面签
                        return false;
                    }
                }
            }

            // 剩余时间，等待0分钟后，走机器面签
            else {

                // 进行机器面签
                return false;
            }

        }

        // nothing  -> 正常排队
        return true;
    }

    /**
     * 面签排队     -台州路桥支行
     *
     * @param webSocketParam
     * @param wsSessionId
     * @return
     */
    private boolean doWaitTeam_ICBC_TaiZhou_LuQiao_default(WebSocketParam webSocketParam, String wsSessionId) {

        // 银行分期本金
        double bankPeriodPrincipal = webSocketParam.getBankPeriodPrincipal().doubleValue();

        // a、若银行分期本金小于10万，进入机器面签
        if (bankPeriodPrincipal < 100000) {

            // 进行机器面签
            return false;
        }

        // b、10万及以上人工面签等待一分钟，若无人接听转入机器面签
        else if (bankPeriodPrincipal >= 100000) {

            // 排队时间
            Long startWaitTime = videoFaceQueue.getWaitTime(webSocketParam.getBankId(), webSocketParam.getUserId(), webSocketParam.getType(),
                    webSocketParam.getAnyChatUserId(), webSocketParam.getOrderId(), wsSessionId);

            if (null != startWaitTime) {

                // 排队时间
                long waitTime = System.currentTimeMillis() - startWaitTime;

                // 60s
                if (waitTime >= 60000) {

                    // 进行机器面签
                    return false;
                }
            }
        }

        // nothing  -> 正常排队
        return true;
    }

    /**
     * 进行机器面签
     *
     * @param webSocketParam
     * @param wsSessionId
     * @return
     */
    private boolean doMachineFace(WebSocketParam webSocketParam, String wsSessionId) {

        // 机器面签
        machineFace(wsSessionId, webSocketParam.getBankId());

        // 退出排队
        exitTeam(webSocketParam);

        return false;
    }

    /**
     * 推送Msg
     *
     * @param bankId
     */
    private void sendMsg(Long bankId) {

        // customerId-orderId  排队客户ID-订单ID 映射
        List<String> anyChatUserId_wsSessionId_userId_orderId_rankNum_List = Lists.newArrayList();

        // APP
        sendMsgToApp(bankId, anyChatUserId_wsSessionId_userId_orderId_rankNum_List);

        // PC
        sendMsgToPC(bankId, anyChatUserId_wsSessionId_userId_orderId_rankNum_List);
    }

    /**
     * 推送Msg给APP
     *
     * @param bankId
     * @param anyChatUserId_wsSessionId_userId_orderId_rankNum_List
     */
    private void sendMsgToApp(Long bankId, List<String> anyChatUserId_wsSessionId_userId_orderId_rankNum_List) {

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

                // 收集anyChatUserId:wsSessionId:userId:order_id     : rankNum
                String k_rankNum = k + SEPARATOR + rankNum;
                anyChatUserId_wsSessionId_userId_orderId_rankNum_List.add(k_rankNum);

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
     * @param anyChatUserId_wsSessionId_userId_orderId_rankNum_List 已经按照排队时间排过序了
     */
    private void sendMsgToPC(Long bankId, List<String> anyChatUserId_wsSessionId_userId_orderId_rankNum_List) {

        // 排队客户列表
        Map<Long, VideoFaceCustomerVO> rankNum_customerVO_Map = Maps.newTreeMap();

        if (!CollectionUtils.isEmpty(anyChatUserId_wsSessionId_userId_orderId_rankNum_List)) {

            anyChatUserId_wsSessionId_userId_orderId_rankNum_List.parallelStream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(e -> {

                        String[] userMsgArr = e.split(SEPARATOR);
                        Long anyChatUserId = Long.valueOf(userMsgArr[0]);
                        String wkSessionId = userMsgArr[1];
                        Long customerId = Long.valueOf(userMsgArr[2]);
                        Long orderId = Long.valueOf(userMsgArr[3]);
                        Long rankNum = Long.valueOf(userMsgArr[4]);

                        VideoFaceCustomerVO videoFaceCustomerVO = setAndGetVideoFaceCustomerVO(bankId, orderId, anyChatUserId);

                        rankNum_customerVO_Map.put(rankNum, videoFaceCustomerVO);
                    });
        }

        // customerVOList
        Collection<VideoFaceCustomerVO> customerVOList = rankNum_customerVO_Map.values();

        // 获取所有pc端  列表               循环发送消息
        Map<String, Long> sessionIdStartTimeMap_PC = videoFaceQueue.listSessionInQueue(bankId, TYPE_PC);

        if (!CollectionUtils.isEmpty(sessionIdStartTimeMap_PC)) {

            sessionIdStartTimeMap_PC.forEach((k, v) -> {

                // anyChatUserId:wsSessionId:userId
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
     * 获取并填充客户信息
     *
     * @param bankId
     * @param orderId
     * @param anyChatUserId
     * @return
     */
    private VideoFaceCustomerVO setAndGetVideoFaceCustomerVO(Long bankId, Long orderId, Long anyChatUserId) {

        VideoFaceCustomerVO videoFaceCustomerVO = new VideoFaceCustomerVO();

        // 担保公司
        videoFaceCustomerVO.setGuaranteeCompanyId(GUARANTEE_COMPANY_ID);
        videoFaceCustomerVO.setGuaranteeCompanyName(GUARANTEE_COMPANY_NAME);

        // bankId
        videoFaceCustomerVO.setBankId(bankId);

        // orderId
        videoFaceCustomerVO.setOrderId(String.valueOf(orderId));

        // anyChatUserId
        videoFaceCustomerVO.setAnyChatUserId(anyChatUserId);

        // info
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        if (null != universalInfoVO) {

            // customer
            videoFaceCustomerVO.setId(Long.valueOf(universalInfoVO.getCustomer_id()));
            videoFaceCustomerVO.setName(universalInfoVO.getCustomer_name());
            videoFaceCustomerVO.setIdCard(universalInfoVO.getCustomer_id_card());

            // carPrice
            videoFaceCustomerVO.setCarPrice(new BigDecimal(universalInfoVO.getFinancial_car_price()));
            // 意向贷款金额    -> 银行分期本金
            videoFaceCustomerVO.setExpectLoanAmount(new BigDecimal(universalInfoVO.getFinancial_bank_period_principal()));

            // partner
            videoFaceCustomerVO.setPartnerId(universalInfoVO.getPartner_id());
            videoFaceCustomerVO.setPartnerName(universalInfoVO.getPartner_name());

            // carInfo
            videoFaceCustomerVO.setCarDetailId(Long.valueOf(universalInfoVO.getCar_detail_id()));
            // carName 特殊处理
            String carName = carService.getName(Long.valueOf(universalInfoVO.getCar_detail_id()), CAR_DETAIL, CAR_MODEL);
            videoFaceCustomerVO.setCarName(carName);
        }

        return videoFaceCustomerVO;
    }

    /**
     * 机器面签
     *
     * @param wsSessionId
     * @param bankId
     */
    private void machineFace(String wsSessionId, Long bankId) {

        WebSocketMsgVO webSocketMsgVO = new WebSocketMsgVO();
        webSocketMsgVO.setFaceSign(FACE_SIGN_MACHINE);

        Map<String, String> kvMap = dictService.getKVMap("videoFaceVoicePath");
        if (!CollectionUtils.isEmpty(kvMap)) {
            String videoFaceVoicePath = kvMap.get(String.valueOf(bankId));
            webSocketMsgVO.setVoicePath(videoFaceVoicePath);
        }

        simpMessagingTemplate.convertAndSendToUser(wsSessionId, "/queue/faceSign/machine",
                JSON.toJSONString(ResultBean.ofSuccess(webSocketMsgVO)));
    }

}
