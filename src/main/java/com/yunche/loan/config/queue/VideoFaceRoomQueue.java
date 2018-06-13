package com.yunche.loan.config.queue;

import com.genxiaogu.ratelimiter.service.impl.DistributedLimiter;
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

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
@Component
public class VideoFaceRoomQueue {

    private static final Logger logger = LoggerFactory.getLogger(VideoFaceRoomQueue.class);

    /**
     * 视频面签 -队列排队列表 前缀
     */
    private static final String VIDEO_FACE_QUEUE_PREFIX = "video:face:queue:";
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ":";
    /**
     * 房间ID
     */
    private static final String VIDEO_FACE_ROOM_ID_KEY_PREFIX = "video:face:room:id:";
    /**
     * 排队过期时间：3s
     */
    private static final Long VIDEO_FACE_ROOM_CACHE_KEY_EXPIRE = 300000L;

    /**
     * 默认过期时间：30min
     */
    private static final Long VIDEO_FACE_ROOM_ID_KEY_EXPIRE = 1800L;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private DistributedLimiter distributedLimiter;


    /**
     * 进入（队列）排队    or  刷新过期时间
     *
     * @param queueId
     * @param userId
     * @param clientType  1-PC; 2-APP;
     * @param wsSessionId WebSocket 会话ID
     */
    public void addQueue(Long queueId, Long userId, Integer clientType, String wsSessionId) {

        // 队列排名依据   -> val ： 开始排队时间
        long startTime = System.currentTimeMillis();

        // prefix  +  queue_id  +  client_type  +  ws_session_id  +  userId
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + wsSessionId + SEPARATOR + userId;

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
     * @param wsSessionId
     */
    public void exitQueue(Long queueId, Long userId, Integer clientType, String wsSessionId) {

        // prefix  +  queue_id  +  client_type  +  ws_session_id  +  userId
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + wsSessionId + SEPARATOR + userId;

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
     * sessionId:userId - startTime 映射
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

                            String sessionId_userId = keyArr[1];

                            sessionIdStartTimeMap.put(sessionId_userId, Long.valueOf(startTime));
                        }

                    });
        }

        return sessionIdStartTimeMap;
    }

}