package com.yunche.loan.config.util;

import com.yunche.loan.domain.entity.EmployeeDO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
public class SessionUtils {

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static EmployeeDO getLoginUser() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (null == principal) {
            SecurityUtils.getSubject().logout();
        }

        EmployeeDO loginUser = new EmployeeDO();
        BeanUtils.copyProperties(principal, loginUser);
        return loginUser;
    }
}
