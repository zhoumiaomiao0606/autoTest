package com.yunche.loan.config.util;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.UserGroupDO;
import com.yunche.loan.mapper.UserGroupDOMapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/3/15
 */
public class SessionUtils {

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;


    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static EmployeeDO getLoginUser() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (null == principal) {
            SecurityUtils.getSubject().logout();
            throw new BizException("操作会话已过期，请重新登录！");
        } else {
            EmployeeDO loginUser = new EmployeeDO();
            BeanUtils.copyProperties(principal, loginUser);
            return loginUser;
        }
    }

    /**
     * 获取当前登录用户-用户组列表
     *
     * @return
     */
//    public List<String> getUserGroupNameList() {
//        // getUser
//        EmployeeDO loginUser = SessionUtils.getLoginUser();
//
//        // getUserGroup
//        List<UserGroupDO> baseUserGroup = userGroupDOMapper.getBaseUserGroupByEmployeeId(loginUser.getId());
//
//        // getUserGroupName
//        List<String> userGroupNameList = null;
//        if (!CollectionUtils.isEmpty(baseUserGroup)) {
//            userGroupNameList = baseUserGroup.parallelStream()
//                    .filter(Objects::nonNull)
//                    .map(e -> {
//                        return e.getName();
//                    })
//                    .collect(Collectors.toList());
//        }
//        return userGroupNameList;
//    }
}
