package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

        List<String> userGroupNameList = getUserGroupNameList();

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
    public List<String> getUserGroupNameList() {
        // getUser
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        // getUserGroup
        List<UserGroupDO> baseUserGroup = userGroupDOMapper.getBaseUserGroupByEmployeeId(loginUser.getId());

        // getUserGroupName
        List<String> userGroupNameList = null;
        if (!CollectionUtils.isEmpty(baseUserGroup)) {
            userGroupNameList = baseUserGroup.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        return e.getName();
                    })
                    .collect(Collectors.toList());
        }
        return userGroupNameList;
    }
}
