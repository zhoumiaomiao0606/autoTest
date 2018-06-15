package com.yunche.loan.config.queue;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.domain.vo.CustomerVO;
import com.yunche.loan.service.LoanCustomerService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
@Component
public class VideoFaceQueue {

    private static final Logger logger = LoggerFactory.getLogger(VideoFaceQueue.class);

    /**
     * 视频面签 -队列排队列表 前缀
     */
    private static final String VIDEO_FACE_QUEUE_PREFIX = "video-face-queue:";
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ":";
    /**
     * 房间ID
     */
    private static final String VIDEO_FACE_ROOM_ID_KEY_PREFIX = "video:face:room:id:";
    /**
     * TODO 排队过期时间：30s
     */
//    private static final Long VIDEO_FACE_ROOM_CACHE_KEY_EXPIRE = 300000L;
    private static final Long VIDEO_FACE_ROOM_CACHE_KEY_EXPIRE = 30000000L;

    /**
     * 默认过期时间：30min
     */
    private static final Long VIDEO_FACE_ROOM_ID_KEY_EXPIRE = 1800L;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private LoanCustomerService loanCustomerService;


    /**
     * 进入（队列）排队    or  刷新过期时间
     *
     * @param queueId
     * @param userId
     * @param clientType    1-PC; 2-APP;
     * @param anyChatUserId
     * @param orderId
     * @param wsSessionId   WebSocket 会话ID
     */
    public void addQueue(Long queueId, Long userId, Integer clientType, Long anyChatUserId, Long orderId, String wsSessionId) {

        // 队列排名依据   -> val ： 开始排队时间
        long startTime = System.currentTimeMillis();

        // prefix  +  queue_id  +  client_type  +  anyChat_user_id  +  ws_session_id  +  user_id  +  order_id
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId
                + SEPARATOR + wsSessionId + SEPARATOR + userId + SEPARATOR + orderId;

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);

        // 不存在，则更新排队时间
        boundValueOps.setIfAbsent(String.valueOf(startTime));
        // 设置过期时间
        boundValueOps.expire(VIDEO_FACE_ROOM_CACHE_KEY_EXPIRE, TimeUnit.MILLISECONDS);
    }

    /**
     * 退出（队列）排队
     *
     * @param queueId
     * @param userId
     * @param clientType
     * @param anyChatUserId
     * @param orderId
     * @param wsSessionId
     */
    public void exitQueue(Long queueId, Long userId, Integer clientType, Long anyChatUserId, Long orderId, String wsSessionId) {

        // prefix  +  queue_id  +  client_type  +  anyChat_user_id  +  ws_session_id  +  user_id  +  order_id
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId
                + SEPARATOR + wsSessionId + SEPARATOR + userId + SEPARATOR + orderId;

        stringRedisTemplate.delete(key);
    }

    /**
     * 队列中的 排队用户列表
     *
     * @param queueId
     * @return
     */
    public List<CustomerVO> listCustomerInQueue(Long queueId) {

//        Set<String> keys = listKeyInQueue(queueId);

        // prefix  +  queue_id  +  customerId / userId
        String keyPrefix = VIDEO_FACE_QUEUE_PREFIX + queueId + ":";

        Set<String> keys = stringRedisTemplate.keys(keyPrefix + "*");

        List<CustomerVO> customerVOList = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(keys)) {

            keys.stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(e -> {

                        String[] keyArr = e.split(keyPrefix);

                        if (keyArr.length == 2) {

                            String userId = keyArr[1];

                            CustomerVO customerVO = loanCustomerService.getById(Long.valueOf(userId));

                            customerVOList.add(customerVO);
                        }

                    });

        }

        return customerVOList;
    }

    public Set<String> listKeyInQueue(Long queueId) {

        // prefix  +  queue_id  +  userId
        String keyPrefix = VIDEO_FACE_QUEUE_PREFIX + queueId + ":";

        Set<String> keys = stringRedisTemplate.keys(keyPrefix + "*");

        return keys;
    }

    /**
     * sessionId:userId: - startTime 映射
     *
     * @param queueId
     * @param clientType
     * @return
     */
    public Map<String, Long> listSessionInQueue(Long queueId, Integer clientType) {

        // prefix  +  queue_id  +  client_type  +
        String keyPrefix = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR;

        Set<String> keys = stringRedisTemplate.keys(keyPrefix + "*");

        Map<String, Long> sessionIdStartTimeMap = Maps.newHashMap();

        if (!CollectionUtils.isEmpty(keys)) {

            keys.stream()
                    .filter(StringUtils::isNotBlank)
                    .forEach(key -> {

                        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);

                        // val
                        String startTime = boundValueOps.get();

                        // key
                        String[] keyArr = key.split(keyPrefix);
                        if (keyArr.length == 2) {

                            // anyChatUserId:wsSessionId:userId:order_id               tips：PC端无orderId
                            String anyChatUserId_wsSessionId_userId_orderId = keyArr[1];

                            sessionIdStartTimeMap.put(anyChatUserId_wsSessionId_userId_orderId, Long.valueOf(startTime));
                        }

                    });
        }

        return sessionIdStartTimeMap;
    }

    /**
     * 获取wsSessionId
     *
     * @param queueId
     * @param anyChatUserId
     * @param clientType
     * @return
     */
    public String getWsSessionIdByAnyChatUserId(Long queueId, Long anyChatUserId, Integer clientType) {

        // prefix  +  queue_id  +  client_type  +  anyChat_userId
        String keyPrefix = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId + SEPARATOR;

        Set<String> keys = stringRedisTemplate.keys(keyPrefix + "*");

        if (!CollectionUtils.isEmpty(keys)) {

            String key = keys.iterator().next();

            String[] keyArr = key.split(keyPrefix);

            // wsSessionId:userId:order_id                tips：PC端无orderId
            String wsSessionId_userId_orderId = keyArr[1];

            String[] strArr = wsSessionId_userId_orderId.split(SEPARATOR);
            String wsSessionId = strArr[0];

            return wsSessionId;
        }

        return null;
    }

    /**
     * 排队时间     单位：毫秒
     *
     * @param queueId
     * @param userId
     * @param clientType
     * @param anyChatUserId
     * @param orderId
     * @param wsSessionId
     * @return
     */
    public long getWaitTime(Long queueId, Long userId, Integer clientType, Long anyChatUserId, Long orderId, String wsSessionId) {

        // prefix  +  queue_id  +  client_type  +  anyChat_user_id  +  ws_session_id  +  user_id  +  order_id
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId
                + SEPARATOR + wsSessionId + SEPARATOR + userId + SEPARATOR + orderId;

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);

        String waitTime = boundValueOps.get();

        if (StringUtils.isNotBlank(waitTime)) {

            return Long.valueOf(waitTime);
        }

        return 0;
    }

}