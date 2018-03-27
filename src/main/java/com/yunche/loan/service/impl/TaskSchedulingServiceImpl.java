package com.yunche.loan.service.impl;



import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessActionEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.query.TaskListQuery;

import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.domain.vo.TaskListVO;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
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

    @Override
    public ResultBean queryTaskList(TaskListQuery taskListQuery) {
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);



        if(!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())){
            throw new BizException("错误的任务节点key");
        }
        List<TaskListVO> list= new ArrayList<TaskListVO>();
        if(LoanProcessEnum.TELEPHONE_VERIFY.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            taskListQuery.setLoginUserId(SessionUtils.getLoginUser().getId());
            list = taskSchedulingDOMapper.selectTelephoneVerifyTaskList(taskListQuery);
        }

        else if(LoanProcessEnum.INFO_SUPPLEMENT.getCode().equals(taskListQuery.getTaskDefinitionKey())) {
            list = taskSchedulingDOMapper.selectSupplementInfoTaskList(taskListQuery);
        }

        else{
            list = taskSchedulingDOMapper.selectOtherTaskList(taskListQuery);
        }

        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<TaskListVO>(list);

        return ResultBean.ofSuccess(list,new Long(pageInfo.getTotal()).intValue(),pageInfo.getPageNum(),pageInfo.getPageSize());
    }

}
