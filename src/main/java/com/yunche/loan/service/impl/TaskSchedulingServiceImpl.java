package com.yunche.loan.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessActionEnum;
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
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_DONE;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_TODO;


@Service
@Transactional
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Resource
    private LoanProcessService loanProcessService;

    @Override
    public ResultBean scheduleTaskList(Integer pageIndex, Integer pageSize) {
        PageHelper.startPage(pageIndex, pageSize, true);

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        List<ScheduleTaskVO> list = taskSchedulingDOMapper.selectScheduleTaskList(loginUser.getId());

        // 取分页信息
        PageInfo<ScheduleTaskVO> pageInfo = new PageInfo<ScheduleTaskVO>(list);


        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean queryTaskList(TaskListQuery taskListQuery) {
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);


        if (!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }
        List<TaskListVO> list = new ArrayList<>();
        if (LoanProcessEnum.TELEPHONE_VERIFY.getCode().equals(taskListQuery.getTaskDefinitionKey())) {
            taskListQuery.setLevel(taskSchedulingDOMapper.selectLevel(SessionUtils.getLoginUser().getId()));
            list = taskSchedulingDOMapper.selectTelephoneVerifyTaskList(taskListQuery);
        } else if (LoanProcessEnum.INFO_SUPPLEMENT.getCode().equals(taskListQuery.getTaskDefinitionKey())) {
            list = taskSchedulingDOMapper.selectSupplementInfoTaskList(taskListQuery);
        } else {
            list = taskSchedulingDOMapper.selectOtherTaskList(taskListQuery);
        }

        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean queryAppTaskList(AppTaskListQuery appTaskListQuery) {
        PageHelper.startPage(appTaskListQuery.getPageIndex(), appTaskListQuery.getPageSize(), true);

        List<TaskListVO> list = taskSchedulingDOMapper.selectAppTaskList(appTaskListQuery.getMultipartType(), appTaskListQuery.getCustomer());

        fillMsg(list);

//        List<LoanOrderVO> loanOrderVOList = convert(list);

        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    private void fillMsg(List<TaskListVO> list) {
        list.parallelStream()
                .forEach(e -> {
                    fillTaskStatus(e);
                });
    }

    /**
     * 任务状态
     *
     * @param loanOrderVO
     */
    private void fillTaskStatus(LoanOrderVO loanOrderVO) {

        ResultBean<List<TaskStateVO>> taskStateVOResult = loanProcessService.currentTask(Long.valueOf(loanOrderVO.getId()));
        Preconditions.checkArgument(taskStateVOResult.getSuccess(), taskStateVOResult.getMsg());

        List<TaskStateVO> taskStateVOS = taskStateVOResult.getData();

        if (CollectionUtils.isEmpty(taskStateVOS)) {
            loanOrderVO.setTaskStatus(TASK_PROCESS_DONE);
            loanOrderVO.setCurrentTask("已结单");
        } else {
            TaskStateVO taskStateVO = taskStateVOS.get(0);
            loanOrderVO.setTaskStatus(taskStateVO.getTaskStatus());
            loanOrderVO.setCurrentTask(taskStateVO.getTaskName());
        }
    }

    private List<LoanOrderVO> convert(List<TaskListVO> list) {

        List<LoanOrderVO> loanOrderVOList = list.parallelStream()
                .map(e -> {

                    LoanOrderVO loanOrderVO = new LoanOrderVO();
                    BeanUtils.copyProperties(e, loanOrderVO);

                    BaseVO customer = new BaseVO(null, e.getCustomer());
                    BaseVO partner = new BaseVO(null, e.getPartner());
                    BaseVO salesman = new BaseVO(null, e.getSalesman());
                    loanOrderVO.setCustomer(customer);
                    loanOrderVO.setPartner(partner);
                    loanOrderVO.setSalesman(salesman);

                    fillTaskStatus(loanOrderVO);

                    return loanOrderVO;

                })
                .collect(Collectors.toList());

        return loanOrderVOList;
    }

    private void fillTaskStatus(TaskListVO taskListVO) {

        ResultBean<List<TaskStateVO>> taskStateVOResult = loanProcessService.currentTask(Long.valueOf(taskListVO.getId()));
        Preconditions.checkArgument(taskStateVOResult.getSuccess(), taskStateVOResult.getMsg());

        List<TaskStateVO> taskStateVOS = taskStateVOResult.getData();

        if (CollectionUtils.isEmpty(taskStateVOS)) {
            taskListVO.setTaskStatus(String.valueOf(TASK_PROCESS_DONE));
            taskListVO.setCurrentTask("已结单");
        } else {
            TaskStateVO taskStateVO = taskStateVOS.get(0);
            taskListVO.setTaskStatus(String.valueOf(taskStateVO.getTaskStatus()));
            taskListVO.setCurrentTask(taskStateVO.getTaskName());
        }
    }
}
