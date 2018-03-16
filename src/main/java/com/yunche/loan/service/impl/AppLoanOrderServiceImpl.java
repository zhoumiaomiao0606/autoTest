package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.AppMultipartQueryTypeConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.vo.AppBusinessInfoVO;
import com.yunche.loan.domain.vo.AppCustomerInfoVO;
import com.yunche.loan.domain.vo.AppInsuranceInfoVO;
import com.yunche.loan.domain.vo.AppLoanCustomerVO;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.AppCustomerQuery;
import com.yunche.loan.domain.query.AppLoanOrderQuery;
import com.yunche.loan.domain.query.BaseQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQuery;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AppMultipartQueryTypeConst.*;
import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.CustomerConst.*;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_SUPPLEMENT;
import static com.yunche.loan.config.constant.LoanProcessEnum.INFO_SUPPLEMENT;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.*;


/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Service
public class AppLoanOrderServiceImpl implements AppLoanOrderService {


    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

    @Autowired
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private DepartmentDOMapper departmentDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private LoanProcessOrderService loanProcessOrderService;

    @Autowired
    private LoanFinancialPlanService loanFinancialPlanService;

    @Autowired
    private LoanBaseInfoService loanBaseInfoService;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private LoanCarInfoService loanCarInfoService;

    @Autowired
    private LoanCreditInfoService loanCreditInfoService;

    @Autowired
    private CarService carService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RuntimeService runtimeService;


    @Override
    public ResultBean<List<AppLoanOrderVO>> query(AppLoanOrderQuery query) {
        Preconditions.checkNotNull(query.getTaskDefinitionKey(), "当前任务节点不能为空");
        Preconditions.checkNotNull(query.getTaskStatus(), "查询类型不能为空");

        // 获取用户角色名列表
        List<String> userGroupNameList = getUserGroupNameList();

        long totalNum = 0;
        TaskInfoQuery taskQuery = null;
        if (!CollectionUtils.isEmpty(userGroupNameList)) {
            // 创建任务查询对象
            taskQuery = getTaskInfoQuery(query, userGroupNameList);
            // 统计
            totalNum = taskQuery.count();
        }

        if (totalNum > 0) {
            // 任务列表
            List<TaskInfo> tasks = taskQuery.orderByTaskCreateTime().desc().listPage(query.getStartRow(), query.getEndRow());

            // 获取流程列表 -> 业务单列表
            if (!CollectionUtils.isEmpty(tasks)) {
                List<AppLoanOrderVO> baseInstProcessOrderVOList = tasks.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstanceId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                AppLoanOrderVO appLoanOrderVO = new AppLoanOrderVO();
                                // 填充订单信息
                                fillOrderMsg(appLoanOrderVO, processInstanceId, query.getTaskDefinitionKey(), query.getTaskStatus());
                                return appLoanOrderVO;
                            }

                            return null;
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(AppLoanOrderVO::getGmtCreate).reversed())
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(baseInstProcessOrderVOList, (int) totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, (int) totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<AppLoanOrderVO>> multipartQuery(AppLoanOrderQuery query) {
        Preconditions.checkNotNull(query.getMultipartType(), "多节点查询类型不能为空");

        // TODO
//        if (LOAN_APPLY_TODO.equals(query.getMultipartType())) {
//
//        } else if (LOAN_APPLY_DONE.equals(query.getMultipartType())) {
//
//        } else if (CUSTOMER_LOAN_TODO.equals(query.getMultipartType())) {
//
//        } else if (CUSTOMER_LOAN_DONE.equals(query.getMultipartType())) {
//
//        }

        long totalNum = loanOrderDOMapper.countMultipartQuery(query);
        if (totalNum > 0) {

            List<LoanOrderDO> loanOrderDOList = loanOrderDOMapper.listMultipartQuery(query);
            if (!CollectionUtils.isEmpty(loanOrderDOList)) {

                List<AppLoanOrderVO> loanOrderVOList = loanOrderDOList.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                AppLoanOrderVO appLoanOrderVO = new AppLoanOrderVO();
                                // 填充订单信息
                                fillOrderMsg(appLoanOrderVO, processInstanceId, e.getCurrentTaskDefKey(), null);
                                return appLoanOrderVO;
                            }

                            return null;
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(AppLoanOrderVO::getGmtCreate).reversed())
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(loanOrderVOList, (int) totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, (int) totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<AppLoanOrderVO>> listCreditNotEnding(BaseQuery query) {

        long totalNum = loanOrderDOMapper.countCreditNotEnding(query);
        if (totalNum > 0) {

            List<LoanOrderDO> loanOrderDOList = loanOrderDOMapper.listCreditNotEnding(query);
            if (!CollectionUtils.isEmpty(loanOrderDOList)) {

                List<AppLoanOrderVO> loanOrderVOList = loanOrderDOList.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                AppLoanOrderVO appLoanOrderVO = new AppLoanOrderVO();
                                // 填充订单信息
                                fillOrderMsg(appLoanOrderVO, processInstanceId, e.getCurrentTaskDefKey(), null);
                                return appLoanOrderVO;
                            }

                            return null;
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(AppLoanOrderVO::getGmtCreate).reversed())
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(loanOrderVOList, (int) totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, (int) totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<AppInfoSupplementVO> infoSupplementDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        AppInfoSupplementVO appInfoSupplementVO = new AppInfoSupplementVO();

        // 客户信息
        if (null != loanOrderDO.getLoanCustomerId()) {
            ResultBean<CustomerVO> customerVOResultBean = loanCustomerService.getById(loanOrderDO.getLoanCustomerId());
            Preconditions.checkArgument(customerVOResultBean.getSuccess(), customerVOResultBean.getMsg());
            CustomerVO customerVO = customerVOResultBean.getData();

            appInfoSupplementVO.setOrderId(String.valueOf(orderId));
            appInfoSupplementVO.setCustomerId(customerVO.getId());
            appInfoSupplementVO.setCustomerName(customerVO.getName());
            appInfoSupplementVO.setIdCard(customerVO.getIdCard());
        }

        // 贷款基本信息：贷款额、期限 & 银行
        if (null != loanOrderDO.getLoanBaseInfoId()) {
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
            if (null != loanFinancialPlanDO) {
                appInfoSupplementVO.setLoanAmount(String.valueOf(loanFinancialPlanDO.getLoanAmount()));
                appInfoSupplementVO.setLoanTime(loanFinancialPlanDO.getLoanTime());
                appInfoSupplementVO.setBank(loanFinancialPlanDO.getBank());
            }
        }

        //  车辆信息
        if (null != loanOrderDO.getLoanCarInfoId()) {
            LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
            if (null != loanCarInfoDO && null != loanCarInfoDO.getCarDetailId()) {
                ResultBean<String> carFullNameResultBean = carService.getFullName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL);
                Preconditions.checkArgument(carFullNameResultBean.getSuccess(), carFullNameResultBean.getMsg());
                appInfoSupplementVO.setCarName(carFullNameResultBean.getData());
            }
        }

        // 增补类型、备注 & 时间
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .taskDefinitionKey(loanOrderDO.getCurrentTaskDefKey())
                .orderByTaskCreateTime()
                .desc()
                .listPage(0, 1);

        if (!CollectionUtils.isEmpty(historicTaskInstanceList)) {
            HistoricTaskInstance historicTaskInstance = historicTaskInstanceList.get(0);
            // 时间
            appInfoSupplementVO.setSupplementStartDate(historicTaskInstance.getCreateTime());
            appInfoSupplementVO.setSupplementEndDate(historicTaskInstance.getEndTime());

            String taskVariablePrefix = loanOrderDO.getCurrentTaskDefKey() + ":" + loanOrderDO.getProcessInstId() + ":"
                    + historicTaskInstance.getExecutionId() + ":";

            String taskVariableTypeKey = taskVariablePrefix + PROCESS_VARIABLE_INFO_SUPPLEMENT_TYPE;
            String taskVariableContentKey = taskVariablePrefix + PROCESS_VARIABLE_INFO_SUPPLEMENT_CONTENT;
            String taskVariableInfoKey = taskVariablePrefix + PROCESS_VARIABLE_INFO_SUPPLEMENT_INFO;
            String taskVariableUserIdKey = taskVariablePrefix + PROCESS_VARIABLE_USER_ID;
            String taskVariableUserNameKey = taskVariablePrefix + PROCESS_VARIABLE_USER_NAME;

            HistoricVariableInstanceQuery historicVariableInstanceQuery = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(loanOrderDO.getProcessInstId());

            HistoricVariableInstance typeHistoricVariableInstance = historicVariableInstanceQuery.variableName(taskVariableTypeKey).singleResult();
            HistoricVariableInstance contentHistoricVariableInstance = historicVariableInstanceQuery.variableName(taskVariableContentKey).singleResult();
            HistoricVariableInstance infoHistoricVariableInstance = historicVariableInstanceQuery.variableName(taskVariableInfoKey).singleResult();
            HistoricVariableInstance userIdHistoricVariableInstance = historicVariableInstanceQuery.variableName(taskVariableUserIdKey).singleResult();
            HistoricVariableInstance userNameHistoricVariableInstance = historicVariableInstanceQuery.variableName(taskVariableUserNameKey).singleResult();

            // 增补类型
            if (null != typeHistoricVariableInstance) {
                appInfoSupplementVO.setSupplementType((Integer) typeHistoricVariableInstance.getValue());
            }
            // 增补内容
            if (null != contentHistoricVariableInstance) {
                appInfoSupplementVO.setSupplementContent((String) contentHistoricVariableInstance.getValue());
            }
            // 增补说明
            if (null != infoHistoricVariableInstance) {
                appInfoSupplementVO.setSupplementInfo((String) infoHistoricVariableInstance.getValue());
            }
            // 要求增补人员
            if (null != userNameHistoricVariableInstance) {
                appInfoSupplementVO.setInitiator((String) userNameHistoricVariableInstance.getValue());
            }
            // 要求增补部门
            if (null != userIdHistoricVariableInstance) {
                Object value = userIdHistoricVariableInstance.getValue();
                if (null != value) {
                    Long userId = (Long) value;
                    EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(userId, null);
                    if (null != employeeDO) {
                        DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(employeeDO.getDepartmentId(), null);
                        if (null != departmentDO) {
                            appInfoSupplementVO.setInitiatorUnit(departmentDO.getName());
                        }
                    }
                }
            }
        }

        // 文件分类列表
        ResultBean<List<FileVO>> fileVOResultBean = loanFileService.listByCustomerIdAndUploadType(loanOrderDO.getLoanCustomerId(), UPLOAD_TYPE_SUPPLEMENT);
        Preconditions.checkArgument(fileVOResultBean.getSuccess(), fileVOResultBean.getMsg());
        appInfoSupplementVO.setFiles(fileVOResultBean.getData());

        return ResultBean.ofSuccess(appInfoSupplementVO);
    }

    @Override
    public ResultBean<AppCreditApplyOrderVO> creditApplyOrderDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        // 订单基本信息
        AppCreditApplyOrderVO creditApplyOrderVO = new AppCreditApplyOrderVO();
        BeanUtils.copyProperties(loanOrderDO, creditApplyOrderVO);
        creditApplyOrderVO.setOrderId(loanOrderDO.getId());

        // 关联的-客户信息(主贷人/共贷人/担保人/紧急联系人)
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId);
        BeanUtils.copyProperties(custDetailVOResultBean.getData(), creditApplyOrderVO);

        // 关联的-贷款基本信息
        if (null == loanOrderDO.getLoanBaseInfoId()) {
            LoanBaseInfoVO loanBaseInfoVO = new LoanBaseInfoVO();
            creditApplyOrderVO.setLoanBaseInfo(loanBaseInfoVO);
        } else {
            ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
            creditApplyOrderVO.setLoanBaseInfo(loanBaseInfoVOResultBean.getData());
        }

        // 实际贷款额
        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
        if (null != loanFinancialPlanDO && null != loanFinancialPlanDO.getLoanAmount()) {
            creditApplyOrderVO.getLoanBaseInfo().setActualLoanAmount(String.valueOf(loanFinancialPlanDO.getLoanAmount()));
        }

        return ResultBean.ofSuccess(creditApplyOrderVO, "查询征信申请单详情成功");
    }

    @Override
    @Transactional
    public ResultBean<AppCreditApplyVO> createCreditApplyOrder(AppCustomerParam customerParam) {
        Preconditions.checkArgument(StringUtils.isNotBlank(customerParam.getName()), "姓名不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(customerParam.getIdCard()), "身份证号不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(customerParam.getMobile()), "手机号码不能为空");

        // 校验权限
        checkStartProcessPermission();

        // 客户信息创建
        Long customerId = createLoanCustomer(customerParam);

        // 业务单创建
        ResultBean<Long> createLoanOrderResult = loanProcessOrderService.createLoanOrder(null, customerId);
        Preconditions.checkArgument(createLoanOrderResult.getSuccess(), createLoanOrderResult.getMsg());

        // 业务单ID & 客户ID
        AppCreditApplyVO appCreditApplyVO = new AppCreditApplyVO();
        appCreditApplyVO.setOrderId(createLoanOrderResult.getData());
        appCreditApplyVO.setCustomerId(customerId);

        return ResultBean.ofSuccess(appCreditApplyVO);
    }

    @Override
    public ResultBean<AppCustDetailVO> customerDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据主贷人ID获取客户详情列表
        ResultBean<CustDetailVO> resultBean = loanCustomerService.detailAll(orderId);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        AppCustDetailVO appCustDetailVO = new AppCustDetailVO();
        BeanUtils.copyProperties(resultBean.getData(), appCustDetailVO);

        return ResultBean.ofSuccess(appCustDetailVO);
    }

    @Override
    @Transactional
    public ResultBean<Void> updateCustomer(AppCustomerParam param) {
        Preconditions.checkNotNull(param, "客户信息不能为空");

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(param, loanCustomerDO);
        ResultBean<Void> updateCustResult = loanCustomerService.update(loanCustomerDO);
        Preconditions.checkArgument(updateCustResult.getSuccess(), updateCustResult.getMsg());

        // TODO 文件编辑
//        ResultBean<Void> updateFileResult = loanFileService.update(param.getId(), param.getFiles());
//        Preconditions.checkArgument(updateFileResult.getSuccess(), updateFileResult.getMsg());

        return ResultBean.ofSuccess(null);
    }

    @Override
    @Transactional
    public ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId) {
        ResultBean<Void> resultBean = loanCustomerService.faceOff(orderId, principalLenderId, commonLenderId);
        return resultBean;
    }

    @Override
    public ResultBean<AppLoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        Long loanFinancialPlanId = loanOrderDOMapper.getLoanFinancialPlanIdById(orderId);

        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);
        AppLoanFinancialPlanVO loanFinancialPlanVO = new AppLoanFinancialPlanVO();
        if (null != loanFinancialPlanDO) {
            BeanUtils.copyProperties(loanFinancialPlanDO, loanFinancialPlanVO);
        }

        return ResultBean.ofSuccess(loanFinancialPlanVO);
    }

    @Override
    public ResultBean<AppLoanHomeVisitVO> homeVisitDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        Long loanHomeVisitId = loanOrderDOMapper.getLoanHomeVisitId(orderId);

        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanHomeVisitId);
        AppLoanHomeVisitVO loanHomeVisitVO = new AppLoanHomeVisitVO();
        BeanUtils.copyProperties(loanHomeVisitDO, loanHomeVisitVO);

        return ResultBean.ofSuccess(loanHomeVisitVO);
    }

    @Override
    @Transactional
    public ResultBean<Void> createOrUpdateLoanHomeVisit(AppLoanHomeVisitParam loanHomeVisitParam) {
        Preconditions.checkNotNull(loanHomeVisitParam, "上门家访资料不能为空");

        if (null == loanHomeVisitParam.getId()) {
            // 创建
            createLoanHomeVisit(loanHomeVisitParam);
        } else {
            // 编辑
            updateLoanHomeVisit(loanHomeVisitParam);
        }

        return ResultBean.ofSuccess(null, "保存上门家访资料成功");
    }

    @Override
    public ResultBean<AppLoanFinancialPlanVO> calcLoanFinancialPlan(AppLoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam, "金融方案不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanFinancialPlanParam, loanFinancialPlanDO);

        // calc
        ResultBean<LoanFinancialPlanVO> calcResult = loanFinancialPlanService.calc(loanFinancialPlanDO);
        Preconditions.checkArgument(calcResult.getSuccess(), calcResult.getMsg());

        AppLoanFinancialPlanVO appLoanFinancialPlanVO = new AppLoanFinancialPlanVO();
        LoanFinancialPlanVO loanFinancialPlanVO = calcResult.getData();
        if (null != loanFinancialPlanVO) {
            BeanUtils.copyProperties(loanFinancialPlanVO, appLoanFinancialPlanVO);
        }

        return ResultBean.ofSuccess(appLoanFinancialPlanVO);
    }

    @Override
    @Transactional
    public ResultBean<Void> infoSupplementUpload(AppInfoSupplementParam infoSupplementParam) {
        Preconditions.checkNotNull(infoSupplementParam.getCustomerId(), "客户ID不能为空");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(infoSupplementParam.getFiles()), "资料信息不能为空");

        List<FileVO> files = infoSupplementParam.getFiles();
        files.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 已经增补过的图片 ——> 正常上传
                    moveOldSupplementToNormal(infoSupplementParam.getCustomerId(), e.getType());

                    // 保存新增补的文件 ——> 增补上传
                    saveNewSupplementFiles(infoSupplementParam.getCustomerId(), e.getType(), e.getUrls());
                });

        return ResultBean.ofSuccess(null, "资料增补成功");
    }

    @Override
    public ResultBean<List<AppLoanCustomerVO>> customerQuery(AppCustomerQuery query) {
        Preconditions.checkNotNull(query.getLoanStatus(), "贷款状态不能为空");

        // TODO
        AppLoanOrderQuery appLoanOrderQuery = new AppLoanOrderQuery();


        return ResultBean.ofSuccess(null);
    }


    @Override
    public ResultBean<AppCustomerInfoVO> customerInfo(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        // 客户基本信息
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId);
        Preconditions.checkArgument(custDetailVOResultBean.getSuccess(), custDetailVOResultBean.getMsg());

        AppCustomerInfoVO customerInfoVO = new AppCustomerInfoVO();
        customerInfoVO.setOrderId(orderId);

        CustDetailVO custDetailVO = custDetailVOResultBean.getData();
        if (null != custDetailVO) {

            // 主
            CustomerVO principalLenderVO = custDetailVO.getPrincipalLender();
            if (null != principalLenderVO) {
                AppCustomerInfoVO.CustomerInfo principalLender = new AppCustomerInfoVO.CustomerInfo();
                fillCustomerInfo(principalLenderVO, principalLender);
                customerInfoVO.setPrincipalLender(principalLender);
            }

            // 共
            List<CustomerVO> commonLenderVOList = custDetailVO.getCommonLenderList();
            if (!CollectionUtils.isEmpty(commonLenderVOList)) {

                List<AppCustomerInfoVO.CustomerInfo> commonLenderList = Lists.newArrayList();

                commonLenderVOList.parallelStream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            AppCustomerInfoVO.CustomerInfo customerInfo = new AppCustomerInfoVO.CustomerInfo();
                            fillCustomerInfo(e, customerInfo);
                            commonLenderList.add(customerInfo);
                        });

                customerInfoVO.setCommonLenderList(commonLenderList);
            }

            // 担保人
            List<CustomerVO> guarantorVOList = custDetailVO.getGuarantorList();
            if (!CollectionUtils.isEmpty(guarantorVOList)) {

                List<AppCustomerInfoVO.CustomerInfo> guarantorList = Lists.newArrayList();

                guarantorVOList.parallelStream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            AppCustomerInfoVO.CustomerInfo customerInfo = new AppCustomerInfoVO.CustomerInfo();
                            fillCustomerInfo(e, customerInfo);
                            guarantorList.add(customerInfo);
                        });

                customerInfoVO.setGuarantorList(guarantorList);
            }

            // 紧急联系人
            List<CustomerVO> emergencyContactVOList = custDetailVO.getEmergencyContactList();
            if (!CollectionUtils.isEmpty(emergencyContactVOList)) {

                List<AppCustomerInfoVO.EmergencyContact> emergencyContactList = Lists.newArrayList();

                emergencyContactVOList.parallelStream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            AppCustomerInfoVO.EmergencyContact emergencyContact = new AppCustomerInfoVO.EmergencyContact();
                            fillCustomerInfo(e, (AppCustomerInfoVO.CustomerInfo) emergencyContact);
                            emergencyContactList.add(emergencyContact);
                        });

                customerInfoVO.setEmergencyContactList(emergencyContactList);
            }
        }

        return ResultBean.ofSuccess(customerInfoVO);
    }

    /**
     * 填充客户信息
     *
     * @param customerVO
     * @param customerInfo
     */
    private void fillCustomerInfo(CustomerVO customerVO, AppCustomerInfoVO.CustomerInfo customerInfo) {
        if (null != customerVO) {
            BeanUtils.copyProperties(customerVO, customerInfo);
        }
    }

    @Override
    public ResultBean<AppBusinessInfoVO> businessInfo(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        AppBusinessInfoVO businessInfoVO = new AppBusinessInfoVO();

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "不能为空");

        // 基本信息
        if (null != loanOrderDO.getLoanBaseInfoId()) {
            ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
            Preconditions.checkArgument(loanBaseInfoVOResultBean.getSuccess(), loanBaseInfoVOResultBean.getMsg());

            // 业务员 & 合伙人
            LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoVOResultBean.getData();
            if (null != loanBaseInfoVO) {
                if (null != loanBaseInfoVO.getSalesman()) {
                    businessInfoVO.setSalesmanName(loanBaseInfoVO.getSalesman().getName());
                }
                if (null != loanBaseInfoVO.getPartner()) {
                    businessInfoVO.setPartnerName(loanBaseInfoVO.getPartner().getName());
                }
            }

            // 车型
            LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
            if (null != loanCarInfoDO) {
                // 车名
                ResultBean<String> carFullNameResultBean = carService.getFullName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL);
                Preconditions.checkArgument(carFullNameResultBean.getSuccess(), carFullNameResultBean.getMsg());
                businessInfoVO.setCarName(carFullNameResultBean.getData());

                // 车辆属性
                businessInfoVO.setCarType(loanCarInfoDO.getCarType());
                // GPS数量
                businessInfoVO.setGpsNum(loanCarInfoDO.getGpsNum());
            }

            if (null != loanOrderDO.getLoanFinancialPlanId()) {
                LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
                // 车价
                businessInfoVO.setCarPrice(loanFinancialPlanDO.getCarPrice());

                // 贷款额
                businessInfoVO.setLoanAmount(loanFinancialPlanDO.getLoanAmount());

                // 首月还款
                businessInfoVO.setFirstMonthRepay(loanFinancialPlanDO.getFirstMonthRepay());
                // 每月还款
                businessInfoVO.setEachMonthRepay(loanFinancialPlanDO.getEachMonthRepay());
                // 按揭期限
                businessInfoVO.setLoanTime(loanFinancialPlanDO.getLoanTime());
            }

            // TODO 车牌号
            businessInfoVO.setLicensePlateNumber("");

            // 征信情况
            if (null != loanOrderDO.getLoanCustomerId()) {
                // 银行征信
                ResultBean<LoanCreditInfoVO> bankLoanCreditInfoVOResultBean = loanCreditInfoService.getByCustomerId(loanOrderDO.getLoanCustomerId(), CREDIT_TYPE_BANK);
                Preconditions.checkArgument(bankLoanCreditInfoVOResultBean.getSuccess(), bankLoanCreditInfoVOResultBean.getMsg());
                LoanCreditInfoVO bankLoanCreditInfoVO = bankLoanCreditInfoVOResultBean.getData();
                if (null != bankLoanCreditInfoVO) {
                    businessInfoVO.setBankCreditResult(bankLoanCreditInfoVO.getResult());
                    businessInfoVO.setBankCreditInfo(bankLoanCreditInfoVO.getInfo());
                }

                // 社会征信
                ResultBean<LoanCreditInfoVO> socialLoanCreditInfoVOResultBean = loanCreditInfoService.getByCustomerId(loanOrderDO.getLoanCustomerId(), CREDIT_TYPE_SOCIAL);
                Preconditions.checkArgument(socialLoanCreditInfoVOResultBean.getSuccess(), socialLoanCreditInfoVOResultBean.getMsg());
                // 银行征信
                LoanCreditInfoVO socialLoanCreditInfoVO = socialLoanCreditInfoVOResultBean.getData();
                if (null != socialLoanCreditInfoVO) {
                    businessInfoVO.setSocialCreditResult(socialLoanCreditInfoVO.getResult());
                    businessInfoVO.setSocialCreditInfo(socialLoanCreditInfoVO.getInfo());
                }
            }
        }

        return ResultBean.ofSuccess(businessInfoVO);
    }

    @Override
    public ResultBean<AppInsuranceInfoVO> insuranceInfo(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        AppInsuranceInfoVO insuranceInfoVO = new AppInsuranceInfoVO();


        return ResultBean.ofSuccess(insuranceInfoVO);
    }

    /**
     * 保存新增补的文件
     *
     * @param customerId
     * @param type
     * @param urls
     */
    private void saveNewSupplementFiles(Long customerId, Byte type, List<String> urls) {
        if (CollectionUtils.isEmpty(urls)) {
            return;
        }

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, type, UPLOAD_TYPE_SUPPLEMENT);
        if (!CollectionUtils.isEmpty(loanFileDOS)) {
            LoanFileDO loanFileDO = loanFileDOS.get(0);
            if (null != loanFileDO) {

                loanFileDO.setPath(JSON.toJSONString(urls));
                ResultBean<Void> resultBean = loanFileService.update(loanFileDO);
                Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
            } else {

                loanFileDO.setType(type);
                loanFileDO.setUploadType(UPLOAD_TYPE_SUPPLEMENT);
                loanFileDO.setCustomerId(customerId);
                loanFileDO.setPath(JSON.toJSONString(urls));
                ResultBean<Long> resultBean = loanFileService.create(loanFileDO);
                Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
            }
        } else {
            LoanFileDO loanFileDO = new LoanFileDO();
            loanFileDO.setType(type);
            loanFileDO.setUploadType(UPLOAD_TYPE_SUPPLEMENT);
            loanFileDO.setCustomerId(customerId);
            loanFileDO.setPath(JSON.toJSONString(urls));
            ResultBean<Long> resultBean = loanFileService.create(loanFileDO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        }
    }


    private void moveOldSupplementToNormal(Long customerId, Byte type) {

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, type, null);
        if (!CollectionUtils.isEmpty(loanFileDOS)) {

            final LoanFileDO[] supplementFile = {null};
            final LoanFileDO[] normalFile = {null};

            loanFileDOS.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(f -> {

                        if (UPLOAD_TYPE_SUPPLEMENT.equals(f.getType())) {
                            supplementFile[0] = f;
                        } else if (UPLOAD_TYPE_NORMAL.equals(f.getType())) {
                            normalFile[0] = f;
                        }
                    });

            if (ArrayUtils.isNotEmpty(supplementFile)) {
                LoanFileDO supplementFileDO = supplementFile[0];
                if (null != supplementFileDO) {

                    String existSupplementPath = supplementFileDO.getPath();
                    if (StringUtils.isNotBlank(existSupplementPath)) {

                        // B - y
                        List<String> existSupplementPathList = JSON.parseArray(existSupplementPath, String.class);
                        if (!CollectionUtils.isEmpty(existSupplementPathList)) {

                            // A -y  编辑
                            if (ArrayUtils.isNotEmpty(normalFile)) {
                                LoanFileDO normalFileDO = normalFile[0];

                                String existNormalPath = normalFileDO.getPath();
                                if (StringUtils.isNotBlank(existNormalPath)) {
                                    List<String> existNormalPathList = JSON.parseArray(existNormalPath, String.class);

                                    existNormalPathList.add(existSupplementPath);
                                    normalFileDO.setPath(JSON.toJSONString(existNormalPathList));
                                    ResultBean<Void> updateResult = loanFileService.update(normalFileDO);
                                    Preconditions.checkArgument(updateResult.getSuccess(), updateResult.getMsg());

                                    supplementFileDO.setPath(null);
                                    ResultBean<Void> updateSupplementFileResult = loanFileService.update(supplementFileDO);
                                    Preconditions.checkArgument(updateSupplementFileResult.getSuccess(), updateSupplementFileResult.getMsg());
                                }
                            } else {

                                // A -n  新增
                                LoanFileDO normalFileDO = new LoanFileDO();
                                normalFileDO.setPath(JSON.toJSONString(existSupplementPathList));
                                normalFileDO.setType(UPLOAD_TYPE_NORMAL);
                                normalFileDO.setCustomerId(customerId);

                                ResultBean<Long> insertResult = loanFileService.create(normalFileDO);
                                Preconditions.checkArgument(insertResult.getSuccess(), insertResult.getMsg());
                            }
                        }
                    }

                }
            }


        }
    }

    @Override
    @Transactional
    public ResultBean<Long> createBaseInfo(AppLoanBaseInfoParam param) {
        Preconditions.checkNotNull(param.getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getLoanBaseInfo(), "贷款基本信息不能为空");

        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        convertLoanBaseInfo(param.getLoanBaseInfo(), loanBaseInfoDO);

        ResultBean<Long> resultBean = loanBaseInfoService.create(loanBaseInfoDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        // 更新业务单表
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(param.getOrderId());
        loanOrderDO.setLoanBaseInfoId(resultBean.getData());
        ResultBean<Void> updateLoanOrderResultBean = loanProcessOrderService.update(loanOrderDO);
        Preconditions.checkArgument(updateLoanOrderResultBean.getSuccess(), updateLoanOrderResultBean.getMsg());

        return resultBean;
    }

    @Override
    @Transactional
    public ResultBean<Void> updateBaseInfo(AppLoanBaseInfoDetailParam param) {
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        convertLoanBaseInfo(param, loanBaseInfoDO);

        ResultBean<Void> resultBean = loanBaseInfoService.update(loanBaseInfoDO);

        return resultBean;
    }

    @Override
    @Transactional
    public ResultBean<Long> addRelaCustomer(AppCustomerParam customerParam) {
        // convert
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        convertLoanCustomer(customerParam, loanCustomerDO);

        ResultBean<Long> resultBean = loanCustomerService.create(loanCustomerDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        return ResultBean.ofSuccess(resultBean.getData(), "创建关联人成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> delRelaCustomer(Long customerId) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        loanCustomerDO.setId(customerId);
        loanCustomerDO.setStatus(INVALID_STATUS);
        ResultBean<Void> resultBean = loanCustomerService.update(loanCustomerDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        return ResultBean.ofSuccess(null, "删除关联人成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> createLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkNotNull(loanCarInfoParam.getOrderId(), "业务单号不能为空");

        // convert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        convertLoanCarInfo(loanCarInfoParam, loanCarInfoDO);
        // insert
        ResultBean<Long> createResultBean = loanCarInfoService.create(loanCarInfoDO);
        Preconditions.checkArgument(createResultBean.getSuccess(), createResultBean.getMsg());

        // 关联
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(loanCarInfoParam.getOrderId());
        loanOrderDO.setLoanCarInfoId(createResultBean.getData());
        ResultBean<Void> updateRelaResultBean = loanProcessOrderService.update(loanOrderDO);
        Preconditions.checkArgument(updateRelaResultBean.getSuccess(), updateRelaResultBean.getMsg());

        return ResultBean.ofSuccess(createResultBean.getData(), "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkArgument(null != loanCarInfoParam && null != loanCarInfoParam.getId(), "车辆信息ID不能为空");

        // convert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        convertLoanCarInfo(loanCarInfoParam, loanCarInfoDO);

        ResultBean<Void> resultBean = loanCarInfoService.update(loanCarInfoDO);
        return resultBean;
    }

    @Override
    public ResultBean<AppLoanCarInfoVO> loanCarInfoDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        AppLoanCarInfoVO loanCarInfoVO = new AppLoanCarInfoVO();

        Long loanCarInfoId = loanOrderDOMapper.getLoanCarInfoIdById(orderId);

        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanCarInfoId);
        if (null != loanCarInfoDO) {
            BeanUtils.copyProperties(loanCarInfoDO, loanCarInfoVO);

            // 车型
            BaseVO carDetail = new BaseVO();
            carDetail.setId(loanCarInfoDO.getCarDetailId());
            carDetail.setName(loanCarInfoDO.getCarDetailName());
            loanCarInfoVO.setCarDetail(carDetail);

            // 合伙人账户信息
            AppLoanCarInfoVO.PartnerAccountInfo partnerAccountInfo = new AppLoanCarInfoVO.PartnerAccountInfo();
            BeanUtils.copyProperties(loanCarInfoDO, partnerAccountInfo);
            loanCarInfoVO.setPartnerAccountInfo(partnerAccountInfo);
        }

        return ResultBean.ofSuccess(loanCarInfoVO);
    }

    /**
     * insert贷款金融方案
     *
     * @param loanFinancialPlanParam
     */
    @Override
    public ResultBean<Long> createLoanFinancialPlan(AppLoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam.getOrderId(), "业务单号不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        convertLoanFinancialPlan(loanFinancialPlanParam, loanFinancialPlanDO);
        // insert
        ResultBean<Long> resultBean = loanFinancialPlanService.create(loanFinancialPlanDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        // 关联
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(loanFinancialPlanParam.getOrderId());
        loanOrderDO.setLoanFinancialPlanId(loanFinancialPlanParam.getId());
        ResultBean<Void> updateRelaResult = loanProcessOrderService.update(loanOrderDO);
        Preconditions.checkArgument(updateRelaResult.getSuccess(), updateRelaResult.getMsg());

        return resultBean;
    }

    /**
     * update贷款金融方案
     *
     * @param loanFinancialPlanParam
     */
    @Override
    public ResultBean<Void> updateLoanFinancialPlan(AppLoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam, "金融方案不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        convertLoanFinancialPlan(loanFinancialPlanParam, loanFinancialPlanDO);

        ResultBean<Void> resultBean = loanFinancialPlanService.update(loanFinancialPlanDO);
        return resultBean;
    }

    /**
     * 填充订单信息
     *
     * @param appLoanOrderVO
     * @param processInstanceId
     * @param taskDefinitionKey
     * @param taskStatus
     */
    private void fillOrderMsg(AppLoanOrderVO appLoanOrderVO, String processInstanceId, String taskDefinitionKey, Integer taskStatus) {
        // 任务状态
        if (null == taskStatus) {
            appLoanOrderVO.setTaskStatus(TASK_TODO);
        } else {
            appLoanOrderVO.setTaskStatus(taskStatus);
        }

        // 贷款客户基本信息填充
        fillBaseMsg(appLoanOrderVO, processInstanceId);

        // 资料增补类型
        fillInfoSupplementType(appLoanOrderVO, taskDefinitionKey, processInstanceId);

        // 当前任务节点
        fillCurrentTask(appLoanOrderVO, taskDefinitionKey);

        // 还款状态
        fillRepayStatus(appLoanOrderVO, taskDefinitionKey, processInstanceId);
    }


    /**
     * 当前任务
     *
     * @param appLoanOrderVO
     * @param taskDefinitionKey
     */
    private void fillCurrentTask(AppLoanOrderVO appLoanOrderVO, String taskDefinitionKey) {
        String currentTask = PROCESS_MAP.get(taskDefinitionKey);
        appLoanOrderVO.setCurrentTask(currentTask);
    }

    /**
     * TODO 还款状态： 1-正常还款;  2-非正常还款;  3-已结清;
     *
     * @param appLoanOrderVO
     * @param taskDefinitionKey
     * @param processInstanceId
     */
    private void fillRepayStatus(AppLoanOrderVO appLoanOrderVO, String taskDefinitionKey, String processInstanceId) {
        appLoanOrderVO.setRepayStatus(1);
    }

    /**
     * 资料增补类型
     *
     * @param appLoanOrderVO
     * @param taskDefinitionKey
     * @param processInstanceId
     */
    private void fillInfoSupplementType(AppLoanOrderVO appLoanOrderVO, String taskDefinitionKey, String processInstanceId) {
        if (INFO_SUPPLEMENT.getCode().equals(taskDefinitionKey)) {

            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .taskDefinitionKey(taskDefinitionKey)
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(0, 1);

            if (!CollectionUtils.isEmpty(historicTaskInstanceList)) {
                HistoricTaskInstance historicTaskInstance = historicTaskInstanceList.get(0);

                String taskVariableTypeKey = taskDefinitionKey + ":" + processInstanceId + ":"
                        + historicTaskInstance.getExecutionId() + ":" + PROCESS_VARIABLE_INFO_SUPPLEMENT_TYPE;

                HistoricVariableInstance typeHistoricVariableInstance = historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(processInstanceId).variableName(taskVariableTypeKey).singleResult();

                // 增补类型
                if (null != typeHistoricVariableInstance) {
                    appLoanOrderVO.setInfoSupplementType((Integer) typeHistoricVariableInstance.getValue());
                }
            }
        }
    }

    /**
     * 填充征信信息
     *
     * @param customer
     * @param loanCustomerDO
     * @param type
     */
    private void fillCreditMsg(CreditRecordVO.CustomerCreditRecord customer, LoanCustomerDO loanCustomerDO, Byte type) {
        if (CREDIT_TYPE_BANK.equals(type)) {
//            customer.setCreditStatus(loanCustomerDO.getBankCreditStatus());
//            customer.setCreditDetail(loanCustomerDO.getBankCreditDetail());
        } else if (CREDIT_TYPE_SOCIAL.equals(type)) {
//            customer.setCreditStatus(loanCustomerDO.getSocialCreditStatus());
//            customer.setCreditDetail(loanCustomerDO.getSocialCreditDetail());
        }
    }


    /**
     * 任务状态
     *
     * @param taskInfo
     * @param taskStatusCondition
     * @return
     */
    private Integer getTaskStatus(TaskInfo taskInfo, Integer taskStatusCondition) {
        Integer taskStatus = taskStatusCondition;
        if (TASK_ALL.equals(taskStatusCondition)) {
            HistoricTaskInstanceEntity historicTaskInstanceEntity = (HistoricTaskInstanceEntity) taskInfo;
            Date endTime = historicTaskInstanceEntity.getEndTime();
            if (null != endTime) {
                // 已处理
                taskStatus = TASK_DONE;
            } else {
                // 未处理
                taskStatus = TASK_TODO;
            }
        }
        return taskStatus;
    }

    /**
     * 根据流程实例ID获取并填充业务单基本信息
     *
     * @param appLoanOrderVO
     * @param processInstanceId
     * @return
     */
    private void fillBaseMsg(AppLoanOrderVO appLoanOrderVO, String processInstanceId) {
        // 业务单
        LoanOrderDO loanOrderDO = loanOrderDOMapper.getByProcessInstId(processInstanceId);
        if (null == loanOrderDO) {
            return;
        }

        // 业务单单号
        appLoanOrderVO.setId(loanOrderDO.getId());
        // 业务单创建时间
        appLoanOrderVO.setGmtCreate(loanOrderDO.getGmtCreate());

        // 主贷人信息
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            BaseVO customer = new BaseVO();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            // 主贷人
            appLoanOrderVO.setCustomer(customer);
            // 身份证
            appLoanOrderVO.setIdCard(loanCustomerDO.getIdCard());
            // 手机号
            appLoanOrderVO.setMobile(loanCustomerDO.getMobile());
        }

        // 合伙人 & 业务员
        ResultBean<LoanBaseInfoVO> loanBaseInfoResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkArgument(loanBaseInfoResultBean.getSuccess(), loanBaseInfoResultBean.getMsg());
        LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoResultBean.getData();
        if (null != loanBaseInfoVO) {
            // 合伙人
            appLoanOrderVO.setPartner(loanBaseInfoVO.getPartner());
            // 业务员
            appLoanOrderVO.setSalesman(loanBaseInfoVO.getSalesman());
        }
    }

    /**
     * 创建任务查询对象
     *
     * @param query
     * @param userGroupNameList
     * @return
     */
    private TaskInfoQuery getTaskInfoQuery(AppLoanOrderQuery query, List<String> userGroupNameList) {
        TaskInfoQuery taskQuery = null;
        // 全部
        if (TASK_ALL.equals(query.getTaskStatus())) {
            taskQuery = historyService.createHistoricTaskInstanceQuery()
                    .taskDefinitionKey(query.getTaskDefinitionKey())
                    .taskCandidateGroupIn(userGroupNameList);
        }
        // 待处理
        else if (TASK_TODO.equals(query.getTaskStatus())) {
            taskQuery = taskService.createTaskQuery()
                    .taskDefinitionKey(query.getTaskDefinitionKey())
                    .taskCandidateGroupIn(userGroupNameList);
        }
        // 已处理
        else if (TASK_DONE.equals(query.getTaskStatus())) {
            taskQuery = historyService.createHistoricTaskInstanceQuery()
                    .taskDefinitionKey(query.getTaskDefinitionKey())
                    .taskCandidateGroupIn(userGroupNameList)
                    .finished();
        }
        return taskQuery;
    }

    /**
     * 获取用户组名称
     *
     * @return
     */
    public List<String> getUserGroupNameList() {
        // getUser
        EmployeeDO loginUser = SessionUtils.getLoginUser();

        // getUserGroup
        List<UserGroupDO> baseUserGroup = userGroupDOMapper.getBaseUserGroupByEmployeeId(loginUser.getId());

        // getUserGroupName
        List<String> userGroupNameList = null;
        if (!CollectionUtils.isEmpty(baseUserGroup)) {
            userGroupNameList = baseUserGroup.parallelStream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        return e.getName();
                    })
                    .collect(Collectors.toList());
        }
        return userGroupNameList;
    }

    /**
     * 创建上门家访资料
     *
     * @param loanHomeVisitParam
     */
    private void createLoanHomeVisit(AppLoanHomeVisitParam loanHomeVisitParam) {
        Preconditions.checkNotNull(loanHomeVisitParam.getOrderId(), "业务单号不能为空");

        // insert
        LoanHomeVisitDO loanHomeVisitDO = new LoanHomeVisitDO();
        BeanUtils.copyProperties(loanHomeVisitParam, loanHomeVisitDO);
        loanHomeVisitDO.setGmtCreate(new Date());
        loanHomeVisitDO.setGmtModify(new Date());
        loanHomeVisitDO.setStatus(VALID_STATUS);

        int count = loanHomeVisitDOMapper.insertSelective(loanHomeVisitDO);
        Preconditions.checkArgument(count > 0, "创建上门家访资料失败");

        // 关联
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(loanHomeVisitParam.getOrderId());
        loanOrderDO.setLoanHomeVisitId(loanHomeVisitParam.getId());
        loanOrderDO.setGmtModify(new Date());

        int relaCount = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        Preconditions.checkArgument(relaCount > 0, "关联上门家访资料失败");
    }

    /**
     * 编辑上门家访资料
     *
     * @param loanHomeVisitParam
     */
    private void updateLoanHomeVisit(AppLoanHomeVisitParam loanHomeVisitParam) {
        LoanHomeVisitDO loanHomeVisitDO = new LoanHomeVisitDO();
        BeanUtils.copyProperties(loanHomeVisitParam, loanHomeVisitDO);
        loanHomeVisitDO.setGmtModify(new Date());

        int count = loanHomeVisitDOMapper.updateByPrimaryKeySelective(loanHomeVisitDO);
        Preconditions.checkArgument(count > 0, "编辑上门家访资料失败");
    }

    private void convertLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam, LoanCarInfoDO loanCarInfoDO) {
        BeanUtils.copyProperties(loanCarInfoParam, loanCarInfoDO);

        BaseVO carDetail = loanCarInfoParam.getCarDetail();
        if (null != carDetail) {
            loanCarInfoDO.setCarDetailId(carDetail.getId());
            loanCarInfoDO.setCarDetailName(carDetail.getName());
        }

        AppLoanCarInfoParam.PartnerAccountInfo partnerAccountInfo = loanCarInfoParam.getPartnerAccountInfo();
        if (null != partnerAccountInfo) {
            BeanUtils.copyProperties(partnerAccountInfo, loanCarInfoDO);
        }
    }

    private void convertLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam, LoanFinancialPlanDO loanFinancialPlanDO) {
        BeanUtils.copyProperties(appLoanFinancialPlanParam, loanFinancialPlanDO);

        BaseVO flinancialProduct = appLoanFinancialPlanParam.getFlinancialProduct();
        if (null != flinancialProduct) {
            loanFinancialPlanDO.setFinancialProductId(flinancialProduct.getId());
            loanFinancialPlanDO.setFinancialProductName(flinancialProduct.getName());
        }
    }

    private void convertLoanCustomer(AppCustomerParam customerParam, LoanCustomerDO loanCustomerDO) {
        if (null != customerParam) {
            BeanUtils.copyProperties(customerParam, loanCustomerDO);
        }
    }

    private void convertLoanBaseInfo(AppLoanBaseInfoDetailParam loanBaseInfo, LoanBaseInfoDO loanBaseInfoDO) {
        if (null != loanBaseInfo) {
            BeanUtils.copyProperties(loanBaseInfo, loanBaseInfoDO);

            BaseVO area = loanBaseInfo.getArea();
            if (null != area) {
                loanBaseInfoDO.setAreaId(area.getId());
            }

            BaseVO partner = loanBaseInfo.getPartner();
            if (null != partner) {
                loanBaseInfoDO.setPartnerId(partner.getId());
            }

            BaseVO salesman = loanBaseInfo.getSalesman();
            if (null != salesman) {
                loanBaseInfoDO.setSalesmanId(salesman.getId());
            }
        }
    }

    /**
     * 创建客户信息
     *
     * @param customerParam
     * @return
     */
    private Long createLoanCustomer(AppCustomerParam customerParam) {
        // convert
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        convertLoanCustomer(customerParam, loanCustomerDO);
        loanCustomerDO.setCustType(CUST_TYPE_PRINCIPAL);

        // insert
        ResultBean<Long> createCustomerResult = loanCustomerService.create(loanCustomerDO);
        Preconditions.checkArgument(createCustomerResult.getSuccess(), createCustomerResult.getMsg());

        // todo 文件上传
//        ResultBean<Void> createFileResultBean = loanFileService.create(createCustomerResult.getData(), customerParam.getFiles());
//        Preconditions.checkArgument(createFileResultBean.getSuccess(), createFileResultBean.getMsg());

        // 返回客户ID
        return createCustomerResult.getData();
    }

    /**
     * 校验权限：只有合伙人、内勤可以 发起征信申请单【创建业务单】
     */
    private void checkStartProcessPermission() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        EmployeeDO user = new EmployeeDO();
        BeanUtils.copyProperties(principal, user);

        // TODO 只有合伙人、内勤可以 发起征信申请单【创建业务单】
        // 获取用户角色名列表
        List<String> userGroupNameList = getUserGroupNameList();
        Preconditions.checkArgument(userGroupNameList.contains("合伙人") || userGroupNameList.contains("内勤"),
                "您无权创建征信申请单");
    }
}
