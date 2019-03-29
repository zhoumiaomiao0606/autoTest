package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.YuncheBoardDO;
import com.yunche.loan.domain.param.YuncheBoardParam;
import com.yunche.loan.mapper.EmployeeRelaUserGroupDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.mapper.UserGroupRelaBankDOMapper;
import com.yunche.loan.mapper.YuncheBoardDOMapper;
import com.yunche.loan.service.YuncheBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.ONE;

@Service
@Transactional
public class YuncheBoardServiceImpl implements YuncheBoardService
{
    @Autowired
    private YuncheBoardDOMapper yuncheBoardDOMapper;

    @Autowired
    private UserGroupRelaBankDOMapper userGroupRelaBankDOMapper;

    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Override
    public ResultBean<Long> create(YuncheBoardParam yuncheBoardParam)
    {
        return null;
    }

    @Override
    public ResultBean update(YuncheBoardParam yuncheBoardParam)
    {

        //VehicleHandleDO vehicleHandleDO =new VehicleHandleDO();
        //BeanUtils.copyProperties(param, vehicleHandleDO);
        if (null == yuncheBoardParam.getId())
        {
            // create
            yuncheBoardParam.setApplyMan(SessionUtils.getLoginUser().getName());

            if (ONE.equals(yuncheBoardParam.getStatus()))
            {
                yuncheBoardParam.setPublishTime(new Date());
            }
            int count = yuncheBoardDOMapper.insertSelective(yuncheBoardParam);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            yuncheBoardParam.setApplyMan(SessionUtils.getLoginUser().getName());
            if (ONE.equals(yuncheBoardParam.getStatus()))
            {
                yuncheBoardParam.setPublishTime(new Date());
            }
            int count = yuncheBoardDOMapper.updateByPrimaryKeySelective(yuncheBoardParam);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(yuncheBoardParam.getId(), "保存成功");
    }

    @Override
    public ResultBean listAll(YuncheBoardParam yuncheBoardParam) {
        PageHelper.startPage(yuncheBoardParam.getPageIndex(), yuncheBoardParam.getPageSize(), true);
        List<YuncheBoardDO> list = yuncheBoardDOMapper.selectBoards(yuncheBoardParam);
        PageInfo<YuncheBoardDO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<Void> delete(Integer id)
    {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = yuncheBoardDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public YuncheBoardDO detail(Integer id)
    {
        Preconditions.checkNotNull(id,"id不能为空！！！");
        return yuncheBoardDOMapper.selectByPrimaryKey(id);
    }

    @Override
    public ResultBean listAllByLoginUser(YuncheBoardParam yuncheBoardParam)
    {

        // 登录用户权限
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        //判断是否是合伙人级别-还是管理员
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        yuncheBoardParam.setMaxGroupLevel(maxGroupLevel);


        //获取用户可见的银行
        yuncheBoardParam.setBankList(getUserHaveBank(loginUser.getId()));
        SessionUtils.getLoginUser().getId();
        PageHelper.startPage(yuncheBoardParam.getPageIndex(), yuncheBoardParam.getPageSize(), true);
        List<YuncheBoardDO> list = yuncheBoardDOMapper.selectBoardsbyPartner(yuncheBoardParam);
        if (!CollectionUtils.isEmpty(list))
        {
            list
                    .stream()
                    .forEach(
                            e ->
                            {
                                if (e.getUrls() !=null)
                                {
                                    e.setAppUrls((List)JSON.parse(e.getUrls()));

                                }

                                if (e.getBank()!=null)
                                {
                                    e.setAppBanks((List)JSON.parse(e.getBank()));
                                }

                            }
                    );

        }

        //System.out.println(l);
        PageInfo<YuncheBoardDO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean board(YuncheBoardParam yuncheBoardParam)
    {
        // 登录用户权限
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        //判断是否是合伙人级别-还是管理员
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        yuncheBoardParam.setMaxGroupLevel(maxGroupLevel);
        //获取用户可见的银行
        yuncheBoardParam.setBankList(getUserHaveBank(loginUser.getId()));
        SessionUtils.getLoginUser().getId();
        PageHelper.startPage(yuncheBoardParam.getPageIndex(), yuncheBoardParam.getPageSize(), true);
        List<YuncheBoardDO> list = yuncheBoardDOMapper.selectBoardsbyPartner(yuncheBoardParam);
        //PageInfo<YuncheBoardDO> pageInfo = new PageInfo<>(list);
        if (!CollectionUtils.isEmpty(list))
        {
            return ResultBean.ofSuccess(list.stream().findFirst().get());
        }
        return ResultBean.ofError("无最新公告");
    }


    /**
     * 获取用户可见的银行 名称
     *
     * @param userId
     */
    private List<String> getUserHaveBank(Long userId) {
        List<Long> groupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(userId);
        List<String> userBankIdList = Lists.newArrayList();
        groupIdList.parallelStream().filter(Objects::nonNull).forEach(groupId -> {
            List<String> tmpBankidList = userGroupRelaBankDOMapper.getBankNameListByUserGroupId(groupId);
            userBankIdList.addAll(tmpBankidList);
        });
        return userBankIdList.parallelStream().distinct().collect(Collectors.toList());

    }
}
