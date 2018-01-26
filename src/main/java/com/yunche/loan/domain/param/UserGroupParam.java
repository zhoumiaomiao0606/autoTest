package com.yunche.loan.domain.param;

import com.yunche.loan.domain.dataObj.UserGroupDO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class UserGroupParam extends UserGroupDO {
    /**
     * 部门ID
     */
    private Long departmentId;
    /**
     * 区域(城市)ID
     */
    private Long areaId;
    /**
     * 权限ID列表
     */
    private List<Long> authIdList;
    /**
     * 员工ID列表
     */
    private List<Long> employeeIdList;
}
