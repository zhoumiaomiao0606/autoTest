package com.yunche.loan.service.impl;

import com.bairuitech.anychat.AnyChatServerSDK;
import com.bairuitech.anychat.main.AnyChatSDK;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.VideoFaceLogDOMapper;
import com.yunche.loan.service.VideoFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
@Service
public class VideoFaceServiceImpl implements VideoFaceService {

    @Autowired
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

//    @PostConstruct
    public ResultBean test() {

//        int[] ints = AnyChatServerSDK.GetRoomIdList();

        // /Users/liuzhe/Library/Java/Extensions:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java:.
//        String library = System.getProperty("java.library.path");

//        AnyChatSDK instance = AnyChatSDK.getInstance();


        return ResultBean.ofSuccess(null);
    }

}
