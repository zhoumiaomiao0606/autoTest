package com.yunche.loan.domain.param;

import com.yunche.loan.domain.dataObj.EmployeeDO;
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
     * 旧密码
     */
    private String oldPassword;
    /**
     * 新密码
     */
    private String newPassword;
}
