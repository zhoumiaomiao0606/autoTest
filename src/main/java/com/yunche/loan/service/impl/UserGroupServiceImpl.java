package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.UserGroupDOMapper;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.UserGroupQuery;
import com.yunche.loan.domain.dataObj.UserGroupDO;
import com.yunche.loan.domain.param.UserGroupParam;
import com.yunche.loan.domain.viewObj.AuthVO;
import com.yunche.loan.domain.viewObj.UserGroupVO;
import com.yunche.loan.service.UserGroupService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Service
@Transactional
public class UserGroupServiceImpl implements UserGroupService {

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;


    @Override
    public ResultBean<Long> create(UserGroupParam userGroupParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(userGroupParam.getName()), "用户组名称不能为空");
        Preconditions.checkNotNull(userGroupParam.getDepartmentId(), "对应部门不能为空");

        // 创建实体，并返回ID
        Long id = insertAndGetId(userGroupParam);

        // 绑定权限列表
        bindAuth(id, userGroupParam.getAuthIdList());

        // 绑定员工列表
        bindEmployee(id, userGroupParam.getEmployeeIdList());

        return ResultBean.ofSuccess(id, "创建成功");
    }

    @Override
    public ResultBean<Void> update(UserGroupDO userGroupDO) {
        Preconditions.checkNotNull(userGroupDO.getId(), "id不能为空");

        userGroupDO.setGmtModify(new Date());
        int count = userGroupDOMapper.updateByPrimaryKeySelective(userGroupDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = userGroupDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<UserGroupVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        UserGroupDO userGroupDO = userGroupDOMapper.selectByPrimaryKey(id, VALID_STATUS);
        Preconditions.checkNotNull(userGroupDO, "id有误，数据不存在");

        UserGroupVO userGroupVO = new UserGroupVO();
        BeanUtils.copyProperties(userGroupDO, userGroupVO);

        return ResultBean.ofSuccess(userGroupVO);
    }

    @Override
    public ResultBean<List<UserGroupVO>> query(UserGroupQuery query) {
        int totalNum = userGroupDOMapper.count(query);
        if (totalNum < 1) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<UserGroupDO> userGroupDOS = userGroupDOMapper.query(query);
        if (CollectionUtils.isEmpty(userGroupDOS)) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<UserGroupVO> departmentVOS = userGroupDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    UserGroupVO userGroupVO = new UserGroupVO();
                    BeanUtils.copyProperties(e, userGroupVO);
                    return userGroupVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(departmentVOS, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<AuthVO>> listAuth(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "id不能为空");

        return null;
    }

    @Override
    public ResultBean<List<UserGroupVO.RelaEmployeeVO>> listEmployee(BaseQuery query) {
        Preconditions.checkNotNull(query.getId(), "id不能为空");

        return null;
    }

    @Override
    public ResultBean<Void> deleteRelaAuths(Long id, String authIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(authIds), "权限ID列表不能为空");

        String[] authIdArr = authIds.split(",");


        return null;
    }

    @Override
    public ResultBean<Void> deleteRelaEmployees(Long id, String employeeIds) {
        Preconditions.checkNotNull(id, "用户组ID不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(employeeIds), "员工ID列表不能为空");

        String[] employeeIdArr = employeeIds.split(",");


        return null;
    }


    /**
     * 插入实体，并返回主键ID
     *
     * @param userGroupParam
     * @return
     */
    private Long insertAndGetId(UserGroupParam userGroupParam) {
        List<String> nameList = userGroupDOMapper.getAllName(VALID_STATUS);
        Preconditions.checkArgument(!nameList.contains(userGroupParam.getName()), "用户组名称已存在");

        UserGroupDO userGroupDO = new UserGroupDO();
        BeanUtils.copyProperties(userGroupParam, userGroupDO);
        userGroupDO.setStatus(VALID_STATUS);
        userGroupDO.setGmtCreate(new Date());
        userGroupDO.setGmtModify(new Date());

        int count = userGroupDOMapper.insertSelective(userGroupDO);
        Preconditions.checkArgument(count > 0, "创建失败");

        return userGroupDO.getId();
    }

    /**
     * TODO 绑定权限列表
     *
     * @param id
     * @param authIdList
     */
    private void bindAuth(Long id, List<Long> authIdList) {


    }

    /**
     * TODO 绑定员工列表
     *
     * @param id
     * @param employeeIdList
     */
    private void bindEmployee(Long id, List<Long> employeeIdList) {


    }
}
