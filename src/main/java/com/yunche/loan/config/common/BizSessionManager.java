package com.yunche.loan.config.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;

/**
 * @author liuzhe
 * @date 2018/2/6
 */
public class BizSessionManager extends DefaultWebSessionManager {

    /**
     * 重写getSessionId方法
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        // 如果参数中有token,则其值为sessionId
        String sessionId = WebUtils.toHttp(request).getParameter("token");
        if (StringUtils.isNotBlank(sessionId)) {
            return sessionId;
        } else {
            // 否则按默认规则从cookie取sessionId
            return super.getSessionId(request, response);
        }
    }
}
