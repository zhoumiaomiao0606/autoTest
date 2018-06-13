package com.yunche.loan.config.queue;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author liuzhe
 * @date 2018/5/31
 */
@Component
public class AnychatQueue {

    /**
     * 缓存KEY
     */
    private static final String VIDEO_FACE_ROOM_CACHE_KEY = "video:face:room";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;


    @PostConstruct
    private void initRoom() {

        List<Room> roomList = Lists.newArrayList();

        List<String> allBankName = bankCache.getAllBankName();

        if (!CollectionUtils.isEmpty(allBankName)) {

            final long[] roomId = {1};

            allBankName.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Room room = new Room();
                        room.setId(roomId[0]++);
                        room.setName(e);

                        roomList.add(room);
                    });
        }

        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(VIDEO_FACE_ROOM_CACHE_KEY);
        boundValueOps.set(JSON.toJSONString(roomList));
    }

    /**
     * 获取房间列表
     *
     * @return
     */
    public List<Room> getRoomList() {
        // get
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(VIDEO_FACE_ROOM_CACHE_KEY);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, Room.class);
        }

        // 刷新缓存
        initRoom();

        // get
        result = boundValueOps.get();
        if (StringUtils.isNotBlank(result)) {
            return JSON.parseArray(result, Room.class);
        }
        return null;
    }

    public Room getRoomByOrderId(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        Long loanBaseInfoId = loanOrderDO.getLoanBaseInfoId();
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
        Preconditions.checkNotNull(loanBaseInfoDO, "");
        Preconditions.checkNotNull(loanBaseInfoDO.getBank(), "");

        return null;
    }

    @Data
    public static class Room {

        private Long id;

        private String name;
    }
}
