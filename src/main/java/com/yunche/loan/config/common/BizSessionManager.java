package com.yunche.loan.config.common;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

import static com.yunche.loan.service.impl.CarServiceImpl.NEW_LINE;

/**
 * @author liuzhe
 * @date 2018/2/6
 */
public class BizSessionManager extends DefaultWebSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(BizSessionManager.class);


    private static final String AUTHORIZATION = "Authorization";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";


    public BizSessionManager() {
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        Cookie[] cookies = WebUtils.toHttp(request).getCookies();
        logger.info("--------------------------" + NEW_LINE +
                JSON.toJSONString(cookies)
                + NEW_LINE + "--------------------------");

        //  如果参数中有token,则其值为sessionId
        String sessionId = WebUtils.toHttp(request).getParameter("token");

        // 如果请求头中有 Authorization 则其值为sessionId
//        String sessionId = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        if (StringUtils.isNotBlank(sessionId)) {
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sessionId;
        } else {
            // 否则按默认规则从cookie取sessionId
            return super.getSessionId(request, response);
        }

//        try {
//
//        } catch (UnknownSessionException ex) {
//            // sessionId 不存在
//        }
    }

}
