package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.queue.AnychatQueue;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.VideoFaceLogDOMapper;
import com.yunche.loan.service.VideoFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
@Service
public class VideoFaceServiceImpl implements VideoFaceService {

    @Autowired
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

    @Autowired
    private AnychatQueue anychatQueue;


    //    @PostConstruct
    public ResultBean test() {

//        /Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/lib

//        int[] ints = AnyChatServerSDK.GetRoomIdList();

        // /Users/liuzhe/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
        String library = System.getProperty("java.library.path");


//        export LD_LIBRARY_PATH=/usr/work/java/jdk1.6.0_21/jre/lib:$LD_LIBRARY_PATH

//        export LD_LIBRARY_PATH=/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home/jre/lib:$LD_LIBRARY_PATH

        System.out.println(library);


//        AnyChatServerSDK anyChatServerSDK = new AnyChatServerSDK();
//        System.out.println(JSON.toJSONString(anyChatServerSDK));


        return ResultBean.ofSuccess(null);
    }

    @Override
    public ResultBean<Object> addRoom(Long customerId, Long orderId) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        AnychatQueue.Room room = anychatQueue.getRoomByOrderId(orderId);


        return null;
    }
}
