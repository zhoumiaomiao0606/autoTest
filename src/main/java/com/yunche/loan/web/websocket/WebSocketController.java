package com.yunche.loan.web.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
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
                "来自：convertAndSendToUser"/*,
                createHeaders(sessionId, new InvocableHandlerMethod.AsyncResultMethodParameter(returnValue))*/);

//        HandlerMethod handlerMethod = handlerMethod.createWithResolvedBean();
//        InvocableHandlerMethod invocable = new InvocableHandlerMethod(handlerMethod);
////        invocable.setMessageMethodArgumentResolvers(this.argumentResolvers);
//        invocable.setMessageMethodArgumentResolvers(new HandlerMethodArgumentResolverComposite());
//        MethodParameter returnType = handlerMethod.getReturnType();


//        simpleBrokerMessageHandler.

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

    //    @GetMapping("/videoFace/info")
    @ResponseBody
    public JSONObject info() {
        JSONObject info = new JSONObject();
        info.put("origins", Lists.newArrayList("*"));
        info.put("cookie_needed", true);
        info.put("websocket", true);
        info.put("entropy", 278083309);
        return info;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    //    @GetMapping("/sockjs.min.js")
    public String sockjs() {
        return "sockjs.min.js";
    }

    //    @GetMapping("/stomp.min.js")
    public String stomp() {
        return "sockjs.min.js";
    }

    //    @GetMapping("/jquery.js")
    public String jquery() {
        return "jquery.js";
    }

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


//    @MessageMapping("/queue")
////  @SendTo("/topic/queue")  // 等同于 convertAndSend          -> 会把方法的返回值广播到指定主题（“主题”这个词并不合适）
//    public void toTopic(String bankId) {
//
//        videoFaceRoomService.sendMsgToQueueUser(bankId);
//
//
//        System.out.println(msg.getName() + "," + msg.getMsg());
//        simpMessagingTemplate.convertAndSend("/topic/queue/" + bankId, msg.getName() + "," + msg.getMsg());
////      return "消息内容："+ msg.getName()+"--"+msg.getMsg();
//
//
//        // 向用户发送一条消息            第一个参数是：浏览器中订阅消息的地址；第二个参数是：消息本身；
//        simpMessagingTemplate.convertAndSend("/message", "发送的消息");
//    }
//
//    @MessageMapping("/message")
////  @SendToUser("/message") // 等同于 convertAndSendToUser     -> 把返回值发到指定队列（“队列”实际不是队列，而是跟上面“主题”类似的东西，只是spring在SendTo的基础上加了用户的内容而已）
//    public void toUser(SocketMessageVo msg) {
//
//
//        simpMessagingTemplate.convertAndSendToUser("123", "/message", msg.getName() + msg.getMsg());
//
//
//        // 向用户发送一条消息            第一个参数是：目标用户用户名；第二个参数是：浏览器中订阅消息的地址；第三个参数是：消息本身；
//        simpMessagingTemplate.convertAndSendToUser("userId", "/message", "要发送的消息");
//    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//    /**
//     * 广播
//     * <p>
//     * SendTo 发送至 Broker 下的指定订阅路径
//     *
//     * @param clientMessage
//     * @return
//     */
//    // @MessageMapping注解和我们之前使用的@RequestMapping类似
//    @MessageMapping("/welcome")
//    // @SendTo注解表示当服务器有消息需要推送的时候，会对订阅了@SendTo中路径的浏览器发送消息
//    @SendTo("/topic/getResponse")
//    public ServerMessage say(ClientMessage clientMessage) {
//
//        System.out.println("clientMessage.getName() = " + clientMessage.getName());
//
//        return new ServerMessage("Welcome , " + clientMessage.getName() + " !");
//    }
//
//
//    /**
//     * 注入SimpMessagingTemplate 用于向浏览器点对点消息发送
//     */
//    @Autowired
//    private SimpMessageSendingOperations simpMessagingTemplate;
//
//    /**
//     * 点对点
//     *
//     * @param toUserMessage
//     */
//    @MessageMapping("/cheat")
//    // 发送的订阅路径为/user/{userId}/message
//    // /user/路径是默认的一个，如果想要改变，必须在config 中setUserDestinationPrefix
//    public void cheatTo(ToUserMessage toUserMessage) {
//
//
//        System.out.println("toUserMessage.getMessage() = " + toUserMessage.getMessage());
//        System.out.println("toUserMessage.getUserId() = " + toUserMessage.getUserId());
//
//        // 向用户发送一条消息，第一个参数是目标用户用户名，第二个参数是浏览器中订阅消息的地址，第三个参数是消息本身
//        simpMessagingTemplate.convertAndSendToUser(toUserMessage.getUserId(), "/message", "发送的消息");
//
//    }


//    @MessageMapping("/chat")
//    //在springmvc 中可以直接获得principal,principal 中包含当前用户的信息
//    public void handleChat(Principal principal, Message message) {
//
//        /**
//         * 此处是一段硬编码。如果发送人是abel 则发送给 admin，  如果发送人是admin 就发送给 abel。
//         * 通过当前用户,然后查找消息,如果查找到未读消息,则发送给当前用户。
//         */
//        if (principal.getName().equals("admin")) {
//            //通过convertAndSendToUser 向用户发送信息,
//            // 第一个参数是接收消息的用户,第二个参数是浏览器订阅的地址,第三个参数是消息本身
//
//            messagingTemplate.convertAndSendToUser("abel",
//                    "/queue/notifications", principal.getName() + "-send:"
//                            + message.getName());
//            /**
//             * 72 行操作相等于
//             * messagingTemplate.convertAndSend("/user/abel/queue/notifications",principal.getName() + "-send:"
//             + message.getName());
//             */
//        } else {
//            messagingTemplate.convertAndSendToUser("admin",
//                    "/queue/notifications", principal.getName() + "-send:"
//                            + message.getName());
//        }
//
//        // 发送到topic     订阅了该topic的人都将收到消息
//        messagingTemplate.convertAndSend("destination", "payload");
//
//        // 发送到个人       点对点
//        messagingTemplate.convertAndSendToUser("userId", "destination", "payload");
//    }
}
