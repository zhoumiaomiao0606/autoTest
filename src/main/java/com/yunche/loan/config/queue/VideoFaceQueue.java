package com.yunche.loan.config.queue;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import sun.jvm.hotspot.debugger.LongHashMap;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
@Component
public class VideoFaceQueue {

    /**
     * 视频面签 -队列排队列表 前缀
     */
    private static final String VIDEO_FACE_QUEUE_PREFIX = "video-face-queue:";
    /**
     * 分隔符
     */
    public static final String SEPARATOR = ":";
    /**
     * 排队过期时间：10s
     */
    private static final Long VIDEO_FACE_ROOM_CACHE_KEY_EXPIRE = 10L;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


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
    public void addQueue(Long queueId, Long userId, Byte clientType, Long anyChatUserId, Long orderId, String wsSessionId) {

        // 队列排名依据   -> val ： 开始排队时间
        long startTime = System.currentTimeMillis();

        // prefix  +  queue_id  +  client_type  +  anyChat_user_id  +  ws_session_id  +  user_id  +  order_id
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId
                + SEPARATOR + wsSessionId + SEPARATOR + userId + SEPARATOR + orderId;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/addQueue.lua")));
        redisScript.setResultType(Long.class);

        Object result = stringRedisTemplate.execute(redisScript, Lists.newArrayList(key),
                String.valueOf(VIDEO_FACE_ROOM_CACHE_KEY_EXPIRE), String.valueOf(startTime));

        Preconditions.checkArgument((long) result == 1, "排队出错");


//        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);
//
//        // 不存在，则更新排队时间
//        boundValueOps.setIfAbsent(String.valueOf(startTime));
//        // 设置过期时间
//        boundValueOps.expire(VIDEO_FACE_ROOM_CACHE_KEY_EXPIRE, TimeUnit.SECONDS);
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
    public void exitQueue(Long queueId, Long userId, Byte clientType, Long anyChatUserId, Long orderId, String wsSessionId) {

        // prefix  +  queue_id  +  client_type  +  anyChat_user_id  +  ws_session_id  +  user_id  +  order_id
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId
                + SEPARATOR + wsSessionId + SEPARATOR + userId + SEPARATOR + orderId;

        String scriptText = "return redis.call('DEL', KEYS[1])";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(scriptText);
        redisScript.setResultType(Long.class);

        stringRedisTemplate.execute(redisScript, Lists.newArrayList(key));

//        stringRedisTemplate.delete(key);
    }

    /**
     * sessionId:userId: - startTime 映射
     *
     * @param queueId
     * @param clientType
     * @return
     */
    public Map<String, Long> listSessionInQueue(Long queueId, Byte clientType) {

        // prefix  +  queue_id  +  client_type  +
        String keyPattern = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + "**";

        String scriptText = "return redis.call('KEYS', KEYS[1])";

        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(scriptText);
        redisScript.setResultType(List.class);

        List<String> keys = (List<String>) stringRedisTemplate.execute(redisScript, Lists.newArrayList(keyPattern));

        Map<String, Long> sessionIdStartTimeMap = Maps.newHashMap();

        if (!CollectionUtils.isEmpty(keys)) {

            String[] keyPatternArr = keyPattern.split("\\*\\*");
            String keyPrefix = keyPatternArr[0];

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
    public String getWsSessionIdByAnyChatUserId(Long queueId, Long anyChatUserId, Byte clientType) {

        // prefix  +  queue_id  +  client_type  +  anyChat_userId
        String keyPrefix = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId + SEPARATOR;

//        Set<String> keys = stringRedisTemplate.keys(keyPrefix + "*");

        String scriptText = "return redis.call('KEYS', KEYS[1])";

        DefaultRedisScript<List> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(scriptText);
        redisScript.setResultType(List.class);

        List<String> keys = stringRedisTemplate.execute(redisScript, Lists.newArrayList(keyPrefix + "**"));

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
    public Long getWaitTime(Long queueId, Long userId, Byte clientType, Long anyChatUserId, Long orderId, String wsSessionId) {

        // prefix  +  queue_id  +  client_type  +  anyChat_user_id  +  ws_session_id  +  user_id  +  order_id
        String key = VIDEO_FACE_QUEUE_PREFIX + queueId + SEPARATOR + clientType + SEPARATOR + anyChatUserId
                + SEPARATOR + wsSessionId + SEPARATOR + userId + SEPARATOR + orderId;

        String scriptText = "return redis.call('GET', KEYS[1])";

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(scriptText);
        redisScript.setResultType(Long.class);

        Long waitTime = stringRedisTemplate.execute(redisScript, Lists.newArrayList(key));

//        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(key);
//        String waitTime = boundValueOps.get();

        return waitTime;
    }

}