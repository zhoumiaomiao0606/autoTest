package com.yunche.loan.config.common;

import com.google.common.collect.Maps;
import com.yunche.loan.config.filter.BizFormAuthenticationFilter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.Map;

import static org.apache.shiro.web.mgt.CookieRememberMeManager.DEFAULT_REMEMBER_ME_COOKIE_NAME;
import static org.apache.shiro.web.servlet.Cookie.ONE_YEAR;
import static org.apache.shiro.web.servlet.Cookie.ROOT_PATH;


/**
 * shiro配置文件
 *
 * @author liuzhe
 * @date 2018/2/6
 */
@Configuration
public class ShiroConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.shiro.anno}")
    private Boolean anno;

    /**
     * session过期时间：30天（单位：秒）
     */
    public static final Integer SESSION_EXPIRE = 3600 * 24 * 30;

    /**
     * 连接到Redis的超时时间：2s（单位：毫秒）
     */
    private static final Integer CONNECT_TO_REDIS_TIMEOUT = 2000;


    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();

        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        // 自定义filter替换authc
        BizFormAuthenticationFilter authcFilter = new BizFormAuthenticationFilter();
        filters.put("authc", authcFilter);
        // 自定义权限过滤器替换perms
//        BizPermissionsAuthorizationFilter permsFilter = new BizPermissionsAuthorizationFilter();
//        filters.put("perms", permsFilter);
        shiroFilterFactoryBean.setFilters(filters);


        // 注意过滤器配置顺序 不能颠倒
        Map<String, String> filterChainDefinitionMap = Maps.newLinkedHashMap();
        if (anno) {
            filterChainDefinitionMap.put("/**", "anon");
        } else {
            filterChainDefinitionMap.put("/static/**", "anon");
            filterChainDefinitionMap.put("/templates/**", "anon");

            // websocket检查服务放行
            filterChainDefinitionMap.put("/api/v1/videoFace/info", "anon");

            filterChainDefinitionMap.put("/api/v1/employee/logout", "anon");
            filterChainDefinitionMap.put("/api/v1/employee/login", "anon");
            filterChainDefinitionMap.put("/api/v1/app/version/check", "anon");
            filterChainDefinitionMap.put("/api/v1/loadqr/apk", "anon");
            filterChainDefinitionMap.put("/api/v1/loadqr/query", "anon");

            filterChainDefinitionMap.put("/api/v1/loanorder/icbc/creditresult", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/icbc/creditreturn", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/icbc/multimediareturn", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/icbc/creditcardresult", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/icbc/fileNotice", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/icbc/artificialgainImage", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/opencard/openCard", "anon");
            filterChainDefinitionMap.put("/api/v1/app/loanorder/zhonganquery", "anon");
            filterChainDefinitionMap.put("/api/v1/app/loanorder/zhongandetail", "anon");
            filterChainDefinitionMap.put("/api/v1/app/loanorder/zhonganname", "anon");
            filterChainDefinitionMap.put("/api/v1/msg/creditDetail", "anon");
            filterChainDefinitionMap.put("/api/v1/app/msg/creditDetail", "anon");
            filterChainDefinitionMap.put("/api/v1/ext/**", "anon");
            filterChainDefinitionMap.put("/api/v1/app/loanorder/chart/parter/**", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/accommodation/jtxresult", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/icbc/multimediaupload", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/material/down2oss", "anon");
            filterChainDefinitionMap.put("/api/v1/loanorder/universal/oss", "anon");
            filterChainDefinitionMap.put("/api/v1/car/list", "anon");
            filterChainDefinitionMap.put("/api/v1/car/detail/query", "anon");
            filterChainDefinitionMap.put("/api/v1/secondHandCar/queryCarTypeByVIN**", "anon");
            filterChainDefinitionMap.put("/**", "authc");
//            filterChainDefinitionMap.put("/**", "authc,perms");
        }

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();

        // 自定义Realm
        securityManager.setRealm(bizShiroRealm());

        // 自定义session管理 使用redis
        securityManager.setSessionManager(sessionManager());

        // 自定义缓存实现 使用redis
        securityManager.setCacheManager(cacheManager());

        // RememberMe
        securityManager.setRememberMeManager(rememberMeManager());

        return securityManager;
    }

    @Bean
    public BizShiroRealm bizShiroRealm() {
        BizShiroRealm bizShiroRealm = new BizShiroRealm();
        return bizShiroRealm;
    }

    /**
     * 自定义sessionManager
     *
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        BizSessionManager bizSessionManager = new BizSessionManager();

        // sessionDAO
        bizSessionManager.setSessionDAO(redisSessionDAO());

        // 删除过期的session
        bizSessionManager.setDeleteInvalidSessions(true);

        // 是否定时检查session
        bizSessionManager.setSessionValidationSchedulerEnabled(true);

        // 设置全局session超时时间
        bizSessionManager.setGlobalSessionTimeout(SESSION_EXPIRE * 1000);

        // cookie
        SimpleCookie sessionIdCookie = new SimpleCookie("sid");
        sessionIdCookie.setHttpOnly(true);
        sessionIdCookie.setMaxAge(SESSION_EXPIRE);
        sessionIdCookie.setPath(ROOT_PATH);
        bizSessionManager.setSessionIdCookie(sessionIdCookie);

        return bizSessionManager;
    }

    /**
     * shiro缓存管理器
     * <p>
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager() {

        RedisCacheManager redisCacheManager = new RedisCacheManager();

        redisCacheManager.setRedisManager(redisManager());

        // TODO redis中针对不同用户缓存
        redisCacheManager.setPrincipalIdFieldName("id");

        // TODO 用户权限信息缓存时间
//        redisCacheManager.setExpire(1800);

        return redisCacheManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * <p>
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setExpire(SESSION_EXPIRE);
        return redisSessionDAO;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式,所以需要开启代码支持
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 配置shiro redisManager
     * <p>
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setPassword(password);
        redisManager.setTimeout(CONNECT_TO_REDIS_TIMEOUT);
        return redisManager;
    }


    /**
     * RememberMe
     *
     * @return
     */
    public CookieRememberMeManager rememberMeManager() {

        Cookie cookie = new SimpleCookie(DEFAULT_REMEMBER_ME_COOKIE_NAME);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(ONE_YEAR);

        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(cookie);

        return cookieRememberMeManager;
    }
}
