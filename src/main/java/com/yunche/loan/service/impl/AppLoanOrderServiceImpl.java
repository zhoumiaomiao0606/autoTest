package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.vo.AppBusinessInfoVO;
import com.yunche.loan.domain.vo.AppCustomerInfoVO;
import com.yunche.loan.domain.vo.AppInsuranceInfoVO;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.history.HistoricVariableInstanceQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.CarConst.CAR_TYPE_MAP;
import static com.yunche.loan.config.constant.CustomerConst.*;
import static com.yunche.loan.config.constant.InsuranceTypeConst.*;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_SUPPLEMENT;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.*;


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
    private CostDetailsDOMapper costDetailsDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

    @Autowired
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private DepartmentDOMapper departmentDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

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
    private ApplyLicensePlateRecordDOMapper applyLicensePlateRecordDOMapper;

    @Autowired
    private ApplyLicensePlateDepositInfoDOMapper applyLicensePlateDepositInfoDOMapper;

    @Autowired
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Autowired
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Autowired
    private CarService carService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;


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
    public ResultBean<AppLoanFinancialPlanVO> calcLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam) {
        Preconditions.checkNotNull(appLoanFinancialPlanParam, "金融方案不能为空");

        // convert
        LoanFinancialPlanParam loanFinancialPlanParam = new LoanFinancialPlanParam();
        BeanUtils.copyProperties(appLoanFinancialPlanParam, loanFinancialPlanParam);

        // calc
        ResultBean<LoanFinancialPlanVO> calcResult = loanFinancialPlanService.calc(loanFinancialPlanParam);
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
                    ResultBean<Void> moveResultBean = loanFileService.moveOldSupplementToNormal(infoSupplementParam.getCustomerId(), e.getType());
                    Preconditions.checkArgument(moveResultBean.getSuccess(), moveResultBean.getMsg());

                    // 保存新增补的文件 ——> 增补上传
                    ResultBean<Void> saveResultBean = loanFileService.saveNewSupplementFiles(infoSupplementParam.getCustomerId(), e.getType(), e.getUrls());
                    Preconditions.checkArgument(saveResultBean.getSuccess(), saveResultBean.getMsg());
                });

        return ResultBean.ofSuccess(null, "资料增补成功");
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
                businessInfoVO.setCarTypeText(CAR_TYPE_MAP.get(loanCarInfoDO.getCarType()));
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

            ApplyLicensePlateRecordDO applyLicensePlateRecordDO = applyLicensePlateRecordDOMapper.selectByPrimaryKey(loanOrderDO.getApplyLicensePlateRecordId());
            if (null != applyLicensePlateRecordDO) {
                // 车牌号
                businessInfoVO.setLicensePlateNumber(applyLicensePlateRecordDO.getLicense_plate_number());
                // 上牌日期
                businessInfoVO.setLicensePlateDate(applyLicensePlateRecordDO.getApply_license_plate_date());
            }

            ApplyLicensePlateDepositInfoDO applyLicensePlateDepositInfoDO = applyLicensePlateDepositInfoDOMapper.selectByPrimaryKey(loanOrderDO.getApplyLicensePlateDepositInfoId());
            if (null != applyLicensePlateDepositInfoDO) {
                // 上牌抵押日期
                businessInfoVO.setLicensePlateDepositDate(applyLicensePlateDepositInfoDO.getApply_license_plate_deposit_date());
            }

            // 还款总额   -本息合计
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
            if (null != loanFinancialPlanDO) {
                businessInfoVO.setTotalRepay(loanFinancialPlanDO.getPrincipalInterestSum());
            }

            // 履约保证金
            CostDetailsDO costDetailsDO = costDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getCostDetailsId());
            if (null != costDetailsDO) {
                businessInfoVO.setPerformanceMoney(costDetailsDO.getPerformance_fee());
            }

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
    public ResultBean<List<AppInsuranceInfoVO>> insuranceInfo(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        List<AppInsuranceInfoVO> appInsuranceInfoVOList = Lists.newArrayList();

        List<InsuranceInfoDO> insuranceInfoDOS = insuranceInfoDOMapper.listByOrderId(orderId);
        if (!CollectionUtils.isEmpty(insuranceInfoDOS)) {

            appInsuranceInfoVOList = insuranceInfoDOS.parallelStream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        List<AppInsuranceInfoVO.InsuranceDetail> commercialInsuranceList = Lists.newArrayList();
                        List<AppInsuranceInfoVO.InsuranceDetail> trafficInsuranceList = Lists.newArrayList();
                        List<AppInsuranceInfoVO.InsuranceDetail> vehicleVesselTaxInsuranceList = Lists.newArrayList();

                        // 关联保险列表
                        List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.listByInsuranceInfoId(e.getId());
                        if (!CollectionUtils.isEmpty(insuranceRelevanceDOS)) {

                            insuranceRelevanceDOS.parallelStream()
                                    .filter(Objects::nonNull)
                                    .forEach(r -> {

                                        if (INSURANCE_TYPE_COMMERCIAL.equals(r.getInsurance_type())) {
                                            // 商业险
                                            AppInsuranceInfoVO.InsuranceDetail insuranceDetail = new AppInsuranceInfoVO.InsuranceDetail();
                                            convertInsuranceDetail(r, insuranceDetail);
                                            commercialInsuranceList.add(insuranceDetail);
                                        } else if (INSURANCE_TYPE_TRAFFIC.equals(r.getInsurance_type())) {
                                            // 交强险
                                            AppInsuranceInfoVO.InsuranceDetail insuranceDetail = new AppInsuranceInfoVO.InsuranceDetail();
                                            convertInsuranceDetail(r, insuranceDetail);
                                            trafficInsuranceList.add(insuranceDetail);
                                        } else if (INSURANCE_TYPE_VEHICLE_VESSEL_TAX.equals(r.getInsurance_type())) {
                                            // 车船税
                                            AppInsuranceInfoVO.InsuranceDetail insuranceDetail = new AppInsuranceInfoVO.InsuranceDetail();
                                            convertInsuranceDetail(r, insuranceDetail);
                                            vehicleVesselTaxInsuranceList.add(insuranceDetail);
                                        }

                                    });
                        }

                        AppInsuranceInfoVO appInsuranceInfoVO = new AppInsuranceInfoVO();
                        appInsuranceInfoVO.setYearNum(e.getInsurance_year());
                        appInsuranceInfoVO.setCommercialInsuranceList(commercialInsuranceList);
                        appInsuranceInfoVO.setTrafficInsuranceList(trafficInsuranceList);
                        appInsuranceInfoVO.setVehicleVesselTaxInsuranceList(vehicleVesselTaxInsuranceList);

                        return appInsuranceInfoVO;
                    })
                    .collect(Collectors.toList());
        }

        return ResultBean.ofSuccess(appInsuranceInfoVOList);
    }

    /**
     * Insurance convert
     *
     * @param insuranceRelevanceDO
     * @param insuranceDetail
     */
    private void convertInsuranceDetail(InsuranceRelevanceDO insuranceRelevanceDO, AppInsuranceInfoVO.InsuranceDetail insuranceDetail) {
        insuranceDetail.setInsuranceNumber(insuranceRelevanceDO.getInsurance_number());
        insuranceDetail.setInsuranceCompany(insuranceRelevanceDO.getInsurance_company_name());
        insuranceDetail.setInsuranceStartDate(insuranceRelevanceDO.getStart_date());
        insuranceDetail.setInsuranceEndDate(insuranceRelevanceDO.getEnd_date());
        insuranceDetail.setInsuranceAmount(insuranceRelevanceDO.getInsurance_amount());
    }

    @Override
    public ResultBean<AppOrderProcessVO> orderProcess(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        AppOrderProcessVO appOrderProcessVO = new AppOrderProcessVO();
        // 基本信息
        fillBaseMsg(appOrderProcessVO, loanOrderDO);
        // 流程信息
        fillProcessMsg(appOrderProcessVO, loanOrderDO);

        return ResultBean.ofSuccess(appOrderProcessVO);
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
     * @param appLoanFinancialPlanParam
     */
    @Override
    public ResultBean<Long> createLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam) {
        Preconditions.checkNotNull(appLoanFinancialPlanParam.getOrderId(), "业务单号不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(appLoanFinancialPlanParam, loanFinancialPlanDO);
        // insert
        ResultBean<Long> resultBean = loanFinancialPlanService.create(loanFinancialPlanDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        // 关联
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setId(appLoanFinancialPlanParam.getOrderId());
        loanOrderDO.setLoanFinancialPlanId(loanFinancialPlanDO.getId());
        ResultBean<Void> updateRelaResult = loanProcessOrderService.update(loanOrderDO);
        Preconditions.checkArgument(updateRelaResult.getSuccess(), updateRelaResult.getMsg());

        return resultBean;
    }

    /**
     * update贷款金融方案
     *
     * @param appLoanFinancialPlanParam
     */
    @Override
    public ResultBean<Void> updateLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam) {
        Preconditions.checkNotNull(appLoanFinancialPlanParam, "金融方案不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(appLoanFinancialPlanParam, loanFinancialPlanDO);

        ResultBean<Void> resultBean = loanFinancialPlanService.update(loanFinancialPlanDO);
        return resultBean;
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

    /**
     * 获取操作员用户名
     *
     * @param taskDefinitionKey
     * @param processInstanceId
     * @param executionId
     * @return
     */
    private String getAuditor(String taskDefinitionKey, String processInstanceId, String executionId) {

        String userNameTaskVariableKey = taskDefinitionKey + ":" + processInstanceId + ":"
                + executionId + ":" + PROCESS_VARIABLE_USER_NAME;

        HistoricVariableInstance userNameHistoricVariableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(userNameTaskVariableKey)
                .singleResult();

        if (null != userNameHistoricVariableInstance) {
            Object value = userNameHistoricVariableInstance.getValue();
            if (null != value) {
                String userName = (String) value;
                return userName;
            }
        }

        return null;
    }

    /**
     * 审核备注
     *
     * @param taskDefinitionKey
     * @param processInstanceId
     * @param executionId
     * @return
     */
    private String getApprovalInfo(String taskDefinitionKey, String processInstanceId, String executionId) {
        String infoTaskVariableKey = taskDefinitionKey + ":" + processInstanceId + ":"
                + executionId + ":" + PROCESS_VARIABLE_INFO;

        HistoricVariableInstance infoHistoricVariableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(infoTaskVariableKey)
                .singleResult();

        if (null != infoHistoricVariableInstance) {
            Object value = infoHistoricVariableInstance.getValue();
            if (null != value) {
                String info = (String) value;
                return info;
            }
        }

        return null;
    }

    /**
     * 基本信息
     *
     * @param appOrderProcessVO
     * @param loanOrderDO
     */
    private void fillBaseMsg(AppOrderProcessVO appOrderProcessVO, LoanOrderDO loanOrderDO) {
        appOrderProcessVO.setOrderId(String.valueOf(loanOrderDO.getId()));

        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            appOrderProcessVO.setCustomerName(loanCustomerDO.getName());
            appOrderProcessVO.setIdCard(loanCustomerDO.getIdCard());
        }

        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
        if (null != loanFinancialPlanDO) {
            appOrderProcessVO.setLoanAmount(loanFinancialPlanDO.getLoanAmount());
            appOrderProcessVO.setBank(loanFinancialPlanDO.getBank());
        }

        ResultBean<LoanBaseInfoVO> loanBaseInfoResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkArgument(loanBaseInfoResultBean.getSuccess(), loanBaseInfoResultBean.getMsg());
        LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoResultBean.getData();
        if (null != loanBaseInfoVO) {
            if (null != loanBaseInfoVO.getPartner()) {
                appOrderProcessVO.setPartnerName(loanBaseInfoVO.getPartner().getName());
            }
            if (null != loanBaseInfoVO.getSalesman()) {
                appOrderProcessVO.setSalesmanName(loanBaseInfoVO.getSalesman().getName());
            }
        }
    }

    /**
     * 流程信息
     *
     * @param appOrderProcessVO
     * @param loanOrderDO
     */
    private void fillProcessMsg(AppOrderProcessVO appOrderProcessVO, LoanOrderDO loanOrderDO) {
        // 历史task列表
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(loanOrderDO.getProcessInstId())
                .orderByTaskCreateTime()
                .desc()
                .list();

        List<AppOrderProcessVO.Task> taskList = Lists.newArrayList();

        if (!CollectionUtils.isEmpty(historicTaskInstanceList)) {

            taskList = historicTaskInstanceList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        AppOrderProcessVO.Task task = new AppOrderProcessVO.Task();
                        // 任务节点名
                        task.setTask(PROCESS_MAP.get(e.getTaskDefinitionKey()));
                        //办理时间
                        task.setApprovalTime(e.getEndTime());
                        // 任务状态
                        fillTaskStatus(task, e);
                        // 审核员
                        task.setAuditor(getAuditor(e.getTaskDefinitionKey(), loanOrderDO.getProcessInstId(), e.getExecutionId()));
                        // 审核备注
                        task.setApprovalInfo(getApprovalInfo(e.getTaskDefinitionKey(), loanOrderDO.getProcessInstId(), e.getExecutionId()));
                        // 审核员角色 OR 合伙人团队名称
                        task.setUserGroup(getUserGroup(e.getTaskDefinitionKey(), loanOrderDO.getLoanBaseInfoId()));

                        return task;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        appOrderProcessVO.setTaskList(taskList);
    }

    /**
     * 任务状态
     *
     * @param historicTaskInstance
     * @return
     */
    private String fillTaskStatus(AppOrderProcessVO.Task task, HistoricTaskInstance historicTaskInstance) {

        if (null != historicTaskInstance) {
            Date endTime = historicTaskInstance.getEndTime();
            if (null != endTime) {
                // 已处理
                task.setTaskStatus(TASK_DONE);
                task.setTaskStatusText("已处理");
            } else {
                // 未处理
                task.setTaskStatus(TASK_TODO);
                task.setTaskStatusText("未处理");
            }
        }

        return null;
    }

    /**
     * 审核员角色 OR 合伙人团队名称
     *
     * @param taskDefinitionKey
     * @param loanBaseInfoId
     * @return
     */
    private String getUserGroup(String taskDefinitionKey, Long loanBaseInfoId) {
        // 审单员角色
        String userGroup = TASK_USER_GROUP_MAP.get(taskDefinitionKey);

        if (StringUtils.isBlank(userGroup)) {

            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            if (null != loanBaseInfoDO) {
                // 合伙人名称
                PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(), null);
                if (null != partnerDO) {
                    userGroup = partnerDO.getName();
                }
            }
        }

        return userGroup;
    }
}
