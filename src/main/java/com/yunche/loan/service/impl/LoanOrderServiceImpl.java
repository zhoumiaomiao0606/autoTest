package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.*;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.CreditService;
import com.yunche.loan.service.CustomerService;
import com.yunche.loan.service.LoanOrderService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
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
import static com.yunche.loan.config.constant.LoanProcessConst.*;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
@Transactional
public class LoanOrderServiceImpl implements LoanOrderService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CreditService creditService;

    @Autowired
    private LoanProcessOrderDOMapper loanProcessOrderDOMapper;

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
    public ResultBean<InstProcessOrderVO> creditApplyDetail(String id) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "业务单号不能为空");

        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(loanProcessOrderDO, "业务单号不存在");

        // 订单基本信息
        InstProcessOrderVO instProcessOrderVO = new InstProcessOrderVO();
        BeanUtils.copyProperties(loanProcessOrderDO, instProcessOrderVO);

        // 关联的-客户信息(主贷人/共贷人/担保人/紧急联系人)
        ResultBean<CustDetailVO> custDetailVOResultBean = customerService.detailAll(id);
        instProcessOrderVO.setCustDetail(custDetailVOResultBean.getData());

        // 关联的-贷款基本信息
        ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = creditService.getLoanBaseInfoById(loanProcessOrderDO.getLoanBaseInfoId());
        instProcessOrderVO.setLoanBaseInfo(loanBaseInfoVOResultBean.getData());


//        // 流程操作记录
//        List<InstProcessNodeDO> instProcessNodeDOList = instProcessNodeDOMapper.selectByOrderId(orderId);
//        if (CollectionUtils.isNotEmpty(instProcessNodeDOList)) {
//            List<InstProcessNodeVO> instProcessNodeVOList = Lists.newArrayList();
//            for (InstProcessNodeDO instProcessNodeDO : instProcessNodeDOList) {
//                InstProcessNodeVO instProcessNodeVO = new InstProcessNodeVO();
//                BeanUtils.copyProperties(instProcessNodeDO, instProcessNodeVO);
//                instProcessNodeVOList.add(instProcessNodeVO);
//            }
//            instLoanOrderVO.setProcessRecordList(instProcessNodeVOList);
//        }
//
//        // 待执行流程
//        List<ActRuTaskDO> actRuTaskDOList = actRuTaskDOMapper.selectByProcInstId(instLoanOrderDO.getProcessInstId());
//        if (CollectionUtils.isNotEmpty(actRuTaskDOList)) {
//            Map<String, String> todoProcessNodeMap = Maps.newConcurrentMap();
//            for (ActRuTaskDO actRuTaskDO : actRuTaskDOList) {
//                todoProcessNodeMap.put(actRuTaskDO.getTaskDefKey(), actRuTaskDO.getName());
//            }
//            instLoanOrderVO.setTodoProcessMap(todoProcessNodeMap);
//        }

        return ResultBean.ofSuccess(instProcessOrderVO, "查询业务单详情成功");
    }

    @Override
    public ResultBean<CreditRecordVO> creditRecordDetail(String id, Byte type) {
        Preconditions.checkArgument(StringUtils.isNotBlank(id), "业务单号不能为空");
        Preconditions.checkNotNull(type, "征信类型不能为空");
        Preconditions.checkNotNull(CREDIT_TYPE_BANK.equals(type) || CREDIT_TYPE_SOCIAL.equals(type), "征信类型有误");

        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(loanProcessOrderDO, "业务单号不存在");

        CreditRecordVO creditRecordVO = new CreditRecordVO();

        // 贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanProcessOrderDO.getLoanBaseInfoId());
        if (null != loanBaseInfoDO) {
            LoanBaseInfoVO loanBaseInfo = new LoanBaseInfoVO();
            BeanUtils.copyProperties(loanBaseInfoDO, loanBaseInfo);
            creditRecordVO.setLoanBaseInfo(loanBaseInfo);
        }

        // 客户信息
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanProcessOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            // 主
            CreditRecordVO.Customer customer = new CreditRecordVO.Customer();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            fillCreditMsg(customer, loanCustomerDO, type);
            creditRecordVO.setPrincipalLender(customer);

            // 共
            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.getListById(loanCustomerDO.getId());
            if (!CollectionUtils.isEmpty(loanCustomerDOS)) {
                List<CreditRecordVO.Customer> commonLenderList = loanCustomerDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {
                            CreditRecordVO.Customer commonLender = new CreditRecordVO.Customer();
                            BeanUtils.copyProperties(loanCustomerDO, commonLender);
                            fillCreditMsg(customer, loanCustomerDO, type);
                            return commonLender;
                        })
                        .collect(Collectors.toList());
                creditRecordVO.setCommonLenderList(commonLenderList);
            }
        }

        return ResultBean.ofSuccess(creditRecordVO, "征信录入详情查询成功");
    }

    @Override
    public ResultBean<Void> creditRecord(CreditRecordParam creditRecordParam) {
        Preconditions.checkNotNull(creditRecordParam.getId(), "客户ID不能为空");
        Preconditions.checkNotNull(creditRecordParam.getType(), "征信类型不能为空");
        Preconditions.checkNotNull(creditRecordParam.getCreditStatus(), "征信结果不能为空");

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        // 征信结果
        if (CREDIT_TYPE_BANK.equals(creditRecordParam.getType())) {
            loanCustomerDO.setBankCreditStatus(creditRecordParam.getCreditStatus());
            loanCustomerDO.setBankCreditDetail(creditRecordParam.getCreditDetail());
        } else if (CREDIT_TYPE_SOCIAL.equals(creditRecordParam.getType())) {
            loanCustomerDO.setSocialCreditStatus(creditRecordParam.getCreditStatus());
            loanCustomerDO.setSocialCreditDetail(creditRecordParam.getCreditDetail());
        }
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.insertSelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "征信结果录入失败");

        return ResultBean.ofSuccess(null, "征信结果录入成功");
    }


    @Override
    public ResultBean<CustDetailVO> customerDetail(String orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据orderId获取主贷人ID
        Long principalLenderId = loanProcessOrderDOMapper.getCustIdById(orderId);

        // 根据主贷人ID获取客户详情列表
        List<LoanCustomerDO> loanCustomerDOList = loanCustomerDOMapper.getListById(principalLenderId);

        CustDetailVO custDetailVO = new CustDetailVO();
        if (!CollectionUtils.isEmpty(loanCustomerDOList)) {
            // 填充客户详情信息
            fillCustInfo(custDetailVO, loanCustomerDOList);
        }

        return ResultBean.ofSuccess(custDetailVO);
    }

    @Override
    public ResultBean<Void> updateCustomer(CustDetailVO custDetailVO) {
        Preconditions.checkNotNull(custDetailVO, "客户信息不能为空");

        // 主贷人
        CustomerVO principalLenderVO = custDetailVO.getPrincipalLender();
        LoanCustomerDO principalLenderDO = new LoanCustomerDO();
        BeanUtils.copyProperties(principalLenderVO, principalLenderDO);
        principalLenderDO.setFiles(JSON.toJSONString(principalLenderVO.getFiles()));
        principalLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(principalLenderDO);

        // 共贷人列表
        List<CustomerVO> commonLenderVOList = custDetailVO.getCommonLenderList();
        if (!CollectionUtils.isEmpty(commonLenderVOList)) {

            commonLenderVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        LoanCustomerDO commonLenderDO = new LoanCustomerDO();
                        BeanUtils.copyProperties(e, commonLenderDO);
                        commonLenderDO.setFiles(JSON.toJSONString(e.getFiles()));
                        commonLenderDO.setGmtModify(new Date());
                        loanCustomerDOMapper.updateByPrimaryKeySelective(commonLenderDO);
                    });
        }

        // 担保人列表
        List<CustomerVO> guarantorVOList = custDetailVO.getGuarantorList();
        if (!CollectionUtils.isEmpty(guarantorVOList)) {

            guarantorVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        LoanCustomerDO guarantorDO = new LoanCustomerDO();
                        BeanUtils.copyProperties(e, guarantorDO);
                        guarantorDO.setFiles(JSON.toJSONString(e.getFiles()));
                        guarantorDO.setGmtModify(new Date());
                        loanCustomerDOMapper.updateByPrimaryKeySelective(guarantorDO);
                    });
        }

        // 紧急联系人列表
        List<CustomerVO> emergencyContactVOList = custDetailVO.getEmergencyContactList();
        if (!CollectionUtils.isEmpty(emergencyContactVOList)) {

            emergencyContactVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        LoanCustomerDO emergencyContactDO = new LoanCustomerDO();
                        BeanUtils.copyProperties(e, emergencyContactDO);
                        emergencyContactDO.setFiles(JSON.toJSONString(e.getFiles()));
                        emergencyContactDO.setGmtModify(new Date());
                        loanCustomerDOMapper.updateByPrimaryKeySelective(emergencyContactDO);
                    });
        }

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }

    @Override
    public ResultBean<Void> faceOff(String orderId, Long principalLenderId, Long commonLenderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");
        Preconditions.checkNotNull(principalLenderId, "主贷人ID不能为空");
        Preconditions.checkNotNull(commonLenderId, "共贷人ID不能为空");

        // 编辑原主贷人
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
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setId(orderId);
        loanProcessOrderDO.setLoanCustomerId(commonLenderId);
        loanProcessOrderDO.setGmtModify(new Date());
        loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);

        return ResultBean.ofSuccess(null, "主贷人和共贷人切换成功");
    }

    @Override
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(String orderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");

        Long loanCarInfoId = loanProcessOrderDOMapper.getLoanCarInfoIdById(orderId);

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
    public ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(String orderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");

        Long loanFinancialPlanId = loanProcessOrderDOMapper.getLoanFinancialPlanIdById(orderId);

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
    public ResultBean<LoanHomeVisitVO> homeVisitDetail(String orderId) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");

        Long loanHomeVisitId = loanProcessOrderDOMapper.getLoanHomeVisitId(orderId);

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

        List<CustomerVO.File> newFiles = infoSupplementParam.getFiles();
        newFiles.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    e.setIsSupplement((byte) 1);
                });

        String existFiles = loanCustomerDOMapper.getFilesById(infoSupplementParam.getCustomerId());
        if (StringUtils.isNotBlank(existFiles)) {
            List<CustomerVO.File> existFileList = JSON.parseArray(existFiles, CustomerVO.File.class);
            newFiles.addAll(existFileList);
            newFiles.removeAll(Collections.singleton(null));
        }

        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        loanCustomerDO.setId(infoSupplementParam.getCustomerId());
        loanCustomerDO.setFiles(JSON.toJSONString(newFiles));
        loanCustomerDO.setGmtModify(new Date());
        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        Preconditions.checkArgument(count > 0, "资料增补失败");

        return ResultBean.ofSuccess(null, "资料增补成功");
    }

    /**
     * 填充征信信息
     *
     * @param customer
     * @param loanCustomerDO
     * @param type
     */
    private void fillCreditMsg(CreditRecordVO.Customer customer, LoanCustomerDO loanCustomerDO, Byte type) {
        if (CREDIT_TYPE_BANK.equals(type)) {
            customer.setCreditStatus(loanCustomerDO.getBankCreditStatus());
            customer.setCreditDetail(loanCustomerDO.getBankCreditDetail());
        } else if (CREDIT_TYPE_SOCIAL.equals(type)) {
            customer.setCreditStatus(loanCustomerDO.getSocialCreditStatus());
            customer.setCreditDetail(loanCustomerDO.getSocialCreditDetail());
        }
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
        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.getByProcessInstId(processInstanceId);
        if (null == loanProcessOrderDO) {
            return;
        }

        // 订单基本信息
        BeanUtils.copyProperties(loanProcessOrderDO, baseInstProcessOrderVO);

        // 关联的-客户信息(主贷人)
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanProcessOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            BaseVO customer = new BaseVO();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            baseInstProcessOrderVO.setCustomer(customer);
            baseInstProcessOrderVO.setIdCard(loanCustomerDO.getIdCard());
            baseInstProcessOrderVO.setMobile(loanCustomerDO.getMobile());
        }

        // 关联的-贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanProcessOrderDO.getLoanBaseInfoId());
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
                        principalLender.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        custDetailVO.setPrincipalLender(principalLender);
                    }
                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {
                        CustomerVO commonLender = new CustomerVO();
                        BeanUtils.copyProperties(e, commonLender);
                        commonLender.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        commonLenderList.add(commonLender);
                    }
                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {
                        CustomerVO guarantor = new CustomerVO();
                        BeanUtils.copyProperties(e, guarantor);
                        guarantor.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
                        guarantorList.add(guarantor);
                    }
                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {
                        CustomerVO emergencyContact = new CustomerVO();
                        BeanUtils.copyProperties(e, emergencyContact);
                        emergencyContact.setFiles(JSON.parseArray(e.getFiles(), CustomerVO.File.class));
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
        Preconditions.checkArgument(StringUtils.isNotBlank(loanFinancialPlanParam.getOrderId()), "业务单号不能为空");

        // insert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanFinancialPlanParam, loanFinancialPlanDO);
        loanFinancialPlanDO.setGmtCreate(new Date());
        loanFinancialPlanDO.setGmtModify(new Date());
        loanFinancialPlanDO.setStatus(VALID_STATUS);

        int count = loanFinancialPlanDOMapper.insertSelective(loanFinancialPlanDO);
        Preconditions.checkArgument(count > 0, "创建贷款金融方案失败");

        // 关联
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setId(loanFinancialPlanParam.getOrderId());
        loanProcessOrderDO.setLoanFinancialPlanId(loanFinancialPlanParam.getId());
        loanProcessOrderDO.setGmtModify(new Date());

        int relaCount = loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
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
        Preconditions.checkArgument(StringUtils.isNotBlank(loanCarInfoParam.getOrderId()), "业务单号不能为空");

        // insert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        BeanUtils.copyProperties(loanCarInfoParam, loanCarInfoDO);
        loanCarInfoDO.setGmtCreate(new Date());
        loanCarInfoDO.setGmtModify(new Date());
        loanCarInfoDO.setStatus(VALID_STATUS);

        int count = loanCarInfoDOMapper.insertSelective(loanCarInfoDO);
        Preconditions.checkArgument(count > 0, "创建贷款车辆信息失败");

        // 关联
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setId(loanCarInfoParam.getOrderId());
        loanProcessOrderDO.setLoanCarInfoId(loanCarInfoParam.getId());
        loanProcessOrderDO.setGmtModify(new Date());

        int relaCount = loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
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
        Preconditions.checkArgument(StringUtils.isNotBlank(loanHomeVisitParam.getOrderId()), "业务单号不能为空");

        // insert
        LoanHomeVisitDO loanHomeVisitDO = new LoanHomeVisitDO();
        BeanUtils.copyProperties(loanHomeVisitParam, loanHomeVisitDO);
        loanHomeVisitDO.setGmtCreate(new Date());
        loanHomeVisitDO.setGmtModify(new Date());
        loanHomeVisitDO.setStatus(VALID_STATUS);

        int count = loanHomeVisitDOMapper.insertSelective(loanHomeVisitDO);
        Preconditions.checkArgument(count > 0, "创建上门家访资料失败");

        // 关联
        LoanProcessOrderDO loanProcessOrderDO = new LoanProcessOrderDO();
        loanProcessOrderDO.setId(loanHomeVisitParam.getOrderId());
        loanProcessOrderDO.setLoanHomeVisitId(loanHomeVisitParam.getId());
        loanProcessOrderDO.setGmtModify(new Date());

        int relaCount = loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
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
