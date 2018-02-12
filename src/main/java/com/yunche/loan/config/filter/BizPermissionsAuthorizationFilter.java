package com.yunche.loan.config.filter;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.constant.BaseExceptionEnum;
import com.yunche.loan.config.result.ResultBean;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author liuzhe
 * @date 2018/2/11
 */
public class BizPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {

    private static final Logger logger = LoggerFactory.getLogger(BizPermissionsAuthorizationFilter.class);


    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {

        Subject subject = getSubject(request, response);
        String[] perms = (String[]) mappedValue;

        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (!subject.isPermitted(perms[0])) {
                    // 未授权，拦截并返回自定义JSON错误信息
                    returnJsonException(response);
                    isPermitted = false;
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    // 未授权，拦截并返回自定义JSON错误信息
                    returnJsonException(response);
                    isPermitted = false;
                }
            }
        }

        return isPermitted;
    }

    /**
     * 写回自定义JSON错误信息
     *
     * @param response
     */
    private void returnJsonException(ServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            ResultBean result = ResultBean.ofError(BaseExceptionEnum.NOT_PERMISSION);
            out.write(JSON.toJSONString(result));
            out.flush();
        } catch (IOException e) {
            logger.error("IOException", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
