package com.yunche.loan.service.impl;


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
    public List<ScheduleTaskVO> scheduleTaskList() {
            EmployeeDO loginUser = SessionUtils.getLoginUser();
            return taskSchedulingDOMapper.selectScheduleTaskList(loginUser.getId());
    }

}
