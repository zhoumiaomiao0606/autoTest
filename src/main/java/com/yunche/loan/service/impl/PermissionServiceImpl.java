package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.ActivitiCache;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.mapper.UserGroupDOMapper;
import com.yunche.loan.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/4/10
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    /**
     * 管理员角色
     */
    public static final String USER_GROUP_ADMIN = "管理员";
    /**
     * 菜单：配置中心
     */
    public static final String MENU_CONFIGURE_CENTER = "configure_center";

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;

    @Autowired
    private ActivitiCache activitiCache;


    @Override
    public void checkTaskPermission(String taskDefinitionKey) {

        Set<String> userGroupNameSet = getUserGroupNameSet();

        if (!CollectionUtils.isEmpty(userGroupNameSet) && userGroupNameSet.contains(USER_GROUP_ADMIN)) {
            return;
        }

        Map<String, List<String>> taskDefinitionKeyCandidateGroupsMap = activitiCache.get();

        List<String> currentTaskCandidateGroups = taskDefinitionKeyCandidateGroupsMap.get(taskDefinitionKey);

        // 若需要角色权限
        if (!CollectionUtils.isEmpty(currentTaskCandidateGroups)) {

            Preconditions.checkArgument(!CollectionUtils.isEmpty(userGroupNameSet),
                    "您无权操作[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]");

            List<String> candidateGroups = Lists.newArrayList();
            currentTaskCandidateGroups.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        if (userGroupNameSet.contains(e)) {
                            candidateGroups.add(e);
                        }
                    });

            Preconditions.checkArgument(!CollectionUtils.isEmpty(candidateGroups),
                    "您无权操作[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]");
        }
    }

    /**
     * 获取用户组名称
     *
     * @return
     */
    @Override
    public Set<String> getUserGroupNameSet() {
        // getUser
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        Set<String> allUserGroupNameSet = Sets.newHashSet();

        // 员工-直接关联的用户组
        List<String> userGroupNameList = userGroupDOMapper.listUserGroupNameByEmployeeId(loginUser.getId());

        // 员工-所属部门 -间接关联的用户组
//        List<String> userGroupNameList_ = userGroupDOMapper.listUserGroupNameByEmployeeIdRelaDepartment(loginUser.getId());

        allUserGroupNameSet.addAll(userGroupNameList);
//        allUserGroupNameSet.addAll(userGroupNameList_);

        // TODO 有[管理员]角色，则赋予所有权限
        if (!CollectionUtils.isEmpty(allUserGroupNameSet) && allUserGroupNameSet.contains(USER_GROUP_ADMIN)) {
            // 所有角色
//            List<String> allUserGroupName = userGroupDOMapper.getAllName(VALID_STATUS);
//            allUserGroupNameSet.addAll(allUserGroupName);

            // 配置中心
            allUserGroupNameSet.add(MENU_CONFIGURE_CENTER);
        }

        allUserGroupNameSet.removeAll(Collections.singleton(null));

        return allUserGroupNameSet;
    }
}
