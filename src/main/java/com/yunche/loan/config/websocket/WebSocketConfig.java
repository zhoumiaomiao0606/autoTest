package com.yunche.loan.config.websocket;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.annotation.support.PrincipalMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import java.io.Serializable;
import java.security.Principal;

import static org.springframework.messaging.simp.SimpMessageHeaderAccessor.USER_HEADER;

/**
 * @author liuzhe
 * @date 2018/6/5
 * <p>
 * 通过EnableWebSocketMessageBroker 开启使用STOMP协议来传输基于代理(message broker)的消息,此时浏览器支持使用@MessageMapping 就像支持@RequestMapping一样
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {


    /**
     * registerStompEndpoints方法的作用是websocket建立连接用的（也就是所谓的注册到指定的url[端点])
     * <p>
     * 注册协议节点,并映射指定的URL（端点）
     *
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        /**
         *
         * 注册一个Stomp协议的endpoint，同时支持SockJS，支持跨域.
         *
         *    /videoFace  ->  就是websocket的端点，客户端需要注册这个端点进行 connect
         */
        registry.addEndpoint("/api/v1/videoFace")
                .setAllowedOrigins("*").withSockJS();
    }


    /**
     * 配置一个简单的消息代理(message broker)
     * <p>
     * 配置消息代理，由于我们是实现推送功能，这里的消息代理是/topic
     * <p>
     * <p>
     * 默认情况下会自动配置一个简单的内存消息队列，用来处理“/topic”为前缀的消息，
     * 但经过重载后，消息队列将会处理前缀为“/topic”、“/user”的消息，
     * 并会将“/app”的消息转给controller处理
     *
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        /**
         * 订阅Broker名称 消息代理      作用：仅作为标识，好区分而已
         *
         *
         *      /topic： 广播式用（标识）
         *
         *      /queue： 点对点式用（标识）
         *
         *      Tips：这里加了代理前缀之后，在  订阅  及  @SendToUser、@SendTo  的路径中，  就必须加上 前缀  -> /topic 、 /queue
         *      Tips：这里加了代理前缀之后，在  订阅  及  @SendToUser、@SendTo  的路径中，  就必须加上 前缀  -> /topic 、 /queue
         *      Tips：这里加了代理前缀之后，在  订阅  及  @SendToUser、@SendTo  的路径中，  就必须加上 前缀  -> /topic 、 /queue
         */
        registry.enableSimpleBroker("/topic", "/queue");


        /**
         * server -> client    发送消息统一前缀
         *
         * Tips：不设置的话，默认值为：/user/
         *
         * eg：@SendToUser("/a/b")
         *                             ->  @SendTo     等同于 convertAndSend
         *                             ->  @SendToUser 等同于 convertAndSendToUser
         *
         *      设置前缀为：/user_prefix/                  则最终destination为：/user_prefix/{user}/a/b
         *
         *      若不给定前缀，则会使用默认前缀： /user/       则最终destination为：/user/{user}/a/b
         *
         *
         *                 ==>  {user} 为 convertAndSendToUser 的第一个参数：user
         *
         *                                              ->  关于user 是client 从 header 中传递过来的
         *
         *  @see  PrincipalMethodArgumentResolver#resolveArgument
         *
         *  @see USER_HEADER
         *
         *
         */
        registry.setUserDestinationPrefix("/user/");


        /**
         * 走@MessageMapping时的请求前缀
         *
         * client -> server    MessageMapping统一前缀     不设置，则无前缀!
         *
         * eg：@MessageMapping("/call")
         *
         *      设置前缀为：/app   则：client发送消息给server时，send路径为：/app/call
         *
         *      不设置            则：send路径为：/call
         */
//        registry.setApplicationDestinationPrefixes("/ws");
        registry.setApplicationDestinationPrefixes("/api/v1/ws");


        /**
         * 可以已“.”来分割路径，看看类级别的@messageMapping和方法级别的@messageMapping
         */
//        registry.setPathMatcher(new AntPathMatcher("."));

    }


    /**
     * 配置客户端入站通道拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(createUserInterceptor());
    }

    /**
     * 将客户端渠道拦截器加入spring ioc容器
     *
     * @return
     */
    @Bean
    public UserInterceptor createUserInterceptor() {
        return new UserInterceptor();
    }

    class UserInterceptor extends ChannelInterceptorAdapter {

        /**
         * 拦截处理：
         * 获取包含在stomp中的用户信息  用webSocket会话ID作为user
         */
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {

            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//            StompCommand command = accessor.getCommand();
//            String destination = accessor.getDestination();
//
//            Principal user = accessor.getUser();
//            String sessionId = accessor.getSessionId();
//
//            String headerJSON = JSON.toJSONString(accessor.getMessageHeaders());

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                // webSocketSessionId
//                String webSocketSessionId = SessionUtils.getWebSocketSessionId();

                // webSocketSessionId
                Object simpSessionId = message.getHeaders().get(SimpMessageHeaderAccessor.SESSION_ID_HEADER);

                // 设置当前访问器的认证用户         即：convertAndSendToUser() 中的 user参数
                accessor.setUser(new UserPrincipal(String.valueOf(simpSessionId)));
            }

            return super.preSend(message, channel);
        }
    }

    @Data
    public class UserPrincipal implements Principal, Serializable {
        private String name;

        public UserPrincipal(String name) {
            this.name = name;
        }

        public UserPrincipal() {
        }
    }

}
