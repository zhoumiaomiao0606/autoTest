package com.yunche.loan.service.impl;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Service
@Transactional
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Override
    public ResultBean scheduleTaskList(Integer pageIndex, Integer pageSize) {
            PageHelper.startPage(pageIndex, pageSize, true);

            EmployeeDO loginUser = SessionUtils.getLoginUser();
            List<ScheduleTaskVO> list = taskSchedulingDOMapper.selectScheduleTaskList(loginUser.getId());

            // 取分页信息
            PageInfo<ScheduleTaskVO> pageInfo = new PageInfo<ScheduleTaskVO>(list);


            return ResultBean.ofSuccess(list,new Long(pageInfo.getTotal()).intValue(),pageInfo.getPageNum(),pageInfo.getPageSize());
    }

}
