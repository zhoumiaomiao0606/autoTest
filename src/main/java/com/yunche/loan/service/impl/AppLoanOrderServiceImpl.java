package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.AppLoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.AppLoanOrderService;
import com.yunche.loan.service.CreditService;
import com.yunche.loan.service.LoanCustomerService;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CustomerConst.*;
import static com.yunche.loan.config.constant.LoanProcessConst.*;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Service
@Transactional
public class AppLoanOrderServiceImpl implements AppLoanOrderService {


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
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;


    @Override
    public ResultBean<List<AppLoanProcessOrderVO>> query(AppLoanOrderQuery query) {
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
                List<AppLoanProcessOrderVO> baseInstProcessOrderVOList = tasks.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 任务状态
                            Byte taskStatus = getTaskStatus(e, query.getTaskStatus());

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstanceId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                AppLoanProcessOrderVO appLoanProcessOrderVO = new AppLoanProcessOrderVO();
                                appLoanProcessOrderVO.setTaskStatus(taskStatus);
                                fillMsg(appLoanProcessOrderVO, processInstanceId);
                                return appLoanProcessOrderVO;
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
    public ResultBean<AppCreditApplyOrderVO> creditApplyOrderDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        // 订单基本信息
        AppCreditApplyOrderVO creditApplyOrderVO = new AppCreditApplyOrderVO();
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
    public ResultBean<String> createCreditApplyOrder(CreditApplyOrderVO creditApplyOrderVO) {
        return null;
    }

    @Override
    public ResultBean<Void> updateCreditApplyOrder(AppCreditApplyOrderVO appCreditApplyOrderVO) {
        return null;
    }

    @Override
    public ResultBean<AppCreditRecordVO> creditRecordDetail(Long id, Byte type) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(id), "业务单号不能为空");
        Preconditions.checkNotNull(type, "征信类型不能为空");
        Preconditions.checkNotNull(CREDIT_TYPE_BANK.equals(type) || CREDIT_TYPE_SOCIAL.equals(type), "征信类型有误");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        AppCreditRecordVO creditRecordVO = new AppCreditRecordVO();

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

        return ResultBean.ofSuccess(null, "征信结果录入成功");
    }


    @Override
    public ResultBean<CustDetailVO> customerDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据orderId获取主贷人ID
        Long principalLenderId = loanOrderDOMapper.getCustIdById(orderId);

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
//        principalLenderDO.setFiles(JSON.toJSONString(principalLenderVO.getFiles()));
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
//                        commonLenderDO.setFiles(JSON.toJSONString(e.getFiles()));
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
//                        guarantorDO.setFiles(JSON.toJSONString(e.getFiles()));
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
//                        emergencyContactDO.setFiles(JSON.toJSONString(e.getFiles()));
                        emergencyContactDO.setGmtModify(new Date());
                        loanCustomerDOMapper.updateByPrimaryKeySelective(emergencyContactDO);
                    });
        }

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }

    @Override
    public ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");
        Preconditions.checkNotNull(principalLenderId, "主贷人ID不能为空");
        Preconditions.checkNotNull(commonLenderId, "共贷人ID不能为空");

        // 编辑原主贷人
        LoanCustomerDO principalLenderDO = new LoanCustomerDO();
        principalLenderDO.setId(principalLenderId);
        principalLenderDO.setCustType(CUST_TYPE_COMMON);
//        principalLenderDO.setPrincipalCustId(commonLenderId);
        principalLenderDO.setGmtModify(new Date());
        loanCustomerDOMapper.updateByPrimaryKeySelective(principalLenderDO);

        // 编辑原共贷人
        LoanCustomerDO commonLenderDO = new LoanCustomerDO();
        commonLenderDO.setId(commonLenderId);
        commonLenderDO.setCustType(CUST_TYPE_PRINCIPAL);
//        commonLenderDO.setPrincipalCustId(null);
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
    public ResultBean<AppLoanCarInfoVO> loanCarInfoDetail(Long orderId) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");

        Long loanCarInfoId = loanOrderDOMapper.getLoanCarInfoIdById(orderId);

        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanCarInfoId);
        AppLoanCarInfoVO loanCarInfoVO = new AppLoanCarInfoVO();
        BeanUtils.copyProperties(loanCarInfoDO, loanCarInfoVO);

        return ResultBean.ofSuccess(loanCarInfoVO);
    }

    @Override
    public ResultBean<Void> createOrUpdateLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam) {
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
    public ResultBean<AppLoanFinancialPlanVO> loanFinancialPlanDetail(Long orderId) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");

        Long loanFinancialPlanId = loanOrderDOMapper.getLoanFinancialPlanIdById(orderId);

        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);
        AppLoanFinancialPlanVO loanFinancialPlanVO = new AppLoanFinancialPlanVO();
        BeanUtils.copyProperties(loanFinancialPlanDO, loanFinancialPlanVO);

        return ResultBean.ofSuccess(loanFinancialPlanVO);
    }

    @Override
    public ResultBean<Void> createOrUpdateLoanFinancialPlan(AppLoanFinancialPlanParam loanFinancialPlanParam) {
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
    public ResultBean<AppLoanHomeVisitVO> homeVisitDetail(Long orderId) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单号不能为空");

        Long loanHomeVisitId = loanOrderDOMapper.getLoanHomeVisitId(orderId);

        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanHomeVisitId);
        AppLoanHomeVisitVO loanHomeVisitVO = new AppLoanHomeVisitVO();
        BeanUtils.copyProperties(loanHomeVisitDO, loanHomeVisitVO);

        return ResultBean.ofSuccess(loanHomeVisitVO);
    }

    @Override
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
        Preconditions.checkNotNull(loanFinancialPlanParam.getCarPrice(), "车辆价格不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getBizModelId(), "业务产品ID不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getSignRate(), "签约利率不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getLoanAmount(), "贷款金额不能为空");
        Preconditions.checkNotNull(loanFinancialPlanParam.getLoanTime(), "贷款期数不能为空");

        // TODO 根据公式计算


        return ResultBean.ofSuccess(null);
    }

    @Override
    public ResultBean<Void> infoSupplement(AppInfoSupplementParam infoSupplementParam) {
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
     * @param loanCustomerDO
     * @param type
     */
    private void fillCreditMsg(CreditRecordVO.Customer customer, LoanCustomerDO loanCustomerDO, Byte type) {
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
    private void fillMsg(AppLoanProcessOrderVO baseInstProcessOrderVO, String processInstanceId) {
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
    private void createLoanFinancialPlan(AppLoanFinancialPlanParam loanFinancialPlanParam) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(loanFinancialPlanParam.getOrderId()), "业务单号不能为空");

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
    private void updateLoanFinancialPlan(AppLoanFinancialPlanParam loanFinancialPlanVO) {
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
    private void createLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(loanCarInfoParam.getOrderId()), "业务单号不能为空");

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
    private void updateLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam) {
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
    private void createLoanHomeVisit(AppLoanHomeVisitParam loanHomeVisitParam) {
//        Preconditions.checkArgument(StringUtils.isNotBlank(loanHomeVisitParam.getOrderId()), "业务单号不能为空");

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
}
