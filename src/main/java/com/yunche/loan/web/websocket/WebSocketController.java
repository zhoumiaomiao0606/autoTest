package com.yunche.loan.web.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.param.WebSocketParam;
import com.yunche.loan.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.*;
import org.springframework.messaging.simp.annotation.SendToUser;
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
     * path： websocket://localhost:8001/websocket/queue/wait
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
     * path： websocket://localhost:8001/websocket/queue/exit
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @MessageMapping("/queue/wait/sendToUser")
    @SendToUser("/queue/addQueue/sendToUser")
    @ResponseBody
    public String sendToUser(/*WebSocketSession socketSession,*/
                             WebSocketParam webSocketParam) {

        JSONObject jObj = new JSONObject();
        jObj.put("name", "sendToUser");
        jObj.put("sex", "1");
        jObj.put("age", "16");

        simpMessagingTemplate.convertAndSend("/queue/addQueue/sendTo", JSON.toJSONString(webSocketParam));


        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        String sessionId = simpAttributes.getSessionId();
        System.out.println("webSocketSessionId - " + sessionId);
        System.out.println("simpAttributes - " + JSON.toJSONString(simpAttributes));


        String user = "EmployeeDO(id=1, name=admin, password=41a55b32184ef50d4f93f27f04757572ed8fa13f44060d4d, idCard=null, mobile=null, email=admin@yunche.com, dingDing=admin, departmentId=null, parentId=215, title=null, entryDate=null, type=1, level=1, gmtCreate=Wed Apr 11 12:31:25 CST 2018, gmtModify=Mon Jun 04 15:37:26 CST 2018, status=0, feature=null, machineId=null)";

        simpMessagingTemplate.convertAndSendToUser(user,
                "/queue/addQueue/sendToUser",
                "来自：convertAndSendToUser   -EmployeeDO"/*,
                createHeaders(sessionId, new InvocableHandlerMethod.AsyncResultMethodParameter(returnValue))*/);

        simpMessagingTemplate.convertAndSendToUser("admin",
                "/queue/addQueue/sendToUser",
                "来自：convertAndSendToUser   -username");

        simpMessagingTemplate.convertAndSendToUser(sessionId,
                "/queue/addQueue/sendToUser",
                "来自：convertAndSendToUser   -sessionId");

        simpMessagingTemplate.convertAndSendToUser(SessionUtils.getLoginUser().toString(),
                "/queue/addQueue/sendToUser",
                "来自：convertAndSendToUser");

        return JSON.toJSONString(jObj);
    }


    private MessageHeaders createHeaders(String sessionId, MethodParameter returnType) {

//        SendToMethodReturnValueHandler sendToMethodReturnValueHandler = new SendToMethodReturnValueHandler();

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
//        if (sendToMethodReturnValueHandler.getHeaderInitializer() != null) {
//            sendToMethodReturnValueHandler.getHeaderInitializer().initHeaders(headerAccessor);
//        }
        if (sessionId != null) {
            headerAccessor.setSessionId(sessionId);
        }
        headerAccessor.setHeader(SimpMessagingTemplate.CONVERSION_HINT_HEADER, returnType);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }

    @MessageMapping("/queue/wait/sendTo")
    @SendTo("/queue/addQueue/sendTo")
    @ResponseBody
    public JSONObject sendTo(/*WebSocketClientSockJsSession sockJsSession,*/ WebSocketParam webSocketParam) {

        JSONObject jObj = new JSONObject();
        jObj.put("name", "sendTo");
        jObj.put("sex", "2");
        jObj.put("age", "17");

        simpMessagingTemplate.convertAndSend("/queue/addQueue/sendTo", JSON.toJSONString(webSocketParam));


        SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
        String sessionId = simpAttributes.getSessionId();
        System.out.println("webSocketSessionId - " + sessionId);
        System.out.println("simpAttributes - " + JSON.toJSONString(simpAttributes));


        simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/addQueue/sendToUser", JSON.toJSONString("来自：convertAndSendToUser"));

        return jObj;
    }

    @GetMapping("/queue/wait")
    public void wait_(@RequestParam Byte type,
                      @RequestParam Long bankId,
                      @RequestParam Long userId) {

        WebSocketParam webSocketParam = new WebSocketParam();
        webSocketParam.setType(type);
        webSocketParam.setBankId(bankId);
        webSocketParam.setUserId(userId);

        webSocketService.waitTeam(webSocketParam);
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
