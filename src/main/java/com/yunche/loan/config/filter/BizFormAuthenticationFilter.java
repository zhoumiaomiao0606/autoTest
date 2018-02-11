package com.yunche.loan.config.filter;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.constant.BaseExceptionEnum;
import com.yunche.loan.config.result.ResultBean;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 替换authc拦截器
 *
 * @author liuzhe
 * @date 2018/2/9
 */
public class BizFormAuthenticationFilter extends FormAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(BizFormAuthenticationFilter.class);


    /**
     * 未登录状态下：所有请求都会经过的方法。
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) {
        // 放行所有OPTIONS请求
        String method = WebUtils.toHttp(request).getMethod();
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(method)) {
            return true;
        }

        // 未登录，拦截并返回自定义JSON错误信息
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            ResultBean result = ResultBean.ofError(BaseExceptionEnum.NOT_LOGIN);
            out.write(JSON.toJSONString(result));
            out.flush();
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

        return false;
    }
}
