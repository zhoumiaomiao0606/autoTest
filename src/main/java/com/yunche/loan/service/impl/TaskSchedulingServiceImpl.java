package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.entity.LoanRejectLogDO;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.AppTaskVO;
import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.domain.vo.TaskListVO;
import com.yunche.loan.domain.vo.TaskStateVO;
import com.yunche.loan.mapper.LoanProcessDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.LoanRejectLogService;
import com.yunche.loan.service.PermissionService;
import com.yunche.loan.service.TaskSchedulingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;
import static com.yunche.loan.config.constant.LoanProcessEnum.LOAN_APPLY;
import static com.yunche.loan.config.constant.MappingConst.SUPPLEMENT_TYPE_TEXT_MAP;


@Service
public class TaskSchedulingServiceImpl implements TaskSchedulingService {

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Resource
    private LoanProcessDOMapper loanProcessDOMapper;

    @Resource
    private LoanProcessService loanProcessService;

    @Resource
    private PermissionService permissionService;

    @Autowired
    private LoanRejectLogService loanRejectLogService;


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

        // 节点权限校验
        permissionService.checkTaskPermission(taskListQuery.getTaskDefinitionKey());

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Integer level = taskSchedulingDOMapper.selectLevel(loginUser.getId());
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);

        List<TaskListVO> list = new ArrayList<>();
        taskListQuery.setLevel(level);
        taskListQuery.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId()));
        taskListQuery.setLoginUserId(loginUser.getId());
        list = taskSchedulingDOMapper.selectTaskList(taskListQuery);
        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        // 打回原因
        rejectReason(taskListQuery.getTaskDefinitionKey(), list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<AppTaskVO>> queryAppTaskList(AppTaskListQuery appTaskListQuery) {

        PageHelper.startPage(appTaskListQuery.getPageIndex(), appTaskListQuery.getPageSize(), true);
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Integer maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());

        List<TaskListVO> list = taskSchedulingDOMapper.selectAppTaskList(appTaskListQuery.getMultipartType(), appTaskListQuery.getCustomer(), loginUser.getId(), maxGroupLevel);

        List<AppTaskVO> appTaskVOList = convert(list);

        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(appTaskVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean queryLoginUserLevel() {
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        return ResultBean.ofSuccess(taskSchedulingDOMapper.selectLevel(loginUser.getId()));
    }

    private List<AppTaskVO> convert(List<TaskListVO> list) {

        List<AppTaskVO> appTaskListVO = list.parallelStream()
                .map(e -> {

                    AppTaskVO appTaskVO = new AppTaskVO();
                    BeanUtils.copyProperties(e, appTaskVO);

                    fillTaskStatus(appTaskVO);

                    canCreditSupplement(Long.valueOf(e.getId()), appTaskVO);

                    return appTaskVO;
                })
                .collect(Collectors.toList());

        return appTaskListVO;
    }

    /**
     * 是否可以发起【征信增补】
     *
     * @param orderId
     * @param appTaskVO
     */
    private void canCreditSupplement(Long orderId, AppTaskVO appTaskVO) {
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        if (TASK_PROCESS_INIT.equals(loanProcessDO.getTelephoneVerify()) && ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus())) {
            appTaskVO.setCanCreditSupplement(true);
        } else {
            appTaskVO.setCanCreditSupplement(false);
        }
    }

    /**
     * 打回原因
     *
     * @param taskDefinitionKey
     * @param list
     */
    private void rejectReason(String taskDefinitionKey, List<TaskListVO> list) {

        if (CREDIT_APPLY.getCode().equals(taskDefinitionKey) || LOAN_APPLY.getCode().equals(taskDefinitionKey)) {

            list.parallelStream()
                    .filter(Objects::nonNull)
                    .filter(e -> TASK_PROCESS_REJECT.equals(Byte.valueOf(e.getTaskStatus())))
                    .forEach(e -> {

                        LoanRejectLogDO loanRejectLogDO = loanRejectLogService.rejectLog(Long.valueOf(e.getId()), taskDefinitionKey);
                        if (null != loanRejectLogDO) {
                            e.setRejectReason(loanRejectLogDO.getReason());
                        }
                    });
        }
    }

    private void fillTaskStatus(AppTaskVO appTaskVO) {

        ResultBean<List<TaskStateVO>> taskStateVOResult = loanProcessService.currentTask(Long.valueOf(appTaskVO.getId()));
        Preconditions.checkArgument(taskStateVOResult.getSuccess(), taskStateVOResult.getMsg());

        List<TaskStateVO> taskStateVOS = taskStateVOResult.getData();

        if (CollectionUtils.isEmpty(taskStateVOS)) {
            // 无节点信息
            String cancelTaskDefKey = loanProcessDOMapper.getCancelTaskDefKey(Long.valueOf(appTaskVO.getId()));
            // 弃单
            if (StringUtils.isNotBlank(cancelTaskDefKey)) {
                appTaskVO.setTaskStatus(String.valueOf(TASK_PROCESS_CANCEL));
                appTaskVO.setCurrentTask("已弃单");
            } else {
                appTaskVO.setTaskStatus(String.valueOf(TASK_PROCESS_CLOSED));
                appTaskVO.setCurrentTask("已结单");
            }
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

        String supplementType = taskListVO.getSupplementType();
        if (StringUtils.isNotBlank(supplementType)) {
            taskListVO.setSupplementTypeText(SUPPLEMENT_TYPE_TEXT_MAP.get(Byte.valueOf(supplementType)));
        }

        String taskStatus = taskListVO.getTaskStatus();

        // 1-已提交;  2-未提交;  3-打回;
        taskListVO.setTaskType(taskStatus);
        // 文本值
        String taskTypeText = getTaskTypeText(taskStatus);
        taskListVO.setTaskTypeText(taskTypeText);

        taskListVO.setCurrentTask(LoanProcessEnum.getNameByCode(taskDefinitionKey));
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
