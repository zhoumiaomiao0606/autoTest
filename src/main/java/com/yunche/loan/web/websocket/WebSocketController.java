package com.yunche.loan.web.websocket;

import com.yunche.loan.domain.param.WebSocketParam;
import com.yunche.loan.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author liuzhe
 * @date 2018/6/5
 */
@CrossOrigin
@Controller
@RequestMapping("/api/v1")
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WebSocketService webSocketService;


    /**
     * 加入排队/心跳消息    client -> server
     * <p>
     * path： ws://localhost:8001/api/v1/ws/team/wait
     *
     * @param webSocketParam 前端参数：以JSON格式发送，即可自动映射
     * @MessageMapping client -> server    发送消息的path      若有设置前缀,则还需加上前缀
     */
    @MessageMapping("/team/wait")
    public void waitTeam(WebSocketParam webSocketParam) {

        webSocketService.waitTeam(webSocketParam);
    }

    /**
     * 退出排队     client -> server
     * <p>
     * path： ws://localhost:8001/api/v1/ws/team/exit
     *
     * @param webSocketParam
     */
    @MessageMapping("/team/exit")
    public void exitTeam(WebSocketParam webSocketParam) {

        webSocketService.exitTeam(webSocketParam);
    }

    /**
     * PC端发起通话请求
     * <p>
     * 推送roomId给APP、PC        APP接通后，需要主动退出排队
     *
     * @param webSocketParam
     */
    @MessageMapping("/call")
    public void call(WebSocketParam webSocketParam) {

        webSocketService.call(webSocketParam);
    }

    /**
     * 图片存储路径转发
     *
     * @param webSocketParam
     */
    @MessageMapping("/livePhoto/path")
    public void livePhotoPath(WebSocketParam webSocketParam) {

        webSocketService.livePhotoPath(webSocketParam);
    }

    /**
     * 经纬度转发
     *
     * @param webSocketParam
     */
    @MessageMapping("/latlon")
    public void latlon(WebSocketParam webSocketParam) {

        webSocketService.latlon(webSocketParam);
    }

    /**
     * APP端 网络环境实时 转发
     *
     * @param webSocketParam
     */
    @MessageMapping("/network")
    public void network(WebSocketParam webSocketParam) {

        webSocketService.network(webSocketParam);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GetMapping("/chat_app")
    public String chat_app() {
        return "chat_app";
    }

    @GetMapping("/chat_pc")
    public String chat_pc() {
        return "chat_pc";
    }

    @GetMapping("/chat_sendToUser")
    public String chat_sendToUser() {
        return "chat_sendToUser";
    }

    @GetMapping("/chat_sendTo")
    public String chat_sendTo() {
        return "chat_sendTo";
    }

    @GetMapping("/chat_exit")
    public String chat_exit() {
        return "chat_exit";
    }

    @GetMapping("/chat_wait")
    public String chat_wait() {
        return "chat_wait";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/ws")
    public String ws() {
        return "websocket";
    }

    @GetMapping("/sendMsg")
    public void sendMsg(HttpSession session) {
        System.out.println("测试发送消息：随机消息" + session.getId());
        simpMessagingTemplate.convertAndSendToUser("123", "/message", "后台具体用户消息");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
