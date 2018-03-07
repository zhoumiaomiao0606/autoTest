package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.mapper.*;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CustomerConst.*;
import static com.yunche.loan.config.constant.LoanCustomerConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.*;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
@Transactional
public class LoanOrderServiceImpl implements LoanOrderService {

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private UserGroupDOMapper userGroupDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private LoanBaseInfoService loanBaseInfoService;

    @Autowired
    private LoanProcessOrderService loanProcessOrderService;

    @Autowired
    private LoanCreditInfoService loanCreditInfoService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;


    @Override
    public ResultBean<List<BaseInstProcessOrderVO>> query(LoanOrderQuery query) {
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
            List<TaskInfo> tasks = taskQuery.listPage(query.getStartRow(), query.getEndRow());

            // 获取流程列表 -> 业务单列表
            if (!CollectionUtils.isEmpty(tasks)) {
                List<BaseInstProcessOrderVO> baseInstProcessOrderVOList = tasks.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 任务状态
                            Byte taskStatus = getTaskStatus(e, query.getTaskStatus());

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstanceId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                BaseInstProcessOrderVO baseInstProcessOrderVO = new BaseInstProcessOrderVO();
                                baseInstProcessOrderVO.setTaskStatus(taskStatus);
                                fillMsg(baseInstProcessOrderVO, processInstanceId);
                                return baseInstProcessOrderVO;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(baseInstProcessOrderVOList, (int) totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, (int) totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        // 订单基本信息
        CreditApplyOrderVO creditApplyOrderVO = new CreditApplyOrderVO();
        BeanUtils.copyProperties(loanOrderDO, creditApplyOrderVO);

        // 关联的-客户信息(主贷人/共贷人/担保人/紧急联系人)
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId);
        BeanUtils.copyProperties(custDetailVOResultBean.getData(), creditApplyOrderVO);

        // 关联的-贷款基本信息
        ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = creditService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        creditApplyOrderVO.setLoanBaseInfo(loanBaseInfoVOResultBean.getData());

        return ResultBean.ofSuccess(creditApplyOrderVO, "查询征信申请单详情成功");
    }

    @Override
    public ResultBean<Long> createCreditApplyOrder(CreditApplyOrderVO creditApplyOrderVO) {
        Preconditions.checkNotNull(creditApplyOrderVO, "不能为空");

        // 创建贷款基本信息
        Long baseInfoId = createLoanBaseInfo(creditApplyOrderVO.getLoanBaseInfo());

        // 创建客户信息
        Long customerId = createLoanCustomer(creditApplyOrderVO);

        // 创建订单
        Long orderId = createLoanOrder(baseInfoId, customerId);

        return ResultBean.ofSuccess(orderId);
    }


    @Override
    public ResultBean<Void> updateCreditApplyOrder(CreditApplyOrderVO creditApplyOrderVO) {
        Preconditions.checkNotNull(creditApplyOrderVO.getOrderId(), "业务单号不能为空");

        // 编辑贷款基本信息
        updateLoanBaseInfo(creditApplyOrderVO.getLoanBaseInfo());

        // 编辑客户信息
        updateOrInsertLoanCustomer(creditApplyOrderVO);

        return ResultBean.ofSuccess(null);
    }

    private void updateLoanBaseInfo(LoanBaseInfoVO loanBaseInfoVO) {
        ResultBean<Void> updateResult = loanBaseInfoService.update(loanBaseInfoVO);
        Preconditions.checkArgument(updateResult.getSuccess(), updateResult.getMsg());
    }

    private void updateOrInsertLoanCustomer(CreditApplyOrderVO creditApplyOrderVO) {
        CustDetailParam custDetailParam = new CustDetailParam();
        BeanUtils.copyProperties(creditApplyOrderVO, custDetailParam);
        loanCustomerService.updateAll(custDetailParam);

        CustomerVO principalLender = creditApplyOrderVO.getPrincipalLender();
        updateOrInsertCustomer(principalLender);
    }

    private void updateOrInsertCustomer(CustomerVO customerVO) {
        if (null == customerVO) {
            return;
        }

        if (null == customerVO.getId()) {
            // insert
            createLoanCustomer(customerVO);
        } else {
            // update
            ResultBean<Void> resultBean = loanCustomerService.update(customerVO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

            // file
            ResultBean<Void> updateFileResultBean = loanFileService.update(customerVO.getId(), customerVO.getFiles());
            Preconditions.checkArgument(updateFileResultBean.getSuccess(), updateFileResultBean.getMsg());
        }
    }


    private Long createLoanOrder(Long baseInfoId, Long customerId) {
        // 开启activiti流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("loan_process");
        Preconditions.checkNotNull(processInstance, "开启流程实例异常");
        Preconditions.checkNotNull(processInstance.getProcessInstanceId(), "开启流程实例异常");

        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setProcessInstId(processInstance.getProcessInstanceId());
        loanOrderDO.setLoanCustomerId(customerId);
        loanOrderDO.setLoanBaseInfoId(baseInfoId);
        loanOrderDO.setStatus(VALID_STATUS);
        ResultBean<Long> createResultBean = loanProcessOrderService.create(loanOrderDO);
        Preconditions.checkArgument(createResultBean.getSuccess(), createResultBean.getMsg());
        return createResultBean.getData();
    }

    private Long createLoanCustomer(CreditApplyOrderVO creditApplyOrderVO) {
        // 主贷人
        Long principalLenderId = createLoanCustomer(creditApplyOrderVO.getPrincipalLender());

        List<CustomerVO> commonLenderList = creditApplyOrderVO.getCommonLenderList();
        List<CustomerVO> guarantorList = creditApplyOrderVO.getGuarantorList();
        List<CustomerVO> emergencyContactList = creditApplyOrderVO.getEmergencyContactList();

        createLoanCustomerList(commonLenderList);
        createLoanCustomerList(guarantorList);
        createLoanCustomerList(emergencyContactList);

        return principalLenderId;
    }

    private void createLoanCustomerList(List<CustomerVO> commonLenderList) {
        if (!CollectionUtils.isEmpty(commonLenderList)) {
            commonLenderList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        createLoanCustomer(e);
                    });
        }
    }

    private Long createLoanCustomer(CustomerVO customerVO) {
        ResultBean<Long> createCustomerResult = loanCustomerService.create(customerVO);
        Preconditions.checkArgument(createCustomerResult.getSuccess(), "创建客户信息失败");

        // 文件上传
        ResultBean<Void> createFileResultBean = loanFileService.create(createCustomerResult.getData(), customerVO.getFiles());
        Preconditions.checkArgument(createFileResultBean.getSuccess(), "创建文件信息失败");

        // 返回客户ID
        return createCustomerResult.getData();
    }

    private Long createLoanBaseInfo(LoanBaseInfoVO loanBaseInfoVO) {
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(loanBaseInfoVO, loanBaseInfoDO);
        int count = loanBaseInfoDOMapper.insertSelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "插入贷款失败");
        return loanBaseInfoDO.getId();
    }


    @Override
    public ResultBean<CreditRecordVO> creditRecordDetail(Long orderId, Byte type) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkNotNull(type, "征信类型不能为空");
        Preconditions.checkNotNull(CREDIT_TYPE_BANK.equals(type) || CREDIT_TYPE_SOCIAL.equals(type), "征信类型有误");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        CreditRecordVO creditRecordVO = new CreditRecordVO();

        // 贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        if (null != loanBaseInfoDO) {
            LoanBaseInfoVO loanBaseInfo = new LoanBaseInfoVO();
            BeanUtils.copyProperties(loanBaseInfoDO, loanBaseInfo);
            creditRecordVO.setLoanBaseInfo(loanBaseInfo);
        }

        // 客户信息
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            // 主
            CreditRecordVO.Customer customer = new CreditRecordVO.Customer();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            fillCreditMsg(customer, type);
            creditRecordVO.setPrincipalLender(customer);

            // 共
            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.listByIdAndType(loanCustomerDO.getId(), COMMON_LENDER);
            if (!CollectionUtils.isEmpty(loanCustomerDOS)) {
                List<CreditRecordVO.Customer> commonLenderList = loanCustomerDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            CreditRecordVO.Customer commonLender = new CreditRecordVO.Customer();
                            BeanUtils.copyProperties(e, commonLender);
                            fillCreditMsg(commonLender, type);
                            return commonLender;
                        })
                        .collect(Collectors.toList());
                creditRecordVO.setCommonLenderList(commonLenderList);
            }
        }

        return ResultBean.ofSuccess(creditRecordVO, "征信录入详情查询成功");
    }

    @Override
    public ResultBean<Long> creditRecord(CreditRecordParam creditRecordParam) {
        if (null == creditRecordParam.getId()) {
            // insert
            ResultBean<Long> resultBean = loanCreditInfoService.create(creditRecordParam);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
            return ResultBean.ofSuccess(resultBean.getData(), "征信结果录入成功");
        } else {
            // update
            ResultBean<Long> resultBean = loanCreditInfoService.update(creditRecordParam);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
            return ResultBean.ofSuccess(resultBean.getData(), "征信结果修改成功");
        }
    }


    @Override
    public ResultBean<CustDetailVO> customerDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据orderId获取主贷人ID
        Long principalLenderId = loanOrderDOMapper.getCustIdById(orderId);

        // 根据主贷人ID获取客户详情列表
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId);
        return custDetailVOResultBean;
    }

    @Override
    public ResultBean<Void> updateCustomer(CustDetailVO custDetailVO) {
        Preconditions.checkNotNull(custDetailVO, "客户信息不能为空");

        CustDetailParam custDetailParam = new CustDetailParam();
        BeanUtils.copyProperties(custDetailVO, custDetailParam);
        loanCustomerService.updateAll(custDetailParam);

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }

    @Override
    public ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");
        Preconditions.checkNotNull(principalLenderId, "主贷人ID不能为空");
        Preconditions.checkNotNull(commonLenderId, "共贷人ID不能为空");

        // TODO 交互数据   编辑原主贷人
        LoanCustomerDO principalLenderDO = new LoanCustomerDO();
        principalLenderDO.setId(principalLenderId);
        principalLenderDO.setCustType(CUST_TYPE_COMMON);
        principalLenderDO.setPrincipalCustId(commonLenderId);
        principalLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(principalLenderDO);

        // 编辑原共贷人
        LoanCustomerDO commonLenderDO = new LoanCustomerDO();
        commonLenderDO.setId(commonLenderId);
        commonLenderDO.setCustType(CUST_TYPE_PRINCIPAL);
        commonLenderDO.setPrincipalCustId(null);
        commonLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(commonLenderDO);

        // 编辑所有关联人的 主贷人ID   TODO  修改时间
        loanCustomerDOMapper.updatePrincipalCustId(principalLenderId, commonLenderId);

        // 编辑业务单主贷人
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(orderId);
        loanOrderDO.setLoanCustomerId(commonLenderId);
        loanOrderDO.setGmtModify(new Date());
        loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

        return ResultBean.ofSuccess(null, "主贷人和共贷人切换成功");
    }

    @Override
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        Long loanCarInfoId = loanOrderDOMapper.getLoanCarInfoIdById(orderId);

        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanCarInfoId);
        LoanCarInfoVO loanCarInfoVO = new LoanCarInfoVO();
        BeanUtils.copyProperties(loanCarInfoDO, loanCarInfoVO);

        return ResultBean.ofSuccess(loanCarInfoVO);
    }

    @Override
    public ResultBean<Void> createOrUpdateLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkNotNull(loanCarInfoParam, "贷款车辆信息不能为空");

        if (null == loanCarInfoParam.getId()) {
            // 创建
            createLoanCarInfo(loanCarInfoParam);
        } else {
            // 编辑
            updateLoanCarInfo(loanCarInfoParam);
        }

        return ResultBean.ofSuccess(null, "保存贷款车辆信息成功");
    }

    @Override
    public ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        Long loanFinancialPlanId = loanOrderDOMapper.getLoanFinancialPlanIdById(orderId);

        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);
        LoanFinancialPlanVO loanFinancialPlanVO = new LoanFinancialPlanVO();
        BeanUtils.copyProperties(loanFinancialPlanDO, loanFinancialPlanVO);

        return ResultBean.ofSuccess(loanFinancialPlanVO);
    }

    @Override
    public ResultBean<Void> createOrUpdateLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam, "贷款金融方案不能为空");

        if (null == loanFinancialPlanParam.getId()) {
            // 创建
            createLoanFinancialPlan(loanFinancialPlanParam);
        } else {
            // 编辑
            updateLoanFinancialPlan(loanFinancialPlanParam);
        }

        return ResultBean.ofSuccess(null, "保存贷款金融方案成功");
    }


    @Override
    public ResultBean<LoanHomeVisitVO> homeVisitDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        Long loanHomeVisitId = loanOrderDOMapper.getLoanHomeVisitId(orderId);

        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanHomeVisitId);
        LoanHomeVisitVO loanHomeVisitVO = new LoanHomeVisitVO();
        BeanUtils.copyProperties(loanHomeVisitDO, loanHomeVisitVO);

        return ResultBean.ofSuccess(loanHomeVisitVO);
    }

    @Override
    public ResultBean<Void> createOrUpdateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam) {
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
    public ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam.getCarPrice(), "车辆价格不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getBizModelId(), "业务产品ID不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getSignRate(), "签约利率不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getLoanAmount(), "贷款金额不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getLoanTime(), "贷款期数不能为空");

        // TODO 根据公式计算


        return ResultBean.ofSuccess(null);
    }

    @Override
    public ResultBean<Void> infoSupplement(InfoSupplementParam infoSupplementParam) {
        Preconditions.checkNotNull(infoSupplementParam.getCustomerId(), "客户ID不能为空");
        Preconditions.checkArgument(CollectionUtils.isEmpty(infoSupplementParam.getFiles()), "资料信息不能为空");

        List<FileVO> newFiles = infoSupplementParam.getFiles();
        newFiles.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {
//                    e.setIsSupplement((byte) 1);
                });

        String existFiles = loanCustomerDOMapper.getFilesById(infoSupplementParam.getCustomerId());
        if (StringUtils.isNotBlank(existFiles)) {
            List<FileVO> existFileList = JSON.parseArray(existFiles, FileVO.class);
            newFiles.addAll(existFileList);
            newFiles.removeAll(Collections.singleton(null));
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        loanCustomerDO.setId(infoSupplementParam.getCustomerId());
//        loanCustomerDO.setFiles(JSON.toJSONString(newFiles));
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "资料增补失败");

        return ResultBean.ofSuccess(null, "资料增补成功");
    }

    /**
     * 填充征信信息
     *
     * @param customer
     * @param type
     */
    private void fillCreditMsg(CreditRecordVO.Customer customer, Byte type) {
        ResultBean<LoanCreditInfoVO> resultBean = loanCreditInfoService.getByCustomerId(customer.getId(), type);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        LoanCreditInfoVO loanCreditInfoVO = resultBean.getData();
        customer.setCreditResult(loanCreditInfoVO.getResult());
        customer.setCreditInfo(loanCreditInfoVO.getInfo());
    }


    /**
     * 任务状态
     *
     * @param taskInfo
     * @param taskStatusCondition
     * @return
     */
    private Byte getTaskStatus(TaskInfo taskInfo, Byte taskStatusCondition) {
        Byte taskStatus = taskStatusCondition;
        if (TASK_ALL.equals(taskStatusCondition)) {
            HistoricTaskInstanceEntity historicTaskInstanceEntity = (HistoricTaskInstanceEntity) taskInfo;
            String deleteReason = historicTaskInstanceEntity.getDeleteReason();
            if (DELETE_RELEASE_HAS_DONE.equals(deleteReason)) {
                // 已处理
                taskStatus = TASK_DONE;
            } else if (StringUtils.isBlank(deleteReason)) {
                // 未处理
                taskStatus = TASK_TODO;
            }
        }
        return taskStatus;
    }

    /**
     * 根据流程实例ID获取并填充业务单基本信息
     *
     * @param baseInstProcessOrderVO
     * @param processInstanceId
     * @return
     */
    private void fillMsg(BaseInstProcessOrderVO baseInstProcessOrderVO, String processInstanceId) {
        // 业务单
        LoanOrderDO loanOrderDO = loanOrderDOMapper.getByProcessInstId(processInstanceId);
        if (null == loanOrderDO) {
            return;
        }

        // 订单基本信息
        BeanUtils.copyProperties(loanOrderDO, baseInstProcessOrderVO);

        // 关联的-客户信息(主贷人)
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            BaseVO customer = new BaseVO();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            baseInstProcessOrderVO.setCustomer(customer);
            baseInstProcessOrderVO.setIdCard(loanCustomerDO.getIdCard());
            baseInstProcessOrderVO.setMobile(loanCustomerDO.getMobile());
        }

        // 关联的-贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        if (null != loanBaseInfoDO) {
            BaseVO salesman = new BaseVO();
            BeanUtils.copyProperties(loanBaseInfoDO, salesman);
            baseInstProcessOrderVO.setSalesman(salesman);
        }
    }

    /**
     * 创建任务查询对象
     *
     * @param query
     * @param userGroupNameList
     * @return
     */
    private TaskInfoQuery getTaskInfoQuery(LoanOrderQuery query, List<String> userGroupNameList) {
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
        Object principal = SecurityUtils.getSubject().getPrincipal();
        EmployeeDO employeeDO = new EmployeeDO();
        BeanUtils.copyProperties(principal, employeeDO);

        // getUserGroup
        List<UserGroupDO> baseUserGroup = userGroupDOMapper.getBaseUserGroupByEmployeeId(employeeDO.getId());

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
     * 填充客户详情信息
     *
     * @param custDetailVO
     * @param loanCustomerDOList
     */
    private void fillCustInfo(CustDetailVO custDetailVO, List<LoanCustomerDO> loanCustomerDOList) {

        List<CustomerVO> commonLenderList = Lists.newArrayList();
        List<CustomerVO> guarantorList = Lists.newArrayList();
        List<CustomerVO> emergencyContactList = Lists.newArrayList();

        loanCustomerDOList.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    // 主贷人
                    if (CUST_TYPE_PRINCIPAL.equals(e.getCustType())) {
                        CustomerVO principalLender = new CustomerVO();
                        BeanUtils.copyProperties(e, principalLender);

//                        principalLender.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        custDetailVO.setPrincipalLender(principalLender);
                    }
                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {
                        CustomerVO commonLender = new CustomerVO();
                        BeanUtils.copyProperties(e, commonLender);
//                        commonLender.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        commonLenderList.add(commonLender);
                    }
                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {
                        CustomerVO guarantor = new CustomerVO();
                        BeanUtils.copyProperties(e, guarantor);
//                        guarantor.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        guarantorList.add(guarantor);
                    }
                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {
                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);
//                        emergencyContact.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        emergencyContactList.add(emergencyContact);
                    }
                });

        custDetailVO.setCommonLenderList(commonLenderList);
        custDetailVO.setGuarantorList(guarantorList);
        custDetailVO.setEmergencyContactList(emergencyContactList);
    }

    /**
     * insert贷款金融方案
     *
     * @param loanFinancialPlanParam
     */
    private void createLoanFinancialPlan(LoanFinancialPlanParam loanFinancialPlanParam) {
        Preconditions.checkNotNull(loanFinancialPlanParam.getOrderId(), "业务单号不能为空");

        // insert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanFinancialPlanParam, loanFinancialPlanDO);
        loanFinancialPlanDO.setGmtCreate(new Date());
        loanFinancialPlanDO.setGmtModify(new Date());
        loanFinancialPlanDO.setStatus(VALID_STATUS);

        int count = loanFinancialPlanDOMapper.insertSelective(loanFinancialPlanDO);
        Preconditions.checkArgument(count > 0, "创建贷款金融方案失败");

        // 关联
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(loanFinancialPlanParam.getOrderId());
        loanOrderDO.setLoanFinancialPlanId(loanFinancialPlanParam.getId());
        loanOrderDO.setGmtModify(new Date());

        int relaCount = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        Preconditions.checkArgument(relaCount > 0, "关联贷款金融方案失败");
    }

    /**
     * update贷款金融方案
     *
     * @param loanFinancialPlanVO
     */
    private void updateLoanFinancialPlan(LoanFinancialPlanVO loanFinancialPlanVO) {
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanFinancialPlanVO, loanFinancialPlanDO);
        loanFinancialPlanDO.setGmtModify(new Date());

        int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
        Preconditions.checkArgument(count > 0, "编辑贷款金融方案失败");
    }

    /**
     * 创建贷款车辆信息
     *
     * @param loanCarInfoParam
     */
    private void createLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkNotNull(loanCarInfoParam.getOrderId(), "业务单号不能为空");

        // insert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        BeanUtils.copyProperties(loanCarInfoParam, loanCarInfoDO);
        loanCarInfoDO.setGmtCreate(new Date());
        loanCarInfoDO.setGmtModify(new Date());
        loanCarInfoDO.setStatus(VALID_STATUS);

        int count = loanCarInfoDOMapper.insertSelective(loanCarInfoDO);
        Preconditions.checkArgument(count > 0, "创建贷款车辆信息失败");

        // 关联
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(loanCarInfoParam.getOrderId());
        loanOrderDO.setLoanCarInfoId(loanCarInfoParam.getId());
        loanOrderDO.setGmtModify(new Date());

        int relaCount = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        Preconditions.checkArgument(relaCount > 0, "关联贷款车辆信息失败");
    }

    /**
     * 编辑贷款车辆信息失败
     *
     * @param loanCarInfoParam
     */
    private void updateLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        BeanUtils.copyProperties(loanCarInfoParam, loanCarInfoDO);
        loanCarInfoDO.setGmtModify(new Date());

        int count = loanCarInfoDOMapper.updateByPrimaryKeySelective(loanCarInfoDO);
        Preconditions.checkArgument(count > 0, "编辑贷款车辆信息失败");
    }

    /**
     * 创建上门家访资料
     *
     * @param loanHomeVisitParam
     */
    private void createLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam) {
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
    private void updateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam) {
        LoanHomeVisitDO loanHomeVisitDO = new LoanHomeVisitDO();
        BeanUtils.copyProperties(loanHomeVisitParam, loanHomeVisitDO);
        loanHomeVisitDO.setGmtModify(new Date());

        int count = loanHomeVisitDOMapper.updateByPrimaryKeySelective(loanHomeVisitDO);
        Preconditions.checkArgument(count > 0, "编辑上门家访资料失败");
    }
}
