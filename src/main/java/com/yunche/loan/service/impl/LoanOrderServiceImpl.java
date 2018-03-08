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
    private LoanBaseInfoService loanBaseInfoService;

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
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private LoanProcessOrderService loanProcessOrderService;

    @Autowired
    private LoanCreditInfoService loanCreditInfoService;

    @Autowired
    private LoanCarInfoService loanCarInfoService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;


    @Override
    public ResultBean<List<BaseLoanOrderVO>> query(LoanOrderQuery query) {
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
                List<BaseLoanOrderVO> baseLoanOrderVOList = tasks.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 任务状态
                            Byte taskStatus = getTaskStatus(e, query.getTaskStatus());

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstanceId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                BaseLoanOrderVO baseLoanOrderVO = new BaseLoanOrderVO();
                                baseLoanOrderVO.setTaskStatus(taskStatus);
                                fillMsg(baseLoanOrderVO, processInstanceId);
                                return baseLoanOrderVO;
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(baseLoanOrderVOList, (int) totalNum, query.getPageIndex(), query.getPageSize());
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
        creditApplyOrderVO.setOrderId(orderId);

        // 关联的-客户信息(主贷人/共贷人/担保人/紧急联系人)
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId);
        BeanUtils.copyProperties(custDetailVOResultBean.getData(), creditApplyOrderVO);

        // 关联的-贷款基本信息
        ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        creditApplyOrderVO.setLoanBaseInfo(loanBaseInfoVOResultBean.getData());

        return ResultBean.ofSuccess(creditApplyOrderVO, "查询征信申请单详情成功");
    }

    @Override
    public ResultBean<String> createCreditApplyOrder(CreditApplyOrderParam param) {
        Preconditions.checkNotNull(param, "不能为空");

        // 创建贷款基本信息
        Long baseInfoId = createLoanBaseInfo(param.getLoanBaseInfo());

        // 创建客户信息
        Long customerId = createLoanCustomer(param);

        // 创建订单
        Long orderId = createLoanOrder(baseInfoId, customerId);

        return ResultBean.ofSuccess(String.valueOf(orderId));
    }


    @Override
    public ResultBean<Void> updateCreditApplyOrder(CreditApplyOrderParam param) {
        Preconditions.checkNotNull(param.getOrderId(), "业务单号不能为空");

        // 编辑贷款基本信息
        updateLoanBaseInfo(param.getLoanBaseInfo());

        // 编辑客户信息
        updateOrInsertLoanCustomer(param);

        return ResultBean.ofSuccess(null);
    }


    private void updateLoanBaseInfo(LoanBaseInfoParam loanBaseInfoParam) {
        ResultBean<Void> updateResult = loanBaseInfoService.update(loanBaseInfoParam);
        Preconditions.checkArgument(updateResult.getSuccess(), updateResult.getMsg());
    }

    private void updateOrInsertLoanCustomer(CreditApplyOrderParam param) {
        AllCustDetailParam allCustDetailParam = new AllCustDetailParam();
        BeanUtils.copyProperties(param, allCustDetailParam);
        loanCustomerService.updateAll(allCustDetailParam);

        CustomerParam principalLender = param.getPrincipalLender();
        updateOrInsertCustomer(principalLender);
    }

    private void updateOrInsertCustomer(CustomerParam customerParam) {
        if (null == customerParam) {
            return;
        }

        if (null == customerParam.getId()) {
            // insert
            createLoanCustomer(customerParam);
        } else {
            // update
            LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
            BeanUtils.copyProperties(customerParam, loanCustomerDO);
            ResultBean<Void> resultBean = loanCustomerService.update(loanCustomerDO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

            // file
            ResultBean<Void> updateFileResultBean = loanFileService.update(customerParam.getId(), customerParam.getFiles());
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

    private Long createLoanCustomer(CreditApplyOrderParam param) {
        // 主贷人
        Long principalLenderId = createLoanCustomer(param.getPrincipalLender());

        List<CustomerParam> commonLenderList = param.getCommonLenderList();
        List<CustomerParam> guarantorList = param.getGuarantorList();
        List<CustomerParam> emergencyContactList = param.getEmergencyContactList();

        createLoanCustomerList(principalLenderId, commonLenderList);
        createLoanCustomerList(principalLenderId, guarantorList);
        createLoanCustomerList(principalLenderId, emergencyContactList);

        return principalLenderId;
    }

    /**
     * @param principalLenderId
     * @param relaCustomerList
     */
    private void createLoanCustomerList(Long principalLenderId, List<CustomerParam> relaCustomerList) {
        if (!CollectionUtils.isEmpty(relaCustomerList)) {
            relaCustomerList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {
                        e.setPrincipalCustId(principalLenderId);
                        createLoanCustomer(e);
                    });
        }
    }

    private Long createLoanCustomer(CustomerParam customerParam) {
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerParam, loanCustomerDO);

        ResultBean<Long> createCustomerResult = loanCustomerService.create(loanCustomerDO);
        Preconditions.checkArgument(createCustomerResult.getSuccess(), createCustomerResult.getMsg());

        // 文件上传
        ResultBean<Void> createFileResultBean = loanFileService.create(createCustomerResult.getData(), customerParam.getFiles());
        Preconditions.checkArgument(createFileResultBean.getSuccess(), createFileResultBean.getMsg());

        // 返回客户ID
        return createCustomerResult.getData();
    }

    private Long createLoanBaseInfo(LoanBaseInfoParam loanBaseInfoParam) {
        ResultBean<Long> resultBean = loanBaseInfoService.create(loanBaseInfoParam);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        return resultBean.getData();
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
            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.listByPrincipalCustIdAndType(loanCustomerDO.getId(), COMMON_LENDER);
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
            LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
            BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);

            ResultBean<Long> resultBean = loanCreditInfoService.create(loanCreditInfoDO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
            return ResultBean.ofSuccess(resultBean.getData(), "征信结果录入成功");
        } else {
            // update
            LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
            BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);

            ResultBean<Long> resultBean = loanCreditInfoService.update(loanCreditInfoDO);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
            return ResultBean.ofSuccess(resultBean.getData(), "征信结果修改成功");
        }
    }


    @Override
    public ResultBean<CustDetailVO> customerDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据主贷人ID获取客户详情列表
        ResultBean<CustDetailVO> resultBean = loanCustomerService.detailAll(orderId);

        return resultBean;
    }

    @Override
    public ResultBean<Void> updateCustomer(CustDetailVO custDetailVO) {
        Preconditions.checkNotNull(custDetailVO, "客户信息不能为空");

        AllCustDetailParam allCustDetailParam = new AllCustDetailParam();
        BeanUtils.copyProperties(custDetailVO, allCustDetailParam);
        loanCustomerService.updateAll(allCustDetailParam);

        return ResultBean.ofSuccess(null, "客户信息编辑成功");
    }

    @Override
    public ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId) {
        ResultBean<Void> resultBean = loanCustomerService.faceOff(orderId, principalLenderId, commonLenderId);
        return resultBean;
    }

    @Override
    public ResultBean<Long> createLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
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
    public ResultBean<Void> updateLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkArgument(null != loanCarInfoParam && null != loanCarInfoParam.getId(), "车辆信息ID不能为空");

        // convert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        convertLoanCarInfo(loanCarInfoParam, loanCarInfoDO);

        ResultBean<Void> resultBean = loanCarInfoService.update(loanCarInfoDO);
        return resultBean;
    }

    @Override
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanCarInfoVO loanCarInfoVO = new LoanCarInfoVO();

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
            LoanCarInfoVO.PartnerAccountInfo partnerAccountInfo = new LoanCarInfoVO.PartnerAccountInfo();
            BeanUtils.copyProperties(loanCarInfoDO, partnerAccountInfo);
            loanCarInfoVO.setPartnerAccountInfo(partnerAccountInfo);
        }

        return ResultBean.ofSuccess(loanCarInfoVO);
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
     * @param baseLoanOrderVO
     * @param processInstanceId
     * @return
     */
    private void fillMsg(BaseLoanOrderVO baseLoanOrderVO, String processInstanceId) {
        // 业务单
        LoanOrderDO loanOrderDO = loanOrderDOMapper.getByProcessInstId(processInstanceId);
        if (null == loanOrderDO) {
            return;
        }

        // 订单基本信息
        BeanUtils.copyProperties(loanOrderDO, baseLoanOrderVO);
        baseLoanOrderVO.setId(String.valueOf(loanOrderDO.getId()));

        // 关联的-客户信息(主贷人)
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            BaseVO customer = new BaseVO();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            baseLoanOrderVO.setCustomer(customer);
            baseLoanOrderVO.setIdCard(loanCustomerDO.getIdCard());
            baseLoanOrderVO.setMobile(loanCustomerDO.getMobile());
        }

        // 关联的-贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        if (null != loanBaseInfoDO) {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanBaseInfoDO.getSalesmanId(), null);
            if (null != employeeDO) {
                BaseVO salesman = new BaseVO();
                BeanUtils.copyProperties(employeeDO, salesman);
                baseLoanOrderVO.setSalesman(salesman);
            }
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

    private void convertLoanCarInfo(LoanCarInfoParam loanCarInfoParam, LoanCarInfoDO loanCarInfoDO) {
        BeanUtils.copyProperties(loanCarInfoParam, loanCarInfoDO);

        BaseVO carDetail = loanCarInfoParam.getCarDetail();
        if (null != carDetail) {
            loanCarInfoDO.setCarDetailId(carDetail.getId());
            loanCarInfoDO.setCarDetailName(carDetail.getName());
        }

        LoanCarInfoParam.PartnerAccountInfo partnerAccountInfo = loanCarInfoParam.getPartnerAccountInfo();
        if (null != partnerAccountInfo) {
            BeanUtils.copyProperties(partnerAccountInfo, loanCarInfoDO);
        }
    }
}
