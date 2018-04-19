package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.EmployeeDO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class EmployeeParam extends EmployeeDO {
    /**
     * 绑定的用户组(角色)列表
     */
    private List<Long> userGroupIdList;
    /**
     * 登录名
     */
    private String username;
    /**
     * 旧密码
     */
    private String oldPassword;
    /**
     * 新密码
     */
    private String newPassword;
    /**
     * 是否为移动端登录
     */
    private Boolean isTerminal = false;

    /**
     * 机器id
     */
    private String machineId;

    /**
     * 记住我  -单位：天
     */
    private Integer rememberMe;
}
