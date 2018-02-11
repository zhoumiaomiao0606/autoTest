package com.yunche.loan.config.filter;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author liuzhe
 * @date 2018/2/11
 */
public class BizPermissionsAuthorizationFilter extends PermissionsAuthorizationFilter {

    private static final Logger logger = LoggerFactory.getLogger(BizPermissionsAuthorizationFilter.class);


    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        return super.onAccessDenied(request, response);
    }
}
