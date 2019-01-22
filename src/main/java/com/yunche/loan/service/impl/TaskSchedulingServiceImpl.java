package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.param.FlowOperationMsgParam;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.ScheduleTaskQuery;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.query.ZhonganListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.ListQueryTaskStatusConst.*;
import static com.yunche.loan.config.constant.LoanDataFlowConst.DATA_FLOW_TASK_KEY_REVIEW_SUFFIX;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.DATA_FLOW;
import static com.yunche.loan.config.util.DateTimeFormatUtils.formatter_yyyyMMdd_HHmmss;

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
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CarService carService;

    @Autowired
    private ActivitiService activitiService;

    @Autowired
    private DictService dictService;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private PartnerRelaEmployeeDOMapper partnerRelaEmployeeDOMapper;

    @Autowired
    private TotalQueryListDOMapper totalQueryListDOMapper;

    @Override
    public ResultBean<List<ZhonganListVO>> selectZhonganList(ZhonganListQuery query) {
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(loginUser.getId());
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        query.setPartnerId(partnerId);
        query.setMaxGroupLevel(maxGroupLevel);
        query.setJuniorIds(juniorIds);
        //获取用户可见的区域
        query.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        query.setBankList(getUserHaveBank(loginUser.getId()));
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List list = taskSchedulingDOMapper.selectZhonganList(query);
        PageInfo<ZhonganListVO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    @Override
    public ResultBean<Map> appCount() {
        ResultBean<Long> countScheduletask = countScheduletasklist();
        ResultBean<Long> countCredit = countFlowOperationMsgList(new Long("1"));
        ResultBean<Long> countRisk = countFlowOperationMsgList(new Long("2"));
        ResultBean<Long> countBusi = countFlowOperationMsgList(new Long("3"));
        Map map = new HashMap();
        map.put("countScheduletask", countScheduletask.getData());
        map.put("countCredit", countCredit.getData());
        map.put("countRisk", countRisk.getData());
        map.put("countBusi", countBusi.getData());
        return ResultBean.ofSuccess(map);
    }

    @Override
    public ResultBean<Long> countFlowOperationMsgList(Long multipartType) {
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        FlowOperationMsgParam query = new FlowOperationMsgParam();
        query.setMultipartType(multipartType);
        query.setMaxGroupLevel(maxGroupLevel);
        query.setJuniorIds(juniorIds);
        //获取用户可见的区域
        query.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        query.setBankList(getUserHaveBank(loginUser.getId()));
        query.setReadStatus(new Long("0"));
        long count = PageHelper.count(() -> taskSchedulingDOMapper.selectFlowOperationMsgList(query));

        return ResultBean.ofSuccess(count);
    }

    @Override
    public ResultBean<List<FlowOperationMsgListVO>> selectFlowOperationMsgList(FlowOperationMsgParam query) {
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        query.setMaxGroupLevel(maxGroupLevel);
        query.setJuniorIds(juniorIds);
        //获取用户可见的区域
        query.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        query.setBankList(getUserHaveBank(loginUser.getId()));

        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<FlowOperationMsgListVO> list = taskSchedulingDOMapper.selectFlowOperationMsgList(query);
        PageInfo<FlowOperationMsgListVO> pageInfo = new PageInfo<>(list);

        // 征信结果：截取title的[已通过/xx]
        cutTitle(list, query.getMultipartType());

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public boolean selectRejectTask(Long orderId) {
        // 节点校验
        if (orderId == null) {
            throw new BizException("无订单");
        }

        return taskSchedulingDOMapper.selectRejectTask(orderId);
    }

    @Override
    public ResultBean<List<ScheduleTaskVO>> scheduleTaskList(String key, Integer pageIndex, Integer pageSize) {

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
        query.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        query.setBankList(getUserHaveBank(loginUser.getId()));

        PageHelper.startPage(pageIndex, pageSize, true);
        //List<ScheduleTaskVO> list = taskSchedulingDOMapper.selectScheduleTaskList(query);
        List<ScheduleTaskVO> list = new ArrayList<>();
        PageInfo<ScheduleTaskVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<Long> countScheduletasklist() {
        /*EmployeeDO loginUser = SessionUtils.getLoginUser();
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
        query.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        query.setBankList(getUserHaveBank(loginUser.getId()));

        long count = PageHelper.count(() -> {
            taskSchedulingDOMapper.selectScheduleTaskList(query);
        });*/

        return ResultBean.ofSuccess(Long.valueOf("0"));
    }

    @Override
    public ResultBean<List<TaskListVO>> queryRoleChangeHisTaskList(TaskListQuery taskListQuery) {

        // 节点校验
        if (!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }

        // 节点权限校验
        permissionService.checkTaskPermission(taskListQuery.getTaskDefinitionKey());

        // loginUser 只能访问 自己及下级的数据
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        taskListQuery.setMaxGroupLevel(maxGroupLevel);
        taskListQuery.setJuniorIds(juniorIds);

        //获取用户可见的区域内合伙人ID列表
        taskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));

        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);
        List<TaskListVO> list = taskSchedulingDOMapper.selectRoleChangeHisTaskList(taskListQuery);
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<TaskListVO>> creditApplyList(TaskListQuery taskListQuery) {

        Preconditions.checkNotNull(taskListQuery.getTaskStatus(), "taskStatus不能为空");
        // 节点校验
        if (!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }
        // 节点权限校验
        permissionService.checkTaskPermission(taskListQuery.getTaskDefinitionKey());
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        taskListQuery.setJuniorIds(juniorIds);
        taskListQuery.setMaxGroupLevel(maxGroupLevel);
        //获取用户可见的区域
        taskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);
        List<TaskListVO> list = totalQueryListDOMapper.selectApplyCredit(taskListQuery);

        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<TaskListVO>> queryCancelTaskList(TaskListQuery taskListQuery) {

        // 节点校验
        Preconditions.checkArgument(LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey()),
                "错误的任务节点key");

        // 2年前
        LocalDateTime _2_years_ago = LocalDate.now().minusYears(2).atTime(0, 0, 0);
        taskListQuery.setStartCancelGmtCreate(_2_years_ago.format(formatter_yyyyMMdd_HHmmss));

        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);
        List<TaskListVO> list = taskSchedulingDOMapper.selectCancelTaskList(taskListQuery);
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<TaskListVO>> queryNewTaskList(TaskListQuery taskListQuery) throws BizException {

        Preconditions.checkNotNull(taskListQuery.getTaskStatus(), "taskStatus不能为空");


        // 节点校验
        if (!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }

        // 节点权限校验
        permissionService.checkTaskPermission(taskListQuery.getTaskDefinitionKey());
        List<Long> bankInterfaceSerialOrderidList = new ArrayList<>();
        if(LoanProcessEnum.BANK_CREDIT_RECORD.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            if("process".equals(taskListQuery.getSerialStatus())){
                bankInterfaceSerialOrderidList = totalQueryListDOMapper.selectProcessBankOrder("applyCredit");
            }else if("pending".equals(taskListQuery.getSerialStatus())){
                bankInterfaceSerialOrderidList = totalQueryListDOMapper.selectSuccessBankOrder("applyCredit");
            }

            taskListQuery.setBankInterfaceSerialOrderidList(bankInterfaceSerialOrderidList);
        }
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        taskListQuery.setEmployeeId(loginUser.getId());
        taskListQuery.setJuniorIds(juniorIds);
        taskListQuery.setMaxGroupLevel(maxGroupLevel);
        //获取用户可见的区域
        taskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));
        List<TaskListVO> list = new ArrayList<>();
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);
        if(LoanProcessEnum.CREDIT_APPLY.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectApplyCredit(taskListQuery);
        }else if(LoanProcessEnum.FINANCIAL_SCHEME.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectTotalCusInfo(taskListQuery);
        }else if(LoanProcessEnum.BANK_CREDIT_RECORD.getCode().equals(taskListQuery.getTaskDefinitionKey())) {
            list = totalQueryListDOMapper.selectBankCreditPend(taskListQuery);
        }else if (LoanProcessEnum.INSTALL_GPS.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectCarGps(taskListQuery);
        }else if(LoanProcessEnum.LOAN_INFO_RECORD.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectLoanInfoRecordList(taskListQuery);
        }else if(LoanProcessEnum.VISIT_VERIFY.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectVisitDoor(taskListQuery);
        }else if(LoanProcessEnum.CAR_INSURANCE.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectCarInsurance(taskListQuery);
        }else if(LoanProcessEnum.VEHICLE_INFORMATION.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectVehicleInformationList(taskListQuery);
        }else if(LoanProcessEnum.INFO_SUPPLEMENT.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectSupplementInfo(taskListQuery);
        }else if(LoanProcessEnum.FINANCIAL_SCHEME_MODIFY_APPLY.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectFinancialSchemeModifyApplyList(taskListQuery);
        }else if(LoanProcessEnum.BUSINESS_PAY.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectBusinessPay(taskListQuery);
        }else if(LoanProcessEnum.BUSINESS_REVIEW.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectBusinessReviewList(taskListQuery);
        }else if(LoanProcessEnum.LOAN_REVIEW.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectLoanReview(taskListQuery);
        }else if(LoanProcessEnum.REMIT_REVIEW.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectRemitReview(taskListQuery);
        }else if (LoanProcessEnum.LOAN_APPLY.getCode().equals(taskListQuery.getTaskDefinitionKey()))
        {
            list = totalQueryListDOMapper.queryLoanApplyList(taskListQuery);
        }else if (LoanProcessEnum.MATERIAL_MANAGE.getCode().equals(taskListQuery.getTaskDefinitionKey()))
        {
            list = totalQueryListDOMapper.queryMaterialManageList(taskListQuery);

        }else if (LoanProcessEnum.MATERIAL_PRINT_REVIEW.getCode().equals(taskListQuery.getTaskDefinitionKey()))
        {
            list = totalQueryListDOMapper.queryMaterialPrintList(taskListQuery);

        }else if (LoanProcessEnum.APPLY_LICENSE_PLATE_DEPOSIT_INFO.getCode().equals(taskListQuery.getTaskDefinitionKey()))
        {
            list = totalQueryListDOMapper.queryApplyLicensePlateDepositList(taskListQuery);

        }

        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<TaskListVO>> queryTaskList(TaskListQuery taskListQuery) {
        Preconditions.checkNotNull(taskListQuery.getTaskStatus(), "taskStatus不能为空");

        // 资料流转l
        if (DATA_FLOW.getCode().equals(taskListQuery.getTaskDefinitionKey())) {
            return queryDataFlowTaskList(taskListQuery);
        }

        // 节点校验
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
        List<Long> bankInterfaceSerialOrderidList = taskSchedulingDOMapper.selectBankInterfaceSerialOrderidList(taskListQuery);
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
        taskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));
        taskListQuery.setBankInterfaceSerialOrderidList(bankInterfaceSerialOrderidList);

        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);
        List<TaskListVO> list = taskSchedulingDOMapper.selectTaskList(taskListQuery);
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
    @Override
    public ResultBean<Long> countNewQueryTaskList(TaskListQuery taskListQuery) {
        Preconditions.checkNotNull(taskListQuery.getTaskStatus(), "taskStatus不能为空");

        // 节点校验
        if (!LoanProcessEnum.havingCode(taskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }

        // 节点权限校验
        permissionService.checkTaskPermission(taskListQuery.getTaskDefinitionKey());
        List<Long> bankInterfaceSerialOrderidList = new ArrayList<>();
        if(LoanProcessEnum.BANK_CREDIT_RECORD.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            if("process".equals(taskListQuery.getSerialStatus())){
                bankInterfaceSerialOrderidList = totalQueryListDOMapper.selectProcessBankOrder("applyCredit");
            }else if("pending".equals(taskListQuery.getSerialStatus())){
                bankInterfaceSerialOrderidList = totalQueryListDOMapper.selectSuccessBankOrder("applyCredit");
            }

            taskListQuery.setBankInterfaceSerialOrderidList(bankInterfaceSerialOrderidList);
        }
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        taskListQuery.setJuniorIds(juniorIds);
        taskListQuery.setMaxGroupLevel(maxGroupLevel);
        //获取用户可见的区域
        taskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));
        List<TaskListVO> list = new ArrayList<>();
        if(LoanProcessEnum.FINANCIAL_SCHEME.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectTotalCusInfo(taskListQuery);
        }else if(LoanProcessEnum.BANK_CREDIT_RECORD.getCode().equals(taskListQuery.getTaskDefinitionKey())){
            list = totalQueryListDOMapper.selectBankCreditPend(taskListQuery);

        }
        long count =0;
        if(list != null){
            count = list.size();
        }

        return ResultBean.ofSuccess(count);
    }

    @Override
    public ResultBean<Long> countQueryTaskList(TaskListQuery taskListQuery) {
        Preconditions.checkNotNull(taskListQuery.getTaskStatus(), "taskStatus不能为空");

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
        List<Long> bankInterfaceSerialOrderidList = taskSchedulingDOMapper.selectBankInterfaceSerialOrderidList(taskListQuery);

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
        taskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));
        taskListQuery.setBankInterfaceSerialOrderidList(bankInterfaceSerialOrderidList);


        long count = PageHelper.count(() -> {
            taskSchedulingDOMapper.selectTaskList(taskListQuery);
        });

        return ResultBean.ofSuccess(count);
    }


    @Override
    public ResultBean<List<AppTaskVO>> queryAppTaskList(AppTaskListQuery appTaskListQuery) {

        // 节点校验
        /*if (!LoanProcessEnum.havingCode(appTaskListQuery.getTaskDefinitionKey())) {
            throw new BizException("错误的任务节点key");
        }
        // 节点权限校验
        permissionService.checkTaskPermission(appTaskListQuery.getTaskDefinitionKey());*/
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
        appTaskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        appTaskListQuery.setBankList(getUserHaveBank(loginUser.getId()));

        PageHelper.startPage(appTaskListQuery.getPageIndex(), appTaskListQuery.getPageSize(), true);
        List<TaskListVO> list = taskSchedulingDOMapper.selectAppTaskList(appTaskListQuery);
        List<AppTaskVO> appTaskVOList = convert(list, appTaskListQuery.getMultipartType());
        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(appTaskVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    /**
     * [资料流转] 列表查询
     *
     * @param taskListQuery
     * @return
     */
    private ResultBean<List<TaskListVO>> queryDataFlowTaskList(TaskListQuery taskListQuery) {

        // 获取并设置 资料流转节点-key
        getAndSetDataFlowNodeSet(taskListQuery);

        // 空node    则直接返回EMPTY
        if (CollectionUtils.isEmpty(taskListQuery.getDataFlowNodeSet())) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        // loginUser 只能访问 自己及下级的数据
        loginUserGetSelfAndSub(taskListQuery);

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        //获取用户可见的区域内合伙人ID列表
        taskListQuery.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        //获取用户可见的银行
        taskListQuery.setBankList(getUserHaveBank(loginUser.getId()));

        // 分页
        PageHelper.startPage(taskListQuery.getPageIndex(), taskListQuery.getPageSize(), true);

        // query
        List<TaskListVO> list = taskSchedulingDOMapper.selectDataFlowTaskList(taskListQuery);

        // 取分页信息
        PageInfo<TaskListVO> pageInfo = new PageInfo<>(list);

        // 补充
        convert(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    /**
     * loginUser 只能访问 自己及下级的数据
     *
     * @param taskListQuery
     */
    private void loginUserGetSelfAndSub(TaskListQuery taskListQuery) {
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUser.getId());
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUser.getId());
        taskListQuery.setMaxGroupLevel(maxGroupLevel);
        taskListQuery.setJuniorIds(juniorIds);
    }

    /**
     * 获取并设置 资料流转node-key
     *
     * @param taskListQuery
     */
    private void getAndSetDataFlowNodeSet(TaskListQuery taskListQuery) {

        // 禁止外部传入
        if (!CollectionUtils.isEmpty(taskListQuery.getDataFlowNodeSet())) {
            taskListQuery.setDataFlowNodeSet(null);
        }

        // loginUser 有权访问的nodes
        Set<String> loginUserOwnDataFlowNodes = activitiService.getLoginUserOwnDataFlowNodes();

        // 无权访问
        if (CollectionUtils.isEmpty(loginUserOwnDataFlowNodes)) {
            return;
        }

        // nodes
        Set<String> dataFlowNodes = loginUserOwnDataFlowNodes;

        // 21-待邮寄(资料流转)
        if (TASK_STATUS_21_OF_DATA_FLOW_TO_BE_SEND.equals(taskListQuery.getTaskStatus())) {

            Set<String> toBeSendDataFlowNodes = loginUserOwnDataFlowNodes.stream()
                    .filter(node -> StringUtils.isNotBlank(node) && !node.endsWith(DATA_FLOW_TASK_KEY_REVIEW_SUFFIX))
                    .collect(Collectors.toSet());

            dataFlowNodes = toBeSendDataFlowNodes;
            // 21 ===还原为===> 2
            taskListQuery.setTaskStatus(TASK_STATUS_2_TODO);
        }
        // 22-待接收(资料流转)
        else if (TASK_STATUS_22_OF_DATA_FLOW_TO_BE_RECEIVED.equals(taskListQuery.getTaskStatus())) {
            Set<String> toBeReceivedDataFlowNodes = loginUserOwnDataFlowNodes.stream()
                    .filter(node -> StringUtils.isNotBlank(node) && node.endsWith(DATA_FLOW_TASK_KEY_REVIEW_SUFFIX))
                    .collect(Collectors.toSet());

            dataFlowNodes = toBeReceivedDataFlowNodes;
            // 22 ===还原为===> 2
            taskListQuery.setTaskStatus(TASK_STATUS_2_TODO);
        }

        // 限制的node类型
        Byte dataFlowType = taskListQuery.getDataFlowType();

        // 限制了node
        if (null != dataFlowType) {

            // type -> node
            String node = dictService.getCodeByKey("loanDataFlowType", String.valueOf(dataFlowType));

            // node存在
            if (StringUtils.isNotBlank(node)) {

                // 有权访问
                if (dataFlowNodes.contains(node)) {
                    // codeKMap
                    taskListQuery.setDataFlowNodeSet(Sets.newHashSet(node));
                    taskListQuery.setDataFlowTypeList(Lists.newArrayList(String.valueOf(dataFlowType)));
                } else {
                    // 无权访问
                    return;
                }

            } else {
                // node不存在
                return;
            }

        } else {

            // 未限制node
            taskListQuery.setDataFlowNodeSet(dataFlowNodes);

            // codeKMap
            Map<String, String> codeKMap = dictService.getCodeKMap("loanDataFlowType");

            // taskKey -> type
            List<String> typeList = dataFlowNodes.stream()
                    .filter(Objects::nonNull)
                    .map(code -> {

                        String type = codeKMap.get(code);
                        return type;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // typeList
            taskListQuery.setDataFlowTypeList(typeList);
        }
    }

    private void convert(List<TaskListVO> list) {

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        // kvMap
        Map<String, String> kvMap = dictService.getKVMap("loanDataFlowType");
        if (CollectionUtils.isEmpty(kvMap)) {
            return;
        }

        // kCodeMap
        Map<String, String> kCodeMap = dictService.getKCodeMap("loanDataFlowType");
        if (CollectionUtils.isEmpty(kvMap)) {
            return;
        }

        list.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // dataFlowTypeText
                    String dataFlowTypeText = kvMap.get(e.getDataFlowType());

                    // dataFlowTypeText
                    e.setDataFlowTypeText(dataFlowTypeText);

                    // 2   - 任务状态Text
                    if (TASK_STATUS_2_TODO.equals(Integer.valueOf(e.getTaskStatus()))) {
                        if (dataFlowTypeText.endsWith("-确认接收")) {
                            e.setTaskType(String.valueOf(TASK_STATUS_22_OF_DATA_FLOW_TO_BE_RECEIVED));
                            e.setTaskTypeText("待接收");
                        } else {
                            e.setTaskType(String.valueOf(TASK_STATUS_21_OF_DATA_FLOW_TO_BE_SEND));
                            e.setTaskTypeText("待邮寄");
                        }
                    }
                    // type -> taskKey
                    String taskKey = kCodeMap.get(e.getDataFlowType());
                    e.setTaskKey(taskKey);

                });
    }

    private List<AppTaskVO> convert(List<TaskListVO> list, Integer multipartType) {

        List<AppTaskVO> appTaskListVO = list.parallelStream()
                .map(e -> {

                    AppTaskVO appTaskVO = new AppTaskVO();
                    BeanUtils.copyProperties(e, appTaskVO);

                    appTaskVO.setBankName(e.getBank());
                    appTaskVO.setCarPrice(e.getCar_price());
                    appTaskVO.setCarDetailId(e.getCar_detail_id());

                    // taskStatus
                    fillTaskStatus(appTaskVO);

                    // can [征信增补]
                    canCreditSupplement(Long.valueOf(e.getId()), appTaskVO);

                    // [视频面签]列表查询时   ==>  才需要车型
                    if (multipartType == 7) {
                        // carName
                        if (StringUtils.isNotBlank(appTaskVO.getCarDetailId())) {
                            appTaskVO.setCarName(carService.getFullName(Long.valueOf(appTaskVO.getCarDetailId()), CAR_DETAIL));
                        }
                    }

                    return appTaskVO;
                })
                .collect(Collectors.toList());

        return appTaskListVO;
    }

    /**
     * 发起【征信增补】前置条件校验
     *
     * @param orderId
     * @param appTaskVO
     */
    private void canCreditSupplement(Long orderId, AppTaskVO appTaskVO) {
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        // 发起【征信增补】前置条件校验： ->  未过【电审】
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

    /**
     * 获取 用户可见区域内的 所有合伙人ID列表
     *
     * @param userId
     */
    private List<Long> getUserHaveBizAreaPartnerId(Long userId) {
        List<Long> empBizAreaPartnerIds = loanQueryDOMapper.selectEmpBizAreaPartnerIds(userId);
        if (CollectionUtils.isEmpty(empBizAreaPartnerIds)) {
            return null;
        }
        if (empBizAreaPartnerIds.get(0) == null) {
            return null;
        }
        return empBizAreaPartnerIds;
    }

    /**
     * 征信结果：截取title的[已xx]
     *
     * @param list
     * @param multipartType
     */
    private void cutTitle(List<FlowOperationMsgListVO> list, Long multipartType) {

        // 征信结果
        if (multipartType == 1L) {

            list.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String title = e.getTitle();
                        if (StringUtils.isNotBlank(title)) {

                            // 截取title的[已通过/打回/xx]
                            String[] titleArr = title.split("\\[");
                            e.setTitle(titleArr[0]);
                        }
                    });
        }
    }
}
