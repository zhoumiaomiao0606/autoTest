package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.ActivitiCache;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.UserGroupDO;
import com.yunche.loan.mapper.UserGroupDOMapper;
import com.yunche.loan.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/4/10
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;

    @Autowired
    private ActivitiCache activitiCache;


    @Override
    public void checkTaskPermission(String taskDefinitionKey) {

        Set<String> userGroupNameList = getUserGroupNameSet();

        Map<String, List<String>> taskDefinitionKeyCandidateGroupsMap = activitiCache.get();

        List<String> currentTaskCandidateGroups = taskDefinitionKeyCandidateGroupsMap.get(taskDefinitionKey);

        // 若需要角色权限
        if (!CollectionUtils.isEmpty(currentTaskCandidateGroups)) {

            Preconditions.checkArgument(!CollectionUtils.isEmpty(userGroupNameList),
                    "您无权审核[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]任务");

            List<String> candidateGroups = Lists.newArrayList();
            currentTaskCandidateGroups.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        if (userGroupNameList.contains(e)) {
                            candidateGroups.add(e);
                        }
                    });

            Preconditions.checkArgument(!CollectionUtils.isEmpty(candidateGroups),
                    "您无权审核[" + LoanProcessEnum.getNameByCode(taskDefinitionKey) + "]任务");
        }
    }

    /**
     * 获取用户组名称
     *
     * @return
     */
    public Set<String> getUserGroupNameSet() {
        // getUser
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        // 员工-直接关联的用户组
        List<String> userGroupNameList = userGroupDOMapper.listUserGroupNameByEmployeeId(loginUser.getId());

        // 员工-所属部门 -间接关联的用户组
        List<String> userGroupNameList_ = userGroupDOMapper.listUserGroupNameByEmployeeIdRelaDepartment(loginUser.getId());

        Set<String> allUserGroupNameList = Sets.newHashSet();
        allUserGroupNameList.addAll(userGroupNameList);
        allUserGroupNameList.addAll(userGroupNameList_);

        return allUserGroupNameList;
    }
}
