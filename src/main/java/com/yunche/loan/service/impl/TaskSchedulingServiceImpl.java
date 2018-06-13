package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.AreaCache;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.ScheduleTaskQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.AppTaskVO;
import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.domain.vo.TaskListVO;
import com.yunche.loan.domain.vo.TaskStateVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.PermissionService;
import com.yunche.loan.service.TaskSchedulingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;

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
    private UserGroupRelaBankDOMapper userGroupRelaBankDOMapper;

    @Autowired
    private UserGroupRelaAreaDOMapper userGroupRelaAreaDOMapper;

    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AreaCache areaCache;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Override
    public ResultBean<List<ScheduleTaskVO>> scheduleTaskList(Integer pageIndex, Integer pageSize) {
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long telephoneVerifyLevel = taskSchedulingDOMapper.selectTelephoneVerifyLevel(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        Long financeLevel = taskSchedulingDOMapper.selectFinanceLevel(loginUser.getId());
        Long collectionLevel = taskSchedulingDOMapper.selectCollectionLevel(loginUser.getId());
        Long financeApplyLevel = taskSchedulingDOMapper.selectFinanceApplyLevel(loginUser.getId());
        Long refundApplyLevel = taskSchedulingDOMapper.selectRefundApplyLevel(loginUser.getId());
        Long materialSupplementLevel = taskSchedulingDOMapper.selectMaterialSupplementLevel(loginUser.getId());
        ScheduleTaskQuery query = new ScheduleTaskQuery();
        query.setJuniorIds(juniorIds);
        query.setEmployeeId(loginUser.getId());
        query.setTelephoneVerifyLevel(telephoneVerifyLevel);
        query.setFinanceLevel(financeLevel);
        query.setCollectionLevel(collectionLevel);
        query.setMaxGroupLevel(maxGroupLevel);
        query.setFinanceApplyLevel(financeApplyLevel);
        query.setRefundApplyLevel(refundApplyLevel);
        query.setMaterialSupplementLevel(materialSupplementLevel);
        //获取用户可见的区域
        query.setAreaIdList(getUserHaveArea(loginUser.getId()));
        //获取用户可见的银行
        query.setBankList(getUserHaveBank(loginUser.getId()));
        PageHelper.startPage(pageIndex, pageSize, true);
        List<ScheduleTaskVO> list = taskSchedulingDOMapper.selectScheduleTaskList(query);
        PageInfo<ScheduleTaskVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<ScheduleTaskVO>> scheduleTaskListBykey(String key, Integer pageIndex, Integer pageSize) {
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long telephoneVerifyLevel = taskSchedulingDOMapper.selectTelephoneVerifyLevel(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        Long financeLevel = taskSchedulingDOMapper.selectFinanceLevel(loginUser.getId());
        Long collectionLevel = taskSchedulingDOMapper.selectCollectionLevel(loginUser.getId());
        Long financeApplyLevel = taskSchedulingDOMapper.selectFinanceApplyLevel(loginUser.getId());
        Long refundApplyLevel = taskSchedulingDOMapper.selectRefundApplyLevel(loginUser.getId());
        Long materialSupplementLevel = taskSchedulingDOMapper.selectMaterialSupplementLevel(loginUser.getId());
        ScheduleTaskQuery query = new ScheduleTaskQuery();
        query.setJuniorIds(juniorIds);
        query.setKey(key);
        query.setEmployeeId(loginUser.getId());
        query.setTelephoneVerifyLevel(telephoneVerifyLevel);
        query.setFinanceLevel(financeLevel);
        query.setCollectionLevel(collectionLevel);
        query.setMaxGroupLevel(maxGroupLevel);
        query.setFinanceApplyLevel(financeApplyLevel);
        query.setRefundApplyLevel(refundApplyLevel);
        query.setMaterialSupplementLevel(materialSupplementLevel);
        //获取用户可见的区域
        query.setAreaIdList(getUserHaveArea(loginUser.getId()));
        //获取用户可见的银行
        query.setBankList(getUserHaveBank(loginUser.getId()));
        PageHelper.startPage(pageIndex, pageSize, true);
        List<ScheduleTaskVO> list = taskSchedulingDOMapper.selectScheduleTaskList(query);
        PageInfo<ScheduleTaskVO> pageInfo = new PageInfo<ScheduleTaskVO>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<TaskListVO>> queryTaskList(TaskListQuery taskListQuery) {

        if (!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }
        // 节点权限校验
        permissionService.checkTaskPermission(taskListQuery.getTaskDefinitionKey());
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long telephoneVerifyLevel = taskSchedulingDOMapper.selectTelephoneVerifyLevel(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        Long financeLevel = taskSchedulingDOMapper.selectFinanceLevel(loginUser.getId());
        Long collectionLevel = taskSchedulingDOMapper.selectCollectionLevel(loginUser.getId());
        Long financeApplyLevel = taskSchedulingDOMapper.selectFinanceApplyLevel(loginUser.getId());
        Long refundApplyLevel = taskSchedulingDOMapper.selectRefundApplyLevel(loginUser.getId());
        Long materialSupplementLevel = taskSchedulingDOMapper.selectMaterialSupplementLevel(loginUser.getId());
        taskListQuery.setJuniorIds(juniorIds);
        taskListQuery.setEmployeeId(loginUser.getId());
        taskListQuery.setTelephoneVerifyLevel(telephoneVerifyLevel);
        taskListQuery.setFinanceLevel(financeLevel);
        taskListQuery.setCollectionLevel(collectionLevel);
        taskListQuery.setMaxGroupLevel(maxGroupLevel);
        taskListQuery.setFinanceApplyLevel(financeApplyLevel);
        taskListQuery.setRefundApplyLevel(refundApplyLevel);
        taskListQuery.setMaterialSupplementLevel(materialSupplementLevel);
        //获取用户可见的区域
        taskListQuery.setAreaIdList(getUserHaveArea(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);
        List<TaskListVO> list = taskSchedulingDOMapper.selectTaskList(taskListQuery);
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<AppTaskVO>> queryAppTaskList(AppTaskListQuery appTaskListQuery) {

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long telephoneVerifyLevel = taskSchedulingDOMapper.selectTelephoneVerifyLevel(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        Long financeLevel = taskSchedulingDOMapper.selectFinanceLevel(loginUser.getId());
        Long collectionLevel = taskSchedulingDOMapper.selectCollectionLevel(loginUser.getId());
        Long financeApplyLevel = taskSchedulingDOMapper.selectFinanceApplyLevel(loginUser.getId());
        Long refundApplyLevel = taskSchedulingDOMapper.selectRefundApplyLevel(loginUser.getId());
        Long materialSupplementLevel = taskSchedulingDOMapper.selectMaterialSupplementLevel(loginUser.getId());
        appTaskListQuery.setJuniorIds(juniorIds);
        appTaskListQuery.setEmployeeId(loginUser.getId());
        appTaskListQuery.setTelephoneVerifyLevel(telephoneVerifyLevel);
        appTaskListQuery.setFinanceLevel(financeLevel);
        appTaskListQuery.setCollectionLevel(collectionLevel);
        appTaskListQuery.setMaxGroupLevel(maxGroupLevel);
        appTaskListQuery.setFinanceApplyLevel(financeApplyLevel);
        appTaskListQuery.setRefundApplyLevel(refundApplyLevel);
        appTaskListQuery.setMaterialSupplementLevel(materialSupplementLevel);
        //获取用户可见的区域
        appTaskListQuery.setAreaIdList(getUserHaveArea(loginUser.getId()));
        //获取用户可见的银行
        appTaskListQuery.setBankList(getUserHaveBank(loginUser.getId()));

        PageHelper.startPage(appTaskListQuery.getPageIndex(), appTaskListQuery.getPageSize(), true);
        List<TaskListVO> list = taskSchedulingDOMapper.selectAppTaskList(appTaskListQuery);
        List<AppTaskVO> appTaskVOList = convert(list);
        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(appTaskVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }


    private List<AppTaskVO> convert(List<TaskListVO> list) {

        List<AppTaskVO> appTaskListVO = list.stream()
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


    private void fillTaskStatus(AppTaskVO appTaskVO) {

        ResultBean<List<TaskStateVO>> taskStateVOResult = loanProcessService.currentTask(Long.valueOf(appTaskVO.getId()));
        Preconditions.checkArgument(taskStateVOResult.getSuccess(), taskStateVOResult.getMsg());

        List<TaskStateVO> taskStateVOS = taskStateVOResult.getData();

        if (CollectionUtils.isEmpty(taskStateVOS)) {
            // TODO 待删除
//            // 无节点信息
//            String cancelTaskDefKey = loanProcessDOMapper.getCancelTaskDefKey(Long.valueOf(appTaskVO.getId()));
//            // 弃单
//            if (StringUtils.isNotBlank(cancelTaskDefKey)) {
//                appTaskVO.setTaskStatus(String.valueOf(TASK_PROCESS_CANCEL));
//                appTaskVO.setCurrentTask("已弃单");
//            } else {
//                appTaskVO.setTaskStatus(String.valueOf(TASK_PROCESS_CLOSED));
//                appTaskVO.setCurrentTask("已结单");
//            }

            LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(Long.valueOf(appTaskVO.getId()));
            Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

            if (ORDER_STATUS_CANCEL.equals(loanProcessDO.getOrderStatus())) {
                // 弃单
                appTaskVO.setTaskStatus(String.valueOf(TASK_PROCESS_CANCEL));
                appTaskVO.setCurrentTask("已弃单");
            } else if (ORDER_STATUS_END.equals(loanProcessDO.getOrderStatus())) {
                // 结单
                appTaskVO.setTaskStatus(String.valueOf(TASK_PROCESS_CLOSED));
                appTaskVO.setCurrentTask("已结单");
            } else {
                appTaskVO.setTaskStatus(null);
                appTaskVO.setCurrentTask("状态异常");
            }

        } else {
            TaskStateVO taskStateVO = taskStateVOS.get(0);

            if (null != taskStateVO) {
                String taskStatus = String.valueOf(taskStateVO.getTaskStatus());

                appTaskVO.setTaskStatus(taskStatus);
                appTaskVO.setCurrentTask(taskStateVO.getTaskName());

                appTaskVO.setTaskType(taskStatus);
                // 文本值
                String taskTypeText = getTaskStatusText(taskStatus);
                appTaskVO.setTaskTypeText(taskTypeText);
            }
        }
    }


//    private void fillMsg(List<TaskListVO> list, String taskDefinitionKey) {
//        list.parallelStream()
//                .forEach(e -> {
//                    fillMsg(e, taskDefinitionKey);
//                });
//    }
//
//    private void fillMsg(TaskListVO taskListVO, String taskDefinitionKey) {
//
//        String supplementType = taskListVO.getSupplementType();
//        if (StringUtils.isNotBlank(supplementType)) {
//            taskListVO.setSupplementTypeText(SUPPLEMENT_TYPE_TEXT_MAP.get(Byte.valueOf(supplementType)));
//        }
//
//        String taskStatus = taskListVO.getTaskStatus();
//
//        // 1-已提交;  2-未提交;  3-打回;
//        taskListVO.setTaskType(taskStatus);
//        // 文本值
//        String taskTypeText = getTaskStatusText(taskStatus);
//        taskListVO.setTaskTypeText(taskTypeText);
//
//        taskListVO.setCurrentTask(LoanProcessEnum.getNameByCode(taskDefinitionKey));
//    }

    public static String getTaskStatusText(String taskStatus) {

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
                case "12":
                    taskTypeText = "已弃单";
                    break;
                default:
                    taskTypeText = "未知状态";
            }
        }

        return taskTypeText;
    }
    /**
     * 获取用户可见的银行
     * @param id
     */
    private List<String> getUserHaveBank(Long id) {
        List<Long> groupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(id);
        List<String> userBankIdList =Lists.newArrayList();
        groupIdList.parallelStream().filter(Objects::nonNull).forEach(groupId->{
            List<String> tmpBankidList = userGroupRelaBankDOMapper.getBankNameListByUserGroupId(groupId);
            userBankIdList.addAll(tmpBankidList);
        });
        return userBankIdList.parallelStream().distinct().collect(Collectors.toList());

    }

    /**
     * 获取用户可见的区域
     * @param id
     */
    private List<Long> getUserHaveArea(Long id) {
        List<Long> groupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(id);
        List<Long> userAreaList = Lists.newArrayList();
        groupIdList.parallelStream().filter(Objects::nonNull).forEach(groupId->{

            List<Long> tmpUserAreaList = userGroupRelaAreaDOMapper.getAreaIdListByUserGroupId(groupId);
            if(tmpUserAreaList.size()>0){
                List<BaseAreaDO> baseAreaDOS = baseAreaDOMapper.selectByIdList(tmpUserAreaList, BaseConst.VALID_STATUS);
                baseAreaDOS.parallelStream().filter(Objects::nonNull).forEach(e->{

                    switch(e.getLevel()){
                        case 0:
                            break;
                        case 1:
                            List<Long> idByProvenceId = baseAreaDOMapper.selectCityIdByProvenceId(e.getAreaId());
                            userAreaList.addAll(idByProvenceId);
                            break;
                        case 2:
                            userAreaList.add(e.getAreaId());
                            break;
                    }
                });
            }
        });
        return userAreaList.parallelStream().distinct().collect(Collectors.toList());

    }
}
