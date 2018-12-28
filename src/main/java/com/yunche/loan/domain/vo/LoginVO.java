package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.EmployeeDO;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Set;

import static com.yunche.loan.service.impl.PermissionServiceImpl.USER_GROUP_ADMIN;

/**
 * @author liuzhe
 * @date 2018/2/8
 */
@Data
public class LoginVO extends EmployeeDO {

    private Long userId;

    private String username ;




    private Set<String> userGroupSet;
    /**
     * 是否为管理员
     */
    private Boolean isAdmin = false;

    public Boolean getIsAdmin() {
        if (!CollectionUtils.isEmpty(userGroupSet) && userGroupSet.contains(USER_GROUP_ADMIN)) {
            return true;
        }
        return false;
    }
}
