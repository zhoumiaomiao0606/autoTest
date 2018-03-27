package com.yunche.loan.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;

import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.TaskSchedulingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_DONE;
import static com.yunche.loan.config.constant.LoanProcessConst.PROCESS_MAP;


@Service
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Resource
    private LoanProcessService loanProcessService;

    @Override
    public ResultBean scheduleTaskList(Integer pageIndex, Integer pageSize) {
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Integer level = taskSchedulingDOMapper.selectLevel(loginUser.getId());
        PageHelper.startPage(pageIndex, pageSize, true);
        List<ScheduleTaskVO> list = taskSchedulingDOMapper.selectScheduleTaskList(loginUser.getId(), level);

        // 取分页信息
        PageInfo<ScheduleTaskVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean queryTaskList(TaskListQuery taskListQuery) {

        if (!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Integer level = taskSchedulingDOMapper.selectLevel(loginUser.getId());
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);

        List<TaskListVO> list = new ArrayList<>();
        if (LoanProcessEnum.TELEPHONE_VERIFY.getCode().equals(taskListQuery.getTaskDefinitionKey())) {
            taskListQuery.setLevel(level);
            list = taskSchedulingDOMapper.selectTelephoneVerifyTaskList(taskListQuery);
        } else if (LoanProcessEnum.INFO_SUPPLEMENT.getCode().equals(taskListQuery.getTaskDefinitionKey())) {
            list = taskSchedulingDOMapper.selectSupplementInfoTaskList(taskListQuery);
        } else {
            list = taskSchedulingDOMapper.selectOtherTaskList(taskListQuery);
        }

        fillMsg(list, taskListQuery.getTaskDefinitionKey());

        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean queryAppTaskList(AppTaskListQuery appTaskListQuery) {
        PageHelper.startPage(appTaskListQuery.getPageIndex(), appTaskListQuery.getPageSize(), true);

        List<TaskListVO> list = taskSchedulingDOMapper.selectAppTaskList(appTaskListQuery.getMultipartType(), appTaskListQuery.getCustomer());

        List<AppTaskVO> appTaskVOList = convert(list);

        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(appTaskVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    private List<AppTaskVO> convert(List<TaskListVO> list) {

        List<AppTaskVO> appTaskListVO = list.parallelStream()
                .map(e -> {

                    AppTaskVO appTaskVO = new AppTaskVO();
                    BeanUtils.copyProperties(e, appTaskVO);

                    fillTaskStatus(appTaskVO);

                    return appTaskVO;
                })
                .collect(Collectors.toList());

        return appTaskListVO;
    }

    private void fillTaskStatus(AppTaskVO appTaskVO) {

        ResultBean<List<TaskStateVO>> taskStateVOResult = loanProcessService.currentTask(Long.valueOf(appTaskVO.getId()));
        Preconditions.checkArgument(taskStateVOResult.getSuccess(), taskStateVOResult.getMsg());

        List<TaskStateVO> taskStateVOS = taskStateVOResult.getData();

        if (CollectionUtils.isEmpty(taskStateVOS)) {
            // 无节点信息
            appTaskVO.setTaskStatus(String.valueOf(TASK_PROCESS_DONE));
            appTaskVO.setCurrentTask("已结单");
        } else {
            TaskStateVO taskStateVO = taskStateVOS.get(0);

            if (null != taskStateVO) {
                String taskStatus = String.valueOf(taskStateVO.getTaskStatus());

                appTaskVO.setTaskStatus(taskStatus);
                appTaskVO.setCurrentTask(taskStateVO.getTaskName());

                appTaskVO.setTaskType(taskStatus);
                // 文本值
                String taskTypeText = getTaskTypeText(taskStatus);
                appTaskVO.setTaskTypeText(taskTypeText);
            }

        }
    }


    private void fillMsg(List<TaskListVO> list, String taskDefinitionKey) {
        list.parallelStream()
                .forEach(e -> {
                    fillMsg(e, taskDefinitionKey);
                });
    }

    private void fillMsg(TaskListVO taskListVO, String taskDefinitionKey) {

        String taskStatus = taskListVO.getTaskStatus();

        // 1-已提交;  2-未提交;  3-打回;
        taskListVO.setTaskType(taskStatus);
        // 文本值
        String taskTypeText = getTaskTypeText(taskStatus);
        taskListVO.setTaskTypeText(taskTypeText);

        taskListVO.setCurrentTask(PROCESS_MAP.get(taskDefinitionKey));
    }

    private String getTaskTypeText(String taskStatus) {

        String taskTypeText = null;

        if (StringUtils.isNotBlank(taskStatus)) {
            switch (taskStatus) {
                case "0":
                    taskTypeText = "未执行到此";
                    break;
                case "1":
                    taskTypeText = "已提交";
                    break;
                case "2":
                    taskTypeText = "未提交";
                    break;
                case "3":
                    taskTypeText = "打回";
                    break;
                case "4":
                    taskTypeText = "未提交";
                    break;
                case "5":
                    taskTypeText = "未提交";
                    break;
                case "6":
                    taskTypeText = "未提交";
                    break;
                case "7":
                    taskTypeText = "已提交";
                    break;
                default:
                    taskTypeText = "未知状态";
            }
        }

        return taskTypeText;
    }

}
