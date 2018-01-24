package com.yunche.loan.domain.param;

import com.yunche.loan.domain.dataObj.DepartmentDO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class DepartmentParam extends DepartmentDO {
    /**
     * 绑定的用户组(角色)列表
     */
    private List<Long> userGroupIdList;
}
