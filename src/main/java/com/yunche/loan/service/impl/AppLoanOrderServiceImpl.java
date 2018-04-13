package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.LoanOrderProcessConst;
import com.yunche.loan.config.constant.LoanProcessConst;
import com.yunche.loan.config.constant.LoanProcessEnum;
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
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
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
import static com.yunche.loan.config.constant.LoanOrderProcessConst.ORDER_STATUS_DOING;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_TODO;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_LEND_RECORD;
import static com.yunche.loan.config.constant.LoanProcessEnum.FINANCIAL_SCHEME;
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
    private DepartmentDOMapper departmentDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private PartnerRelaEmployeeDOMapper partnerRelaEmployeeDOMapper;

    @Autowired
    private VehicleInformationDOMapper vehicleInformationDOMapper;

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
    private VehicleInformationService vehicleInformationService;

    @Autowired
    private ApplyLicensePlateDepositInfoDOMapper applyLicensePlateDepositInfoDOMapper;

    @Autowired
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Autowired
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Autowired
    private LoanInfoSupplementDOMapper loanInfoSupplementDOMapper;

    @Autowired
    private LoanProcessLogDOMapper loanProcessLogDOMapper;

    @Autowired
    private CarService carService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private PermissionService permissionService;


    @Override
    public ResultBean<AppInfoSupplementVO> infoSupplementDetail(Long supplementOrderId) {
        Preconditions.checkNotNull(supplementOrderId, "增补单不能为空");

        LoanInfoSupplementDO loanInfoSupplementDO = loanInfoSupplementDOMapper.selectByPrimaryKey(supplementOrderId);
        Preconditions.checkNotNull(loanInfoSupplementDO, "增补单不存在");

        AppInfoSupplementVO appInfoSupplementVO = new AppInfoSupplementVO();

        // 增补信息
        appInfoSupplementVO.setSupplementOrderId(supplementOrderId);
        appInfoSupplementVO.setSupplementType(loanInfoSupplementDO.getType());
        appInfoSupplementVO.setSupplementContent(loanInfoSupplementDO.getContent());
        appInfoSupplementVO.setSupplementInfo(loanInfoSupplementDO.getInfo());
        appInfoSupplementVO.setSupplementStartDate(loanInfoSupplementDO.getStartTime());
        appInfoSupplementVO.setSupplementEndDate(loanInfoSupplementDO.getEndTime());
        appInfoSupplementVO.setInitiator(loanInfoSupplementDO.getInitiatorName());

        Long orderId = loanInfoSupplementDO.getOrderId();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

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

        // 要求增补部门
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanInfoSupplementDO.getInitiatorId(), null);
        if (null != employeeDO) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(employeeDO.getDepartmentId(), null);
            if (null != departmentDO) {
                appInfoSupplementVO.setInitiatorUnit(departmentDO.getName());
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
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId, null);
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
        permissionService.checkTaskPermission(CREDIT_APPLY.getCode());

        // 客户信息创建
        Long customerId = createLoanCustomer(customerParam);

        // 初始化贷款业余员相关信息  -根据当前登录账户
        Long baseInfoId = initBaseInfo();

        // 业务单创建
        Long orderId = createLoanOrder(baseInfoId, customerId);

        // 返回信息：业务单ID & 客户ID
        AppCreditApplyVO appCreditApplyVO = new AppCreditApplyVO();
        appCreditApplyVO.setOrderId(orderId);
        appCreditApplyVO.setCustomerId(customerId);

        return ResultBean.ofSuccess(appCreditApplyVO);
    }

    /**
     * 初始化贷款业余员相关信息   -根据当前登录账户
     *
     * @return
     */
    private Long initBaseInfo() {
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        loanBaseInfoDO.setSalesmanId(loginUser.getId());

        Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(loginUser.getId());
        loanBaseInfoDO.setPartnerId(partnerId);

        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(partnerId, null);
        if (null != partnerDO) {
            loanBaseInfoDO.setAreaId(partnerDO.getAreaId());
        }

        ResultBean<Long> resultBean = loanBaseInfoService.create(loanBaseInfoDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        return resultBean.getData();
    }

    @Override
    public ResultBean<AppCustDetailVO> customerDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单ID不能为空");

        // 根据主贷人ID获取客户详情列表
        ResultBean<CustDetailVO> resultBean = loanCustomerService.detailAll(orderId, null);
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

        // 文件编辑
        ResultBean<Void> updateFileResult = loanFileService.updateOrInsertByCustomerIdAndUploadType(param.getId(), param.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(updateFileResult.getSuccess(), updateFileResult.getMsg());

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
        Map map = financialProductDOMapper.selectProductInfoByOrderId(orderId);
        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);
        AppLoanFinancialPlanVO loanFinancialPlanVO = new AppLoanFinancialPlanVO();
        if (null != loanFinancialPlanDO) {
            BeanUtils.copyProperties(loanFinancialPlanDO, loanFinancialPlanVO);
        }
        if (map != null) {
            loanFinancialPlanVO.setCategorySuperior((String) map.get("categorySuperior"));
            loanFinancialPlanVO.setBankRate((BigDecimal) map.get("bankRate"));
            loanFinancialPlanVO.setStagingRatio((BigDecimal) map.get("stagingRatio"));
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
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId, null);
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
            //产品信息 (产品大类+产品费率+银行分期比率)
            Map map = financialProductDOMapper.selectProductInfoByOrderId(orderId);
            if (map != null) {
                //产品大类
                businessInfoVO.setCategorySuperior((String) map.get("categorySuperior"));
                //银行分期比率
                businessInfoVO.setStagingRatio((BigDecimal) map.get("stagingRatio"));
            }
            // 业务员 & 合伙人
            LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoVOResultBean.getData();
            if (null != loanBaseInfoVO) {
                if (null != loanBaseInfoVO.getSalesman()) {
                    businessInfoVO.setSalesmanName(loanBaseInfoVO.getSalesman().getName());
                }
                if (null != loanBaseInfoVO.getPartner()) {
                    businessInfoVO.setPartnerName(loanBaseInfoVO.getPartner().getName());
                }
                if (null != loanBaseInfoVO.getBank()) {
                    businessInfoVO.setBank(loanBaseInfoVO.getBank());
                }
                if (null != loanBaseInfoVO.getCarType()) {
                    businessInfoVO.setCarType(loanBaseInfoVO.getCarType());
                }
                if (null != loanBaseInfoVO.getDepartmentName()) {
                    businessInfoVO.setDepartmentName(loanBaseInfoVO.getDepartmentName());
                }

            }

            // 车型
            LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
            if (null != loanCarInfoDO) {
                // 车名
                ResultBean<String> carFullNameResultBean = carService.getFullName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL);
                Preconditions.checkArgument(carFullNameResultBean.getSuccess(), carFullNameResultBean.getMsg());
                //车型名称
                businessInfoVO.setCarName(carFullNameResultBean.getData());

                // 车辆类型
                businessInfoVO.setCarType(loanCarInfoDO.getCarType());
                //车辆类型描述
                businessInfoVO.setCarTypeText(CAR_TYPE_MAP.get(loanCarInfoDO.getCarType()));
                // GPS数量
                businessInfoVO.setGpsNum(loanCarInfoDO.getGpsNum());
                //车辆属性
                businessInfoVO.setVehicleProperty(loanCarInfoDO.getVehicleProperty());
                //留备用钥匙
                businessInfoVO.setCarKey(loanCarInfoDO.getCarKey());
                //业务来源
                businessInfoVO.setBusinessSource(loanCarInfoDO.getBusinessSource());
                //二手车初登日期
                businessInfoVO.setFirstRegisterDate(loanCarInfoDO.getFirstRegisterDate());
                //备注
                businessInfoVO.setInfo(loanCarInfoDO.getInfo());
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
                // 按揭期限（贷款期限）
                businessInfoVO.setLoanTime(loanFinancialPlanDO.getLoanTime());
                //准评估价
                businessInfoVO.setAppraisal(loanFinancialPlanDO.getAppraisal());
                //执行利率
                businessInfoVO.setSignRate(loanFinancialPlanDO.getSignRate());
                //首付额
                businessInfoVO.setDownPaymentMoney(loanFinancialPlanDO.getDownPaymentMoney());
                //首付比例
                businessInfoVO.setDownPaymentRatio(loanFinancialPlanDO.getDownPaymentRatio());
                //银行分期本金
                businessInfoVO.setBankPeriodPrincipal(loanFinancialPlanDO.getBankPeriodPrincipal());
                //首月还款
                businessInfoVO.setFirstMonthRepay(loanFinancialPlanDO.getFirstMonthRepay());
                //月还款
                businessInfoVO.setEachMonthRepay(loanFinancialPlanDO.getEachMonthRepay());
                //还款总额
                businessInfoVO.setTotalRepayment(loanFinancialPlanDO.getPrincipalInterestSum());
                //贷款利息
                businessInfoVO.setLoanInterest(loanFinancialPlanDO.getBankFee());
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

                Long productId = loanFinancialPlanDO.getFinancialProductId();
                FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(productId);
                //贷款产品
                businessInfoVO.setProdName(financialProductDO.getProdName());
                // 履约保证金
                CostDetailsDO costDetailsDO = costDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getCostDetailsId());
                if (null != costDetailsDO) {
                    businessInfoVO.setPerformanceMoney(costDetailsDO.getPerformance_fee());
                }
                //
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

            Long vid = loanOrderDOMapper.getVehicleInformationIdById(orderId);
            VehicleInformationDO vehicleInformationDO = vehicleInformationDOMapper.selectByPrimaryKey(vid);
            if (vehicleInformationDO != null) {
                //行驶证车主
                businessInfoVO.setNowDrivingLicenseOwner(vehicleInformationDO.getNow_driving_license_owner());
                //上牌方式
                businessInfoVO.setLicensePlateType(vehicleInformationDO.getLicense_plate_type());
                //上牌地点
                businessInfoVO.setApplyLicensePlateArea(vehicleInformationDO.getApply_license_plate_area());
                //车辆颜色
                businessInfoVO.setColor(vehicleInformationDO.getColor());
                //上牌日期
                businessInfoVO.setApplyLicensePlateDate(vehicleInformationDO.getApply_license_plate_date());

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

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        loanBaseInfoDO.setSalesmanId(loginUser.getId());

        Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(loginUser.getId());
        loanBaseInfoDO.setPartnerId(partnerId);

        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(partnerId, null);
        if (null != partnerDO) {
            loanBaseInfoDO.setAreaId(partnerDO.getAreaId());
        }

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

        // 文件保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(resultBean.getData(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

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

        VehicleInformationUpdateParam vehicleInformationUpdateParam = new VehicleInformationUpdateParam();
        vehicleInformationUpdateParam.setOrder_id(loanCarInfoParam.getOrderId().toString());
        vehicleInformationUpdateParam.setApply_license_plate_area(loanCarInfoParam.getApplyLicensePlateArea());
        vehicleInformationUpdateParam.setLicense_plate_type(loanCarInfoParam.getLicensePlateType());
        vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
        vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());

        vehicleInformationService.update(vehicleInformationUpdateParam);

        return ResultBean.ofSuccess(createResultBean.getData(), "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkArgument(null != loanCarInfoParam && null != loanCarInfoParam.getId(), "车辆信息ID不能为空");
        Preconditions.checkNotNull(loanCarInfoParam.getOrderId(), "订单号不能为空");

        // convert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        convertLoanCarInfo(loanCarInfoParam, loanCarInfoDO);

        ResultBean<Void> resultBean = loanCarInfoService.update(loanCarInfoDO);

        VehicleInformationUpdateParam vehicleInformationUpdateParam = new VehicleInformationUpdateParam();
        vehicleInformationUpdateParam.setOrder_id(loanCarInfoParam.getOrderId().toString());
        vehicleInformationUpdateParam.setApply_license_plate_area(loanCarInfoParam.getApplyLicensePlateArea());
        vehicleInformationUpdateParam.setLicense_plate_type(loanCarInfoParam.getLicensePlateType());
        vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
        vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
        vehicleInformationService.update(vehicleInformationUpdateParam);

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

        Long vid = loanOrderDOMapper.getVehicleInformationIdById(orderId);
        VehicleInformationDO vehicleInformationDO = vehicleInformationDOMapper.selectByPrimaryKey(vid);
        if (vehicleInformationDO != null) {
            loanCarInfoVO.setApplyLicensePlateArea(vehicleInformationDO.getApply_license_plate_area());
            loanCarInfoVO.setNowDrivingLicenseOwner(vehicleInformationDO.getNow_driving_license_owner());
            loanCarInfoVO.setLicensePlateType(vehicleInformationDO.getLicense_plate_type() == null ? null : vehicleInformationDO.getLicense_plate_type().toString());
            loanCarInfoVO.setColor(vehicleInformationDO.getColor());
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
     * 创建订单
     *
     * @param baseInfoId
     * @param customerId
     * @return
     */
    private Long createLoanOrder(Long baseInfoId, Long customerId) {
        ResultBean<Long> createLoanOrderResult = loanProcessOrderService.createLoanOrder(baseInfoId, customerId);
        Preconditions.checkArgument(createLoanOrderResult.getSuccess(), createLoanOrderResult.getMsg());
        return createLoanOrderResult.getData();
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

        // 文件KEY列表保存
        ResultBean<Void> insertFileResultBean = loanFileService.batchInsert(createCustomerResult.getData(), customerParam.getFiles());
        Preconditions.checkArgument(insertFileResultBean.getSuccess(), insertFileResultBean.getMsg());

        // 返回客户ID
        return createCustomerResult.getData();
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
                // 合伙人
                appOrderProcessVO.setPartnerName(loanBaseInfoVO.getPartner().getName());

                // 合伙人所属云车管辖部门
                PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoVO.getId(), null);
                if (null != partnerDO) {
                    DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(partnerDO.getDepartmentId(), null);
                    if (null != departmentDO) {
                        appOrderProcessVO.setDepartment(departmentDO.getName());
                    }
                }
            }
            if (null != loanBaseInfoVO.getSalesman()) {
                // 业务员
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
                        task.setTask(LoanProcessEnum.getNameByCode(e.getTaskDefinitionKey()));
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

        // TODO 当前任务KEY
//        ResultBean<TaskStateVO> taskStateVOResultBean = loanProcessService.currentTask(loanOrderDO.getId());
//        Preconditions.checkArgument(taskStateVOResultBean.getSuccess(), taskStateVOResultBean.getMsg());
//        TaskStateVO taskStateVO = taskStateVOResultBean.getData();
//        if (null != taskStateVO) {
//            appOrderProcessVO.setTaskDefinitionKey(taskStateVO.getTaskDefinitionKey());
//        }

        // 是否可以弃单 -> 【打款确认】为界限
        if (REMIT_REVIEW.getCode().equals(appOrderProcessVO.getTaskDefinitionKey())) {
            appOrderProcessVO.setCanCancelTask(false);
        } else {
            appOrderProcessVO.setCanCancelTask(true);
        }

        appOrderProcessVO.setTaskList(taskList);
    }

    /**
     * 流程信息
     *
     * @param appOrderProcessVO
     * @param loanOrderDO
     */
    private void fillProcessMsg_(AppOrderProcessVO appOrderProcessVO, LoanOrderDO loanOrderDO) {

        // 是否可以弃单
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(loanOrderDO.getId());
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");
        if (ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus())) {
            appOrderProcessVO.setCanCancelTask(true);
        } else {
            appOrderProcessVO.setCanCancelTask(false);
        }

        List<LoanProcessLogDO> loanProcessLogDOList = loanProcessLogDOMapper.listByOrderId(loanOrderDO.getId(), null);

        if (!CollectionUtils.isEmpty(loanProcessLogDOList)) {

            List<AppOrderProcessVO.Task> taskList = Lists.newArrayList();

            loanProcessLogDOList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        AppOrderProcessVO.Task task = new AppOrderProcessVO.Task();
                        // 任务节点名
                        task.setTask(LoanProcessEnum.getNameByCode(e.getTaskDefinitionKey()));
                        //办理时间
                        task.setApprovalTime(e.getCreateTime());

                        // TODO 任务状态
//                        xxx(task);



//                        fillTaskStatus(task, e);
                        // 审核员
                        task.setAuditor(e.getUserName());
                        // 审核备注
                        task.setApprovalInfo(e.getInfo());
                        // TODO 审核员角色 OR 合伙人团队名称
                        task.setUserGroup(getUserGroup(e.getTaskDefinitionKey(), loanOrderDO.getLoanBaseInfoId()));

                        taskList.add(task);
                    });

            appOrderProcessVO.setTaskList(taskList);
        } else {
            appOrderProcessVO.setTaskList(Collections.EMPTY_LIST);
        }


//        if (!CollectionUtils.isEmpty(historicTaskInstanceList)) {
//
//            taskList = historicTaskInstanceList.stream()
//                    .filter(Objects::nonNull)
//                    .map(e -> {
//
//                        AppOrderProcessVO.Task task = new AppOrderProcessVO.Task();
//                        // 任务节点名
//                        task.setTask(LoanProcessEnum.getNameByCode(e.getTaskDefinitionKey()));
//                        //办理时间
//                        task.setApprovalTime(e.getEndTime());
//                        // 任务状态
//                        fillTaskStatus(task, e);
//                        // 审核员
//                        task.setAuditor(getAuditor(e.getTaskDefinitionKey(), loanOrderDO.getProcessInstId(), e.getExecutionId()));
//                        // 审核备注
//                        task.setApprovalInfo(getApprovalInfo(e.getTaskDefinitionKey(), loanOrderDO.getProcessInstId(), e.getExecutionId()));
//                        // 审核员角色 OR 合伙人团队名称
//                        task.setUserGroup(getUserGroup(e.getTaskDefinitionKey(), loanOrderDO.getLoanBaseInfoId()));
//
//                        return task;
//                    })
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.toList());
//        }
//
//
//        appOrderProcessVO.setTaskList(taskList);
    }

//    private void xxx(AppOrderProcessVO.Task task) {
//
//
//        if (CREDIT_APPLY.getCode().equals(taskDefinitionKey)) {
//            task.setTaskStatus(taskProcessStatus);
//        } else if (BANK_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setBankCreditRecord(taskProcessStatus);
//        } else if (SOCIAL_CREDIT_RECORD.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setSocialCreditRecord(taskProcessStatus);
//        } else if (LOAN_APPLY.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setLoanApply(taskProcessStatus);
//        } else if (VISIT_VERIFY.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setVisitVerify(taskProcessStatus);
//        } else if (TELEPHONE_VERIFY.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setTelephoneVerify(taskProcessStatus);
//        } else if (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setBusinessReview(taskProcessStatus);
//        } else if (LOAN_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setLoanReview(taskProcessStatus);
//        } else if (REMIT_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setRemitReview(taskProcessStatus);
//        } else if (CAR_INSURANCE.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setCarInsurance(taskProcessStatus);
//        } else if (APPLY_LICENSE_PLATE_DEPOSIT_INFO.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setApplyLicensePlateDepositInfo(taskProcessStatus);
//        } else if (INSTALL_GPS.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setInstallGps(taskProcessStatus);
//        } else if (COMMIT_KEY.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setCommitKey(taskProcessStatus);
//        } else if (VEHICLE_INFORMATION.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setVehicleInformation(taskProcessStatus);
//        } else if (BUSINESS_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setBusinessReview(taskProcessStatus);
//        } else if (LOAN_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setLoanReview(taskProcessStatus);
//        } else if (REMIT_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setRemitReview(taskProcessStatus);
//        } else if (MATERIAL_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setMaterialReview(taskProcessStatus);
//        } else if (MATERIAL_PRINT_REVIEW.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setMaterialPrintReview(taskProcessStatus);
//        } else if (BANK_CARD_RECORD.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setBankCardRecord(taskProcessStatus);
//        } else if (FINANCIAL_SCHEME.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setFinancialScheme(taskProcessStatus);
//        } else if (BANK_LEND_RECORD.getCode().equals(taskDefinitionKey)) {
//            loanProcessDO.setBankLendRecord(taskProcessStatus);
//        }
//    }

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
