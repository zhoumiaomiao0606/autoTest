package com.yunche.loan;

import com.yunche.loan.domain.param.WebSocketParam;
import com.yunche.loan.service.WebSocketService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_APP;
import static com.yunche.loan.config.constant.VideoFaceConst.TYPE_PC;

/**
 * @author liuzhe
 * @date 2018/6/11
 */
public class WebSocketTest extends BaseTest {

    @Autowired
    private WebSocketService webSocketService;


    @Test
    public void test1() {

        WebSocketParam webSocketParam = new WebSocketParam();
        webSocketParam.setBankId(1L);
        webSocketParam.setUserId(1L);
        webSocketParam.setType(TYPE_PC);
        webSocketService.waitTeam(webSocketParam);

        System.out.println("-----");

    }
}
