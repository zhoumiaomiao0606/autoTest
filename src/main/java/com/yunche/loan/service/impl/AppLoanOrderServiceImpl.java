package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.EmployeeCache;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.config.util.ZhongAnHttpUtil;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.ZhongAnDetailQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CarConst.CAR_DETAIL;
import static com.yunche.loan.config.constant.CarConst.CAR_TYPE_MAP;
import static com.yunche.loan.config.constant.InsuranceTypeConst.*;
import static com.yunche.loan.config.constant.LoanCustomerConst.*;
import static com.yunche.loan.config.constant.LoanCustomerEnum.COMMON_LENDER;
import static com.yunche.loan.config.constant.LoanCustomerEnum.GUARANTOR;
import static com.yunche.loan.config.constant.LoanCustomerEnum.PRINCIPAL_LENDER;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.ORDER_STATUS_DOING;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_DONE;
import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;
import static com.yunche.loan.config.constant.LoanProcessEnum.TELEPHONE_VERIFY;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_INFO;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_USER_NAME;
import static com.yunche.loan.config.constant.ProcessApprovalConst.TASK_USER_GROUP_MAP;
import static com.yunche.loan.service.impl.LoanProcessServiceImpl.convertActionText;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Service
public class AppLoanOrderServiceImpl implements AppLoanOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AppLoanOrderServiceImpl.class);

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
    private DictService dictService;

    @Autowired
    private ApplyLicensePlateDepositInfoDOMapper applyLicensePlateDepositInfoDOMapper;

    @Autowired
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Autowired
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Autowired
    private LoanProcessLogDOMapper loanProcessLogDOMapper;

    @Autowired
    private CarService carService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private FinancialProductDOMapper financialProductDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

    @Autowired
    private EmployeeCache employeeCache;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LoanOrderService loanOrderService;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanInfoSupplementService loanInfoSupplementService;

    @Autowired
    private ZhonganInfoDOMapper zhongAnInfoDOMapper;

    @Autowired
    private RspCreditDOMapper rspCreditDOMapper;

    @Autowired
    private RspLawsuitDOMapper rspLawSuitDOMapper;

    @Autowired
    private ZhonganOverdueDOMapper zhongAnOverDueDOMapper;

    @Autowired
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

    @Autowired
    private ConfLoanApplyDOMapper confLoanApplyDOMapper;

    @Autowired
    private LoanProcessLogService loanProcessLogService;

    @Autowired
    private SecondHandCarEvaluateDOMapper secondHandCarEvaluateDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;



    @Autowired
    private ZhonganZhixingDOMapper zhonganZhixingDOMapper;

    @Autowired
    private ZhonganCaipanDOMapper zhonganCaipanDOMapper;

    @Autowired
    private ZhonganFeizhengDOMapper zhonganFeizhengDOMapper;

    @Autowired
    private ZhonganQiankuanDOMapper zhonganQiankuanDOMapper;

    @Autowired
    private ZhonganQianshuiDOMapper zhonganQianshuiDOMapper;

    @Autowired
    private ZhonganShenpanDOMapper zhonganShenpanDOMapper;

    @Autowired
    private ZhonganWeifaDOMapper zhonganWeifaDOMapper;

    @Autowired
    private ZhonganShixinDOMapper zhonganShixinDOMapper;

    @Autowired
    private ZhonganZuifanDOMapper zhonganZuifanDOMapper;

    @Autowired
    private ZhonganXiangaoDOMapper zhonganXiangaoDOMapper;

    @Autowired
    private ZhonganXianchuDOMapper zhonganXianchuDOMapper;


    @Override
    public ResultBean<AppInfoSupplementVO> infoSupplementDetail(Long supplementOrderId) {

        UniversalInfoSupplementVO data = loanQueryService.selectUniversalInfoSupplementDetail(supplementOrderId);

        AppInfoSupplementVO appInfoSupplementVO = new AppInfoSupplementVO();
        BeanUtils.copyProperties(data, appInfoSupplementVO);

        appInfoSupplementVO.setBank(data.getBankName());
        appInfoSupplementVO.setSupplementType(data.getType());
        appInfoSupplementVO.setSupplementTypeText(data.getTypeText());
        appInfoSupplementVO.setSupplementContent(data.getContent());
        appInfoSupplementVO.setSupplementInfo(data.getInfo());
        appInfoSupplementVO.setInitiator(data.getInitiatorName());
        appInfoSupplementVO.setSupplementStartDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd_HHmmss(data.getStartTime()));
        appInfoSupplementVO.setSupplementEndDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd_HHmmss(data.getEndTime()));

        //  车辆信息
        if (null != data.getCarDetailId()) {
            String carFullName = carService.getFullName(data.getCarDetailId(), CAR_DETAIL);
            appInfoSupplementVO.setCarName(carFullName);
        }

        // 要求增补部门
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(data.getInitiatorId(), null);
        if (null != employeeDO) {
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(employeeDO.getDepartmentId(), null);
            if (null != departmentDO) {
                appInfoSupplementVO.setInitiatorUnit(departmentDO.getName());
            }
        }

        return ResultBean.ofSuccess(appInfoSupplementVO);


//        Preconditions.checkNotNull(supplementOrderId, "增补单不能为空");
//
//        LoanInfoSupplementDO loanInfoSupplementDO = loanInfoSupplementDOMapper.selectByPrimaryKey(supplementOrderId);
//        Preconditions.checkNotNull(loanInfoSupplementDO, "增补单不存在");
//
//        AppInfoSupplementVO appInfoSupplementVO = new AppInfoSupplementVO();
//
//        // 增补信息
//        appInfoSupplementVO.setSupplementOrderId(supplementOrderId);
//        appInfoSupplementVO.setSupplementType(loanInfoSupplementDO.getType());
//        appInfoSupplementVO.setSupplementTypeText(getSupplementTypeText(loanInfoSupplementDO.getType()));
//        appInfoSupplementVO.setSupplementContent(loanInfoSupplementDO.getContent());
//        appInfoSupplementVO.setSupplementInfo(loanInfoSupplementDO.getInfo());
//        appInfoSupplementVO.setSupplementStartDate(loanInfoSupplementDO.getStartTime());
//        appInfoSupplementVO.setSupplementEndDate(loanInfoSupplementDO.getEndTime());
//        appInfoSupplementVO.setInitiator(loanInfoSupplementDO.getInitiatorName());
//        appInfoSupplementVO.setRemark(loanInfoSupplementDO.getRemark());
//
//        Long orderId = loanInfoSupplementDO.getOrderId();
//        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
//        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");
//
//        appInfoSupplementVO.setOrderId(String.valueOf(orderId));
//
//        // 客户信息
//        if (null != loanOrderDO.getLoanCustomerId()) {
//            CustomerVO customerVO = loanCustomerService.getById(loanOrderDO.getLoanCustomerId());
//
//            if (null != customerVO) {
//                appInfoSupplementVO.setCustomerId(customerVO.getId());
//                appInfoSupplementVO.setCustomerName(customerVO.getName());
//                appInfoSupplementVO.setIdCard(customerVO.getIdCard());
//            }
//        }
//
//        // 贷款基本信息：贷款额、期限 & 银行
//        if (null != loanOrderDO.getLoanBaseInfoId()) {
//            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
//            if (null != loanFinancialPlanDO) {
//                appInfoSupplementVO.setLoanAmount(String.valueOf(loanFinancialPlanDO.getLoanAmount()));
//                appInfoSupplementVO.setLoanTime(loanFinancialPlanDO.getLoanTime());
//                appInfoSupplementVO.setBank(loanFinancialPlanDO.getBank());
//            }
//        }
//
//        //  车辆信息
//        if (null != loanOrderDO.getLoanCarInfoId()) {
//            LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
//            if (null != loanCarInfoDO && null != loanCarInfoDO.getCarDetailId()) {
//                String carFullName = carService.getFullName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL);
//                appInfoSupplementVO.setCarName(carFullName);
//            }
//        }
//
//        // 要求增补部门
//        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanInfoSupplementDO.getInitiatorId(), null);
//        if (null != employeeDO) {
//            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(employeeDO.getDepartmentId(), null);
//            if (null != departmentDO) {
//                appInfoSupplementVO.setInitiatorUnit(departmentDO.getName());
//            }
//        }
//
//        // 文件分类列表
//        ResultBean<List<FileVO>> fileVOResultBean = loanFileService.listByCustomerIdAndUploadType(loanOrderDO.getLoanCustomerId(), UPLOAD_TYPE_SUPPLEMENT);
//        Preconditions.checkArgument(fileVOResultBean.getSuccess(), fileVOResultBean.getMsg());
//        appInfoSupplementVO.setFiles(fileVOResultBean.getData());
//
//        return ResultBean.ofSuccess(appInfoSupplementVO);
    }

    @Override
    public ResultBean<AppCreditApplyOrderVO> creditApplyOrderDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        // 订单基本信息
        AppCreditApplyOrderVO creditApplyOrderVO = new AppCreditApplyOrderVO();
        BeanUtils.copyProperties(loanOrderDO, creditApplyOrderVO);
        creditApplyOrderVO.setOrderId(String.valueOf(loanOrderDO.getId()));

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
    public ResultBean<AppCreditApplyVO> createCreditApplyOrder(CustomerParam customerParam) {
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
    public ResultBean<CustDetailVO> customerDetail(Long orderId) {
        return loanCustomerService.detailAll(orderId, null);
    }

    @Override
    @Transactional
    public ResultBean<Void> updateCustomer(CustomerParam param) {
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
            if(loanFinancialPlanDO !=null) {
                if (loanFinancialPlanDO.getBankPeriodPrincipal() != null && loanFinancialPlanDO.getLoanAmount() != null) {
                    loanFinancialPlanVO.setFinancialServiceFee(String.valueOf(loanFinancialPlanDO.getBankPeriodPrincipal().subtract(loanFinancialPlanDO.getLoanAmount())));
                }
                if (loanFinancialPlanDO.getLoanAmount() != null && loanFinancialPlanDO.getCarPrice() != null) {
                    loanFinancialPlanVO.setLoanRate(String.valueOf(loanFinancialPlanDO.getLoanAmount().multiply(new BigDecimal("100")).divide(loanFinancialPlanDO.getCarPrice(),2,BigDecimal.ROUND_HALF_UP)));
                }
            }
        }

        return ResultBean.ofSuccess(loanFinancialPlanVO);
    }

    @Override
    public ResultBean<AppLoanHomeVisitVO> homeVisitDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        AppLoanHomeVisitVO appLoanHomeVisitVO = new AppLoanHomeVisitVO();

        // 主贷客户信息
        ResultBean<LoanSimpleInfoVO> simpleInfoVOResultBean = loanOrderService.simpleInfo(orderId);
        Preconditions.checkArgument(simpleInfoVOResultBean.getSuccess(), simpleInfoVOResultBean.getMsg());
        LoanSimpleInfoVO loanSimpleInfoVO = simpleInfoVOResultBean.getData();
        if (null != loanSimpleInfoVO) {
            BeanUtils.copyProperties(loanSimpleInfoVO, appLoanHomeVisitVO);
        }

        // 家访信息
        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanOrderDO.getLoanHomeVisitId());
        if (null != loanHomeVisitDO) {
            BeanUtils.copyProperties(loanHomeVisitDO, appLoanHomeVisitVO);

            // name
            BaseVO baseVO = employeeCache.getById((loanHomeVisitDO.getVisitSalesmanId()));
            if (null != baseVO) {
                appLoanHomeVisitVO.setVisitSalesmanName(baseVO.getName());
            }
        }

        // file
        List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(loanSimpleInfoVO.getCustomerId()));
        if (!CollectionUtils.isEmpty(files)) {

            List<FileVO> homeVisitFiles = Lists.newArrayList();

            // 12-合影照片;13-家访视频; 16-家访照片; 17-车辆照片;18-其他资料;
            files.stream()
//                    .filter(e -> "12".equals(e.getType()) || "13".equals(e.getType())
//                            || "16".equals(e.getType()) || "17".equals(e.getType())
//                            || "18".equals(e.getType()))
                    // 非空
                    .filter(e -> !CollectionUtils.isEmpty(e.getUrls()))
                    .forEach(e -> {

                        FileVO fileVO = new FileVO();
                        fileVO.setType(Byte.valueOf(e.getType()));
                        fileVO.setName(e.getName());
                        fileVO.setUrls(e.getUrls());

                        homeVisitFiles.add(fileVO);
                    });

            appLoanHomeVisitVO.setFiles(homeVisitFiles);
        }

        return ResultBean.ofSuccess(appLoanHomeVisitVO);
    }

    @Override
    @Transactional
    public ResultBean<Void> createOrUpdateLoanHomeVisit(AppLoanHomeVisitParam loanHomeVisitParam) {
        ResultBean<Long> resultBean = loanOrderService.createOrUpdateLoanHomeVisit(loanHomeVisitParam);
        return ResultBean.of(resultBean.getData(), resultBean.getSuccess(), resultBean.getCode(), resultBean.getMsg());
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
            loanFinancialPlanVO.setLoanRate(String.valueOf(loanFinancialPlanParam.getLoanAmount().multiply(new BigDecimal("100")).divide(loanFinancialPlanParam.getCarPrice(),2,BigDecimal.ROUND_HALF_UP)));
            BeanUtils.copyProperties(loanFinancialPlanVO, appLoanFinancialPlanVO);
        }

        return ResultBean.ofSuccess(appLoanFinancialPlanVO);
    }

    @Override
    @Transactional
    public ResultBean<Void> infoSupplementUpload(InfoSupplementParam infoSupplementParam) {

        return loanInfoSupplementService.save(infoSupplementParam);
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
                            //fillCustomerInfo(e, (AppCustomerInfoVO.CustomerInfo) emergencyContact);
                            if (e != null) {
                                BeanUtils.copyProperties(e, emergencyContact);
                            }
                            emergencyContactList.add(emergencyContact);
                        });

                customerInfoVO.setEmergencyContactList(emergencyContactList);

            }
            // 特殊联系人
            List<CustomerVO> specialContactVOList = custDetailVO.getSpecialContactList();
            if (!CollectionUtils.isEmpty(specialContactVOList)) {

                List<AppCustomerInfoVO.CustomerInfo> specialContactList = Lists.newArrayList();

                specialContactVOList.parallelStream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            AppCustomerInfoVO.CustomerInfo customerInfo = new AppCustomerInfoVO.CustomerInfo();
                            //fillCustomerInfo(e, (AppCustomerInfoVO.CustomerInfo) emergencyContact);
                            if (e != null) {
                                BeanUtils.copyProperties(e, customerInfo);
                            }
                            specialContactList.add(customerInfo);
                        });

                customerInfoVO.setSpecialList(specialContactList);
            }
        }
        VideoFaceLogDO videoFaceLogDO = videoFaceLogDOMapper.lastVideoFaceLogByOrderId(orderId);
        if (videoFaceLogDO == null) {
            customerInfoVO.setVideoFaceFlag("0");
        } else {
            customerInfoVO.setVideoFaceFlag("1");
        }
        return ResultBean.ofSuccess(customerInfoVO);
    }

    @Override
    public ResultBean<AppBusinessInfoVO> businessInfo(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        AppBusinessInfoVO businessInfoVO = new AppBusinessInfoVO();

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
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
                String carFullName = carService.getFullName(loanCarInfoDO.getCarDetailId(), CAR_DETAIL);
                //车型名称
                businessInfoVO.setCarName(carFullName);

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

                //待收钥匙
                if (loanCarInfoDO.getCarKey() == 0 )
                {
                    businessInfoVO.setNeedCollectKey("不收");
                }
                else if (loanCarInfoDO.getCarKey() == 1)
                {
                    //查询是否已收钥匙
                    LoanProcessLogDO loanProcessLogDO = loanCarInfoDOMapper.selectNeedCollectKey(orderId);
                    if (loanProcessLogDO != null && loanProcessLogDO.getAction() == 1 )
                    {
                        businessInfoVO.setNeedCollectKey("已收");
                    }else
                    {
                        businessInfoVO.setNeedCollectKey("待收");
                    }


                }

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
                LoanCreditInfoVO bankLoanCreditInfoVO = loanCreditInfoService.getByCustomerId(loanOrderDO.getLoanCustomerId(), CREDIT_TYPE_BANK);
                if (null != bankLoanCreditInfoVO) {
                    businessInfoVO.setBankCreditResult(bankLoanCreditInfoVO.getResult());
                    businessInfoVO.setBankCreditInfo(bankLoanCreditInfoVO.getInfo());
                }

                // 社会征信
                LoanCreditInfoVO socialLoanCreditInfoVO = loanCreditInfoService.getByCustomerId(loanOrderDO.getLoanCustomerId(), CREDIT_TYPE_SOCIAL);
                if (null != socialLoanCreditInfoVO) {
                    businessInfoVO.setSocialCreditResult(socialLoanCreditInfoVO.getResult());
                    businessInfoVO.setSocialCreditInfo(socialLoanCreditInfoVO.getInfo());
                }

                //加入还款卡号
                LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), VALID_STATUS);
                if (loanCustomerDO!=null)
                {
                    businessInfoVO.setRepayCardId(loanCustomerDO.getLendCard());
                }
            }

            Long vid = loanOrderDOMapper.getVehicleInformationIdById(orderId);
            VehicleInformationDO vehicleInformationDO = vehicleInformationDOMapper.selectByPrimaryKey(vid);

            String tmpApplyLicensePlateArea = null;

            if (vehicleInformationDO != null) {
                if (vehicleInformationDO.getApply_license_plate_area() != null) {
                    BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.valueOf(vehicleInformationDO.getApply_license_plate_area()), VALID_STATUS);
                    //（个性化）如果上牌地是区县一级，则返回形式为 省+区
                    if ("3".equals(String.valueOf(baseAreaDO.getLevel()))) {
                        Long parentAreaId = baseAreaDO.getParentAreaId();
                        BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                        baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                        baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
                    }
                    if (baseAreaDO != null) {
                        if (baseAreaDO.getParentAreaName() != null) {
                            tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                        } else {
                            tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                        }
                    }
                }
                //行驶证车主
                businessInfoVO.setNowDrivingLicenseOwner(vehicleInformationDO.getNow_driving_license_owner());
                //上牌方式
                businessInfoVO.setLicensePlateType(vehicleInformationDO.getLicense_plate_type());
                //上牌地点
                businessInfoVO.setApplyLicensePlateArea(tmpApplyLicensePlateArea);
                //车辆颜色
                businessInfoVO.setColor(vehicleInformationDO.getColor());
                //上牌日期
                businessInfoVO.setApplyLicensePlateDate(vehicleInformationDO.getApply_license_plate_date());
                businessInfoVO.setLicensePlateDate(vehicleInformationDO.getApply_license_plate_date());

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

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
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
    public ZhonganReturnVO zhongAnQuery(ZhongAnQueryParam zhongAnQueryParam) {

        ZhonganReturnVO zhonganReturnVO = new ZhonganReturnVO();
        zhonganReturnVO.setFlag(true);
        String returnInfo = "";
        List<ZhongAnCusParam> customers = zhongAnQueryParam.getCustomers();
        if (customers.size() == 0) {
            throw new BizException("查询数据不能为空");
        }

        try {
            for (ZhongAnCusParam zhongAnCusParam : customers) {

                Random random = new Random();

                Map map = ZhongAnHttpUtil.queryInfo(zhongAnCusParam.getName(), zhongAnCusParam.getTel(), zhongAnCusParam.getIdcard(),
                        zhongAnQueryParam.getOrder_id(), zhongAnCusParam.getLoanmoney(), zhongAnCusParam.getCustomertype(), zhongAnCusParam.getRalationship(),
                        System.currentTimeMillis() + "" + random.nextInt(10000));

                if ((boolean) map.get("success")) {
                    List<ZhonganCaipanDO> zhonganCaipanDOList = new ArrayList<>();
                    List<ZhonganFeizhengDO> zhonganFeizhengDOList = new ArrayList<>();
                    List<ZhonganQiankuanDO> zhonganQiankuanDOList = new ArrayList<>();
                    List<ZhonganQianshuiDO> zhonganQianshuiDOList = new ArrayList<>();
                    List<ZhonganShenpanDO> zhonganShenpanDOList = new ArrayList<>();
                    List<ZhonganShixinDO> zhonganShixinDOList = new ArrayList<>();
                    List<ZhonganWeifaDO> zhonganWeifaDOList = new ArrayList<>();
                    List<ZhonganXianchuDO> zhonganXianchuDOList = new ArrayList<>();
                    List<ZhonganXiangaoDO> zhonganXiangaoDOList = new ArrayList<>();
                    List<ZhonganZhixingDO> zhonganZhixingDOList = new ArrayList<>();
                    List<ZhonganZuifanDO> zhonganZuifanDOList = new ArrayList<>();


                    ZhonganInfoDO zhongAnInfoDO = new ZhonganInfoDO();

                    JSONObject creditResultMap = (JSONObject) map.get("creditResult");

                    if (!CollectionUtils.isEmpty(creditResultMap)) {

                        String qcqlInfo = creditResultMap.getString("qcqlInfo");
                        JSONObject qcqlInfoMap = new JSONObject();
                        if (qcqlInfo.contains("高危行为") || qcqlInfo.contains("风险记录")) {
                            qcqlInfoMap = JSON.parseObject(qcqlInfo);
                        }
                        String litigationDetailsMap = (String)creditResultMap.get("litigationDetails");
                        if(litigationDetailsMap !=null &&!"".equals(litigationDetailsMap)){
                            Map litigationMap = (Map) JSON.parse(litigationDetailsMap);
                            if(litigationMap != null){
                                String seconds = (String)litigationMap.get("seconds");
                                String success = (String)litigationMap.get("success");
                                //风险信息页数
                                String fxpgnum = String.valueOf(litigationMap.get("fxpgnum"));
                                String message = (String)litigationMap.get("message");
                                //风险信息条数
                                String fxmsgnum = String.valueOf(litigationMap.get("fxmsgnum"));
                                JSONObject fxcontent = (JSONObject)litigationMap.get("fxcontent");
                                if(fxcontent != null){
                                    //失信老赖名单
                                    String shixin = fxcontent.getString("shixin");
                                    if(!"[]".equals(shixin)){
                                        JSONArray array = JSONArray.parseArray(shixin);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganShixinDO zhonganShixinDO = (ZhonganShixinDO)JSONObject.toJavaObject(jsonObject,ZhonganShixinDO.class);
                                            zhonganShixinDOList.add(zhonganShixinDO);
                                        }
                                    }
                                    //限制出入境名单
                                    String xianchu = fxcontent.getString("xianchu");
                                    if(!"[]".equals(xianchu)){
                                        JSONArray array = JSONArray.parseArray(xianchu);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganXianchuDO zhonganXianchuDO = (ZhonganXianchuDO)JSONObject.toJavaObject(jsonObject,ZhonganXianchuDO.class);
                                            zhonganXianchuDOList.add(zhonganXianchuDO);
                                        }
                                    }

                                    //欠税名单
                                    String qianshui = fxcontent.getString("qianshui");
                                    if(!"[]".equals(qianshui)){
                                        JSONArray array = JSONArray.parseArray(qianshui);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganQianshuiDO zhonganQianshuiDO = (ZhonganQianshuiDO)JSONObject.toJavaObject(jsonObject,ZhonganQianshuiDO.class);
                                            zhonganQianshuiDOList.add(zhonganQianshuiDO);
                                        }
                                    }
                                    //限制高消费名单
                                    String xiangao = fxcontent.getString("xiangao");
                                    if(!"[]".equals(xiangao)){
                                        JSONArray array = JSONArray.parseArray(xiangao);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganXiangaoDO zhonganXiangaoDO = (ZhonganXiangaoDO)JSONObject.toJavaObject(jsonObject,ZhonganXiangaoDO.class);
                                            zhonganXiangaoDOList.add(zhonganXiangaoDO);
                                        }
                                    }
                                    //罪犯及嫌疑人名单
                                    String zuifan = fxcontent.getString("zuifan");
                                    if(!"[]".equals(zuifan)){
                                        JSONArray array = JSONArray.parseArray(zuifan);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganZuifanDO zhonganZuifanDO = (ZhonganZuifanDO)JSONObject.toJavaObject(jsonObject,ZhonganZuifanDO.class);
                                            zhonganZuifanDOList.add(zhonganZuifanDO);
                                        }
                                    }
                                    //纳税非正常户
                                    String feizheng = fxcontent.getString("feizheng");
                                    if(!"[]".equals(feizheng)){
                                        JSONArray array = JSONArray.parseArray(feizheng);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganFeizhengDO zhonganFeizhengDO = (ZhonganFeizhengDO)JSONObject.toJavaObject(jsonObject,ZhonganFeizhengDO.class);
                                            zhonganFeizhengDOList.add(zhonganFeizhengDO);
                                        }
                                    }
                                    //执行公开信息
                                    String zhixing = fxcontent.getString("zhixing");
                                    if(!"[]".equals(zhixing)){
                                        JSONArray array = JSONArray.parseArray(zhixing);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganZhixingDO zhonganZhixingDO = (ZhonganZhixingDO)JSONObject.toJavaObject(jsonObject,ZhonganZhixingDO.class);
                                            zhonganZhixingDOList.add(zhonganZhixingDO);
                                        }
                                    }
                                    //欠款欠费名单
                                    String qiankuan = fxcontent.getString("qiankuan");
                                    if(!"[]".equals(qiankuan)){
                                        JSONArray array = JSONArray.parseArray(qiankuan);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganQiankuanDO zhonganQiankuanDO = (ZhonganQiankuanDO)JSONObject.toJavaObject(jsonObject,ZhonganQiankuanDO.class);
                                            zhonganQiankuanDOList.add(zhonganQiankuanDO);
                                        }
                                    }
                                    //民商事审判流程
                                    String shenpan = fxcontent.getString("shenpan");
                                    if(!"[]".equals(shenpan)){
                                        JSONArray array = JSONArray.parseArray(shenpan);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganShenpanDO zhonganShenpanDO = (ZhonganShenpanDO)JSONObject.toJavaObject(jsonObject,ZhonganShenpanDO.class);
                                            zhonganShenpanDOList.add(zhonganShenpanDO);
                                        }
                                    }
                                    //民商事裁判文书
                                    String caipan = fxcontent.getString("caipan");
                                    if(!"[]".equals(caipan)){
                                        JSONArray array = JSONArray.parseArray(caipan);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganCaipanDO zhonganCaipanDO = (ZhonganCaipanDO)JSONObject.toJavaObject(jsonObject,ZhonganCaipanDO.class);
                                            zhonganCaipanDOList.add(zhonganCaipanDO);
                                        }
                                    }
                                    //行政违法记录
                                    String weifa = fxcontent.getString("weifa");
                                    if(!"[]".equals(weifa)){
                                        JSONArray array = JSONArray.parseArray(weifa);
                                        for(int i=0;i<array.size();i++){
                                            JSONObject jsonObject = array.getJSONObject(i);
                                            ZhonganWeifaDO zhonganWeifaDO = (ZhonganWeifaDO)JSONObject.toJavaObject(jsonObject,ZhonganWeifaDO.class);
                                            zhonganWeifaDOList.add(zhonganWeifaDO);
                                        }
                                    }
                                }

                            }
                        }
                        zhongAnInfoDO.setIdCard(zhongAnCusParam.getIdcard());
                        zhongAnInfoDO.setAge(creditResultMap.getString("age"));
                        zhongAnInfoDO.setGender(creditResultMap.getString("gender"));
                        zhongAnInfoDO.setMobileCity(creditResultMap.getString("mobileCity"));
                        zhongAnInfoDO.setMobileCommDuration(creditResultMap.getString("mobileCommDuration"));
                        zhongAnInfoDO.setMobileCommSts(creditResultMap.getString("mobileCommSts"));
                        zhongAnInfoDO.setPhoneidNameCheck(creditResultMap.getString("PhoneIdNameCheck"));
                        zhongAnInfoDO.setHighRiskBehavior(qcqlInfoMap.getString("高危行为"));
                        zhongAnInfoDO.setHighRiskRecord(qcqlInfoMap.getString("风险记录"));
                        zhongAnInfoDO.setRsnHighRisk(creditResultMap.getString("rsnHighRisk"));
                        zhongAnInfoDO.setRsnLongOverdue(creditResultMap.getString("rsnLongOverdue"));
                        zhongAnInfoDO.setRsnMultiLoan(creditResultMap.getString("rsnMultiLoan"));
                        zhongAnInfoDO.setRsnPolicyRestrict(creditResultMap.getString("rsnPolicyRestrict"));
                        zhongAnInfoDO.setRsnRiskRec(creditResultMap.getString("rsnRiskRec"));
                        zhongAnInfoDO.setRspLawsuitAlllist(creditResultMap.getString("rspLawsuit_details"));
                        zhongAnInfoDO.setRspSpeclistInblacklist(creditResultMap.getString("rspSpecList_inBlacklist"));
                        zhongAnInfoDO.setRspSpeclistMaxdftlevel(creditResultMap.getString("rspSpecList_maxDftLevel"));
                        zhongAnInfoDO.setRspWatchlistDetail(creditResultMap.getString("rspWatchList_detail"));
                        zhongAnInfoDO.setRspGongAn(creditResultMap.getString("rspGongAn"));
                    }
                    zhongAnInfoDO.setCreateDate(new Date());
                    zhongAnInfoDO.setCustomerName(zhongAnCusParam.getName());
                    zhongAnInfoDO.setOrderId(Long.valueOf(zhongAnQueryParam.getOrder_id()));
                    zhongAnInfoDO.setCustomerType(zhongAnCusParam.getCustomertype());
                    zhongAnInfoDO.setResultMessage((String) map.get("message"));
                    zhongAnInfoDO.setTel(zhongAnCusParam.getTel());
                    zhongAnInfoDOMapper.insertSelective(zhongAnInfoDO);

                    String listString = creditResultMap.getString("rspLawsuit_allList");
                    if (listString != null && !"".equals(listString)) {
                        List<Map> map1 = (List<Map>) JSON.parse(listString);
                        for (Map mapx : map1) {
                            RspLawsuitDO rspLawSuitDO = new RspLawsuitDO();
                            rspLawSuitDO.setBody((String) mapx.get("body"));
                            rspLawSuitDO.setDataType((String) mapx.get("dataType"));
                            rspLawSuitDO.setId(zhongAnInfoDO.getId());
                            rspLawSuitDO.setSortTimeString((String) mapx.get("sortTimeString"));
                            rspLawSuitDO.setTitle((String) mapx.get("title"));
                            rspLawSuitDOMapper.insertSelective(rspLawSuitDO);
                        }
                    }
                    String creditString = (String) creditResultMap.get("rspCreditBehavier_application");
                    if (!"[]".equals(creditString) && !"".equals(creditString) && creditString != null) {
                        List<Map> map2 = (List<Map>) JSON.parse(creditString);
                        for (Map mapx : map2) {
                            RspCreditDO rspCreditDO = new RspCreditDO();
                            rspCreditDO.setApplicationMoney((String) mapx.get("application_money"));
                            rspCreditDO.setApplicationResult((String) mapx.get("application_result"));
                            rspCreditDO.setApplicationTime((String) mapx.get("application_time"));
                            rspCreditDO.setId(zhongAnInfoDO.getId());
                            rspCreditDO.setPlatform((String) mapx.get("platform"));
                            rspCreditDO.setPlatformCode((String) mapx.get("platform_code"));
                            rspCreditDOMapper.insertSelective(rspCreditDO);
                        }
                    }
                    if (!"[]".equals(creditString) && !"".equals(creditString) && creditString != null) {
                        String overdueString = (String) creditResultMap.get("rspCreditBehavier_overdue");
                        List<Map> map3 = (List<Map>) JSON.parse(overdueString);
                        for (Map mapx : map3) {
                            ZhonganOverdueDO zhongAnOverDueDO = new ZhonganOverdueDO();
                            zhongAnOverDueDO.setId(zhongAnInfoDO.getId());
                            zhongAnOverDueDO.setOverdueCounts((String) mapx.get("overdue_counts"));
                            zhongAnOverDueDO.setOverdueMoney((String) mapx.get("overdue_money"));
                            zhongAnOverDueDO.setOverdueTime((String) mapx.get("overdue_time"));
                            zhongAnOverDueDO.setPlatformCode((String) mapx.get("platform_code"));
                            zhongAnOverDueDOMapper.insertSelective(zhongAnOverDueDO);
                        }
                    }



                    if(zhonganCaipanDOList.size()!=0){
                        for(ZhonganCaipanDO zhonganCaipanDO:zhonganCaipanDOList){
                            zhonganCaipanDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganCaipanDOMapper.insertSelective(zhonganCaipanDO);
                        }
                    }
                    if(zhonganFeizhengDOList.size()!=0){
                        for(ZhonganFeizhengDO zhonganFeizhengDO:zhonganFeizhengDOList){
                            zhonganFeizhengDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganFeizhengDOMapper.insertSelective(zhonganFeizhengDO);
                        }
                    }
                    if(zhonganQiankuanDOList.size()!=0){
                        for(ZhonganQiankuanDO zhonganQiankuanDO:zhonganQiankuanDOList){
                            zhonganQiankuanDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganQiankuanDOMapper.insertSelective(zhonganQiankuanDO);
                        }
                    }
                    if(zhonganQianshuiDOList.size()!=0){
                        for(ZhonganQianshuiDO zhonganQianshuiDO:zhonganQianshuiDOList){
                            zhonganQianshuiDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganQianshuiDOMapper.insertSelective(zhonganQianshuiDO);
                        }
                    }
                    if(zhonganShenpanDOList.size()!=0){
                        for(ZhonganShenpanDO zhonganShenpanDO:zhonganShenpanDOList){
                            zhonganShenpanDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganShenpanDOMapper.insertSelective(zhonganShenpanDO);
                        }
                    }
                    if(zhonganShixinDOList.size()!=0){
                        for(ZhonganShixinDO zhonganShixinDO:zhonganShixinDOList){
                            zhonganShixinDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganShixinDOMapper.insertSelective(zhonganShixinDO);
                        }
                    }
                    if(zhonganWeifaDOList.size()!=0){
                        for(ZhonganWeifaDO zhonganWeifaDO:zhonganWeifaDOList){
                            zhonganWeifaDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganWeifaDOMapper.insertSelective(zhonganWeifaDO);
                        }
                    }
                    if(zhonganXianchuDOList.size()!=0){
                        for(ZhonganXianchuDO zhonganXianchuDO:zhonganXianchuDOList){
                            zhonganXianchuDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganXianchuDOMapper.insertSelective(zhonganXianchuDO);
                        }
                    }
                    if(zhonganXiangaoDOList.size()!=0){
                        for(ZhonganXiangaoDO zhonganXiangaoDO:zhonganXiangaoDOList){
                            zhonganXiangaoDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganXiangaoDOMapper.insertSelective(zhonganXiangaoDO);
                        }
                    }
                    if(zhonganZhixingDOList.size()!=0){
                        for(ZhonganZhixingDO zhonganZhixingDO:zhonganZhixingDOList){
                            zhonganZhixingDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganZhixingDOMapper.insertSelective(zhonganZhixingDO);
                        }
                    }
                    if(zhonganZuifanDOList.size()!=0){
                        for(ZhonganZuifanDO zhonganZuifanDO:zhonganZuifanDOList){
                            zhonganZuifanDO.setZhongan_id(zhongAnInfoDO.getId());
                            zhonganZuifanDOMapper.insertSelective(zhonganZuifanDO);
                        }
                    }
                } else if ((boolean) map.get("success") == false) {
                    ZhonganInfoDO zhonganInfoDO = new ZhonganInfoDO();
                    zhonganInfoDO.setOrderId(Long.valueOf(zhongAnQueryParam.getOrder_id()));
                    zhonganInfoDO.setCustomerName(zhongAnCusParam.getName());
                    zhonganInfoDO.setResultMessage((String) map.get("message"));
                    zhonganInfoDO.setCustomerType(zhongAnCusParam.getCustomertype());
                    zhonganInfoDO.setIdCard(zhongAnCusParam.getIdcard());
                    zhonganInfoDO.setCreateDate(new Date());
                    zhonganInfoDO.setTel(zhongAnCusParam.getTel());
                    zhongAnInfoDOMapper.insertSelective(zhonganInfoDO);
                    zhonganReturnVO.setFlag(false);
                    returnInfo += zhongAnCusParam.getName() + "实名验证失败;";
                } else {
                    throw new BizException("该客户:" + zhongAnCusParam.getName() + "," + map.get("message"));
                }
            }
            zhonganReturnVO.setReturnInfn(returnInfo);
        } catch (Exception e) {
            logger.error("大数据风控查询失败", e);
            throw new BizException("大数据风控查询延误，请再次查询");
        }
        return zhonganReturnVO;
    }

    @Override
    public ZhongAnDetailQuery zhongAnDetail(Long orderId) {
        ZhongAnDetailQuery zhongAnDetailQuery = new ZhongAnDetailQuery();
        List<ZhonganInfoDO> list = zhongAnInfoDOMapper.selectByCreaditOrderId(orderId);
        for (ZhonganInfoDO zhongAnInfoDO : list) {
            List<ZhonganOverdueDO> overDueList = zhongAnOverDueDOMapper.selectById(zhongAnInfoDO.getId());
            List<RspCreditDO> creditList = rspCreditDOMapper.selectById(zhongAnInfoDO.getId());
            List<RspLawsuitDO> lawSuitList = rspLawSuitDOMapper.selectById(zhongAnInfoDO.getId());
            List<ZhonganCaipanDO> zhonganCaipanDOList = zhonganCaipanDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganFeizhengDO> zhonganFeizhengDOList = zhonganFeizhengDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganQiankuanDO> zhonganQiankuanDOList = zhonganQiankuanDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganQianshuiDO> zhonganQianshuiDOList = zhonganQianshuiDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganShenpanDO> zhonganShenpanDOList = zhonganShenpanDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganShixinDO> zhonganShixinDOList = zhonganShixinDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganWeifaDO> zhonganWeifaDOList = zhonganWeifaDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganXianchuDO> zhonganXianchuDOList = zhonganXianchuDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganXiangaoDO> zhonganXiangaoDOList = zhonganXiangaoDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganZhixingDO> zhonganZhixingDOList = zhonganZhixingDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            List<ZhonganZuifanDO> zhonganZuifanDOList = zhonganZuifanDOMapper.selectByZhonganId(zhongAnInfoDO.getId());
            zhongAnInfoDO.setOverDueList(overDueList);
            zhongAnInfoDO.setCreditList(creditList);
            zhongAnInfoDO.setLawSuitList(lawSuitList);
            zhongAnInfoDO.setZhonganZuifanDOList(zhonganZuifanDOList);
            zhongAnInfoDO.setZhonganZhixingDOList(zhonganZhixingDOList);
            zhongAnInfoDO.setZhonganXiangaoDOList(zhonganXiangaoDOList);
            zhongAnInfoDO.setZhonganXianchuDOList(zhonganXianchuDOList);
            zhongAnInfoDO.setZhonganWeifaDOList(zhonganWeifaDOList);
            zhongAnInfoDO.setZhonganShixinDOList(zhonganShixinDOList);
            zhongAnInfoDO.setZhonganShenpanDOList(zhonganShenpanDOList);
            zhongAnInfoDO.setZhonganQianshuiDOList(zhonganQianshuiDOList);
            zhongAnInfoDO.setZhonganQiankuanDOList(zhonganQiankuanDOList);
            zhongAnInfoDO.setZhonganFeizhengDOList(zhonganFeizhengDOList);
            zhongAnInfoDO.setZhonganCaipanDOList(zhonganCaipanDOList);

        }
        zhongAnDetailQuery.setList(list);
        return zhongAnDetailQuery;
    }

    @Override
    public ZhonganNameVO zhonganName(Long orderId) {
        ZhonganNameVO zhonganNameVO = zhongAnInfoDOMapper.selectZhonganName(orderId);
        return zhonganNameVO;
    }

    @Override
    public void zhonganInsert() {

    }

    @Override
    @Transactional
    public ResultBean<Long> createBaseInfo(AppLoanBaseInfoParam param) {
        Preconditions.checkNotNull(param.getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getLoanBaseInfo(), "贷款基本信息不能为空");



        // 校验客户是否在系统之前已经查过征信，如果已经查过了，则不允许再添加
        // 根据身份证号校验
        checkBankInterfaceSerial(param);
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        loanBaseInfoDO.setSalesmanId(loginUser.getId());

        Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(loginUser.getId());
        loanBaseInfoDO.setPartnerId(partnerId);

        /*PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(partnerId, null);
        if (null != partnerDO) {
            loanBaseInfoDO.setAreaId(partnerDO.getAreaId());
        }*/
        loanBaseInfoDO.setAreaId(param.getLoanBaseInfo().getArea().getId());
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


    /**
     * 校验客户14天内是否提交过征信盛情
     * @param customerParam
     */
    private void checkBankInterfaceSerial(AppLoanBaseInfoParam customerParam) {
        List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.selectCusByOrderId(customerParam.getOrderId());
        loanCustomerDOS.stream().filter(Objects::nonNull).forEach(e->{
            String bank = customerParam.getLoanBaseInfo().getBank();
            bankCredit(customerParam.getOrderId(), e.getIdCard(), e.getName(), bank, e.getPrincipalCustId());
        });
    }


    private void bankCredit(Long orderId, String idCard, String name, String bankName, Long loanCustomerId) {

        if (StringUtils.isNotBlank(idCard)) {

            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.selectByIdCard(idCard);


            List<LoanOrderDO> collect = Lists.newArrayList();

            loanCustomerDOS.stream().filter(Objects::nonNull).forEach(e -> {
                LoanOrderDO loanOrderDO = null;
                //主贷人
                if (PRINCIPAL_LENDER.getType().equals(e.getCustType())) {

                    loanOrderDO = loanOrderDOMapper.selectByCustomerId(e.getId());

                } else
                    //共待人 || 担保人
                    if (COMMON_LENDER.getType().equals(e.getCustType()) ||
                            GUARANTOR.getType().equals(e.getCustType())) {
                        loanOrderDO = loanOrderDOMapper.selectByCustomerId(e.getPrincipalCustId());
                    }

                //如果orderId 为null 第一次创建 需要判断是否重复
                if (loanOrderDO != null) {

                    if(orderId!=null && !loanOrderDO.getId().equals(orderId)){
                        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
                        //同一个贷款银行需要校验征信
                        if (bankName.equals(loanBaseInfoDO.getBank())) {
                            //如果征信申请日志不为NULL
                            LoanProcessLogDO loanProcessLog = loanProcessLogService.getLoanProcessLog(loanOrderDO.getId(), CREDIT_APPLY.getCode());
                            if (loanProcessLog != null) {
                                Date createTime = loanProcessLog.getCreateTime();
                                Date currDate = new Date();
                                int days = (int) ((currDate.getTime() - createTime.getTime()) / (1000 * 3600 * 24));
                                if (days <= 14) {
                                    collect.add(loanOrderDO);
                                    return;
                                }
                            }
                        }
                    }
                }
            });


            if (collect != null && collect.size() > 0) {
                throw new BizException(name + ":征信14天内已经发起征信申请，对应订单号【" + collect.get(0).getId() + "】");

            }


        }

    }
    @Override
    @Transactional
    public ResultBean<Void> updateBaseInfo(AppLoanBaseInfoDetailParam param) {
        Preconditions.checkNotNull(param.getOrderId(),"订单号不能为空");
        AppLoanBaseInfoParam aparam = new AppLoanBaseInfoParam();
        aparam.setOrderId(param.getOrderId());
        aparam.setLoanBaseInfo(param);

        checkBankInterfaceSerial(aparam);
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        convertLoanBaseInfo(param, loanBaseInfoDO);

        ResultBean<Void> resultBean = loanBaseInfoService.update(loanBaseInfoDO);
        return resultBean;
    }

    @Override
    @Transactional
    public ResultBean<Long> addRelaCustomer(CustomerParam customerParam) {

        return loanCustomerService.addRelaCustomer(customerParam);
    }

    @Override
    @Transactional
    public ResultBean<Long> delRelaCustomer(Long customerId) {

        return loanCustomerService.delRelaCustomer(customerId);
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
        VehicleInformationUpdateParam vehicleInformationUpdateParam = new VehicleInformationUpdateParam();
        //如果是二手车需要更新绑定
        if (loanCarInfoParam.getCarType() ==1 )
        {
            if (loanCarInfoParam.getEvaluationType()==2)//手工评估
            {
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());

                //更新vin码---车辆行驶证号码
                vehicleInformationUpdateParam.setVehicle_identification_number(loanCarInfoParam.getVin());

                loanOrderDO.setSecond_hand_car_evaluate_id(null);
                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

            }else if(loanCarInfoParam.getEvaluationType()==1)//在线评估
            {
                loanOrderDO.setSecond_hand_car_evaluate_id(loanCarInfoParam.getSecond_hand_car_evaluate_id());
                SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanCarInfoParam.getSecond_hand_car_evaluate_id());

                // #车牌号码 #车辆类型（小型轿车）  #所有人名称  #发动机号码  #注册日期   #车型颜色
                // vehicleInformationUpdateParam.setLicense_plate_number(secondHandCarEvaluateDO.getPlate_num());
                vehicleInformationUpdateParam.setCar_category(secondHandCarEvaluateDO.getVehicle_type());
                vehicleInformationUpdateParam.setEngine_number(secondHandCarEvaluateDO.getEngine_num());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                vehicleInformationUpdateParam.setRegister_date(sdf.format(secondHandCarEvaluateDO.getRegister_date()));
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
                vehicleInformationUpdateParam.setVehicle_identification_number(secondHandCarEvaluateDO.getVin());
            }else{
                throw new BizException("估价类型有误");
            }
        }else
        {
            vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
            vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
        }

        ResultBean<Void> updateRelaResultBean = loanProcessOrderService.update(loanOrderDO);
        Preconditions.checkArgument(updateRelaResultBean.getSuccess(), updateRelaResultBean.getMsg());

        vehicleInformationUpdateParam.setOrder_id(loanCarInfoParam.getOrderId().toString());
        vehicleInformationUpdateParam.setApply_license_plate_area(loanCarInfoParam.getApplyLicensePlateAreaId());
        vehicleInformationUpdateParam.setLicense_plate_type(loanCarInfoParam.getLicensePlateType());

        vehicleInformationService.update(vehicleInformationUpdateParam);

        String s = loanCarInfoParam.getApplyLicensePlateAreaId();
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        loanBaseInfoDO.setAreaId(Long.valueOf(s));
        LoanBaseInfoDO loanBaseInfoDO1 = loanBaseInfoDOMapper.getTotalInfoByOrderId(loanCarInfoParam.getOrderId());
        loanBaseInfoDO.setId(loanBaseInfoDO1.getId());
        loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);

        return ResultBean.ofSuccess(createResultBean.getData(), "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLoanCarInfo(AppLoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkArgument(null != loanCarInfoParam && null != loanCarInfoParam.getId(), "车辆信息ID不能为空");
        Preconditions.checkNotNull(loanCarInfoParam.getOrderId(), "订单号不能为空");

        VehicleInformationUpdateParam vehicleInformationUpdateParam = new VehicleInformationUpdateParam();
        //更新绑定
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanCarInfoParam.getOrderId());
        if (loanCarInfoParam.getCarType() ==0 )
        {
            vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
            vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
            loanOrderDO.setSecond_hand_car_evaluate_id(null);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else {

            if (loanCarInfoParam.getEvaluationType()==2)//手工评估
            {
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());

                //更新vin码---车辆行驶证号码
                vehicleInformationUpdateParam.setVehicle_identification_number(loanCarInfoParam.getVin());

                loanOrderDO.setSecond_hand_car_evaluate_id(null);
                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

            }else if(loanCarInfoParam.getEvaluationType()==1)//在线评估
            {
                loanOrderDO.setSecond_hand_car_evaluate_id(loanCarInfoParam.getSecond_hand_car_evaluate_id());
                //更新车辆信息其他信息
                SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanCarInfoParam.getSecond_hand_car_evaluate_id());

                // #车牌号码 #车辆类型（小型轿车）  #所有人名称  #发动机号码  #注册日期   #车型颜色
                //vehicleInformationUpdateParam.setLicense_plate_number(secondHandCarEvaluateDO.getPlate_num());
                vehicleInformationUpdateParam.setCar_category(secondHandCarEvaluateDO.getVehicle_type());
                //vehicleInformationUpdateParam.setNow_driving_license_owner(secondHandCarEvaluateDO.getOwner());
                vehicleInformationUpdateParam.setEngine_number(secondHandCarEvaluateDO.getEngine_num());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                vehicleInformationUpdateParam.setRegister_date(sdf.format(secondHandCarEvaluateDO.getRegister_date()));
                /*vehicleInformationUpdateParam.setColor(secondHandCarEvaluateDO.getStyle_color());*/
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
                vehicleInformationUpdateParam.setVehicle_identification_number(secondHandCarEvaluateDO.getVin());

                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            }else{
                throw new BizException("估价类型有误");
            }

        }
        // convert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        convertLoanCarInfo(loanCarInfoParam, loanCarInfoDO);

        ResultBean<Void> resultBean = loanCarInfoService.update(loanCarInfoDO);

        vehicleInformationUpdateParam.setOrder_id(loanCarInfoParam.getOrderId().toString());
        vehicleInformationUpdateParam.setApply_license_plate_area(loanCarInfoParam.getApplyLicensePlateAreaId());
        vehicleInformationUpdateParam.setLicense_plate_type(loanCarInfoParam.getLicensePlateType());
        vehicleInformationService.update(vehicleInformationUpdateParam);

        String s = loanCarInfoParam.getApplyLicensePlateAreaId();
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        loanBaseInfoDO.setAreaId(Long.valueOf(s));
        LoanBaseInfoDO loanBaseInfoDO1 = loanBaseInfoDOMapper.getTotalInfoByOrderId(loanCarInfoParam.getOrderId());
        loanBaseInfoDO.setId(loanBaseInfoDO1.getId());
        loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);

        return resultBean;
    }

    @Override
    public ResultBean<AppLoanCarInfoVO> loanCarInfoDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        AppLoanCarInfoVO loanCarInfoVO = new AppLoanCarInfoVO();

        Long loanCarInfoId = loanOrderDOMapper.getLoanCarInfoIdById(orderId);

        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanCarInfoId);
        if (null != loanCarInfoDO)
        {
            BeanUtils.copyProperties(loanCarInfoDO, loanCarInfoVO);

            // 车型
            BaseVO carDetail = new BaseVO();
            carDetail.setId(loanCarInfoDO.getCarDetailId());
            if (loanCarInfoDO.getCarDetailId()!=null)
            {
                CarDetailDO carDetailDO = carDetailDOMapper.selectByPrimaryKey(loanCarInfoDO.getCarDetailId(), null);
                carDetail.setName(carDetailDO.getName());
            }

            loanCarInfoVO.setCarDetail(carDetail);

            // 合伙人账户信息
            AppLoanCarInfoVO.PartnerAccountInfo partnerAccountInfo = new AppLoanCarInfoVO.PartnerAccountInfo();
            BeanUtils.copyProperties(loanCarInfoDO, partnerAccountInfo);
            loanCarInfoVO.setPartnerAccountInfo(partnerAccountInfo);
        }

        Long vid = loanOrderDOMapper.getVehicleInformationIdById(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);

        //业务员名
        if (loanBaseInfoDO!=null && loanBaseInfoDO.getSalesmanId()!=null)
        {
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanBaseInfoDO.getSalesmanId(), VALID_STATUS);
            if (employeeDO!=null)
            {
                loanCarInfoVO.setSalemanName(employeeDO.getName());
            }
        }

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        VehicleInformationDO vehicleInformationDO = vehicleInformationDOMapper.selectByPrimaryKey(vid);

        if (loanOrderDO.getSecond_hand_car_evaluate_id()!=null && !"".equals(loanOrderDO.getSecond_hand_car_evaluate_id()))
        {
            SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanOrderDO.getSecond_hand_car_evaluate_id());
            if (secondHandCarEvaluateDO!=null)
            {
                loanCarInfoVO.setVin(secondHandCarEvaluateDO.getVin());
                loanCarInfoVO.setSecond_hand_car_evaluate_id(loanOrderDO.getSecond_hand_car_evaluate_id());
            }

        }
        if (vehicleInformationDO != null)
        {
            if (null != loanCarInfoDO && loanCarInfoDO.getEvaluationType()!=null && loanCarInfoDO.getEvaluationType()==2)
            {
                loanCarInfoVO.setVin(vehicleInformationDO.getVehicle_identification_number());
            }
            loanCarInfoVO.setNowDrivingLicenseOwner(vehicleInformationDO.getNow_driving_license_owner());
            loanCarInfoVO.setLicensePlateType(vehicleInformationDO.getLicense_plate_type() == null ? null : vehicleInformationDO.getLicense_plate_type().toString());
            loanCarInfoVO.setColor(vehicleInformationDO.getColor());
        }
        /*String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            loanCarInfoVO.setApplyLicensePlateAreaId(baseAreaDO.getAreaId());
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        loanCarInfoVO.setApplyLicensePlateArea(tmpApplyLicensePlateArea);*/

        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId() != null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if ("3".equals(String.valueOf(baseAreaDO.getLevel()))) {
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            //loanCarInfoVO.setHasApplyLicensePlateArea(baseAreaDO);

            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }
        loanCarInfoVO.setApplyLicensePlateArea(tmpApplyLicensePlateArea);
        loanCarInfoVO.setApplyLicensePlateAreaId(loanBaseInfoDO.getAreaId());
        return ResultBean.ofSuccess(loanCarInfoVO);
    }

    /**
     * insert贷款金融方案
     *
     * @param appLoanFinancialPlanParam
     */
    @Override
    @Transactional
    public ResultBean<Long> createLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam) {
        Preconditions.checkNotNull(appLoanFinancialPlanParam.getOrderId(), "业务单号不能为空");

        // convert
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(appLoanFinancialPlanParam, loanFinancialPlanDO);
        doAttachTask_loanApply(appLoanFinancialPlanParam.getOrderId(),appLoanFinancialPlanParam);
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
     * 贷款申请校验1大于，2小于，3大于等于，4小于等于
     */
    private void doAttachTask_loanApply( Long orderId,AppLoanFinancialPlanParam appLoanFinancialPlanParam) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
/*            Map map = financialProductDOMapper.selectProductInfoByOrderId(loanOrderDO.getId());
            Long loanFinancialPlanId = loanOrderDOMapper.getLoanFinancialPlanIdById(loanOrderDO.getId());
            LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);*/
        //金融手续费
        BigDecimal financialServiceFee = appLoanFinancialPlanParam.getBankPeriodPrincipal().subtract(appLoanFinancialPlanParam.getLoanAmount());
        //首付比例
        BigDecimal downPaymentRatio = appLoanFinancialPlanParam.getDownPaymentRatio();
        //贷款比例
        BigDecimal loanRate = appLoanFinancialPlanParam.getLoanAmount().divide(appLoanFinancialPlanParam.getCarPrice(),2,BigDecimal.ROUND_HALF_UP);
        //银行分期比例
        BigDecimal stagingRatio = appLoanFinancialPlanParam.getStagingRatio();
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        String bankName = loanBaseInfoDO.getBank();
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
        if (loanCarInfoDO ==null)
        {
            throw new BizException("车辆信息未保存！！");
        }
        int carType = loanCarInfoDO.getCarType();
        ConfLoanApplyDOKey confLoanApplyDOKey = new ConfLoanApplyDOKey();
        confLoanApplyDOKey.setBank(bankName);
        confLoanApplyDOKey.setCar_type(carType);
        ConfLoanApplyDO confLoanApplyDO = confLoanApplyDOMapper.selectByPrimaryKey(confLoanApplyDOKey);
        if(confLoanApplyDO!=null) {
            if (confLoanApplyDO.getDown_payment_ratio() != null && confLoanApplyDO.getDown_payment_ratio_compare() != null) {
                compardNum(confLoanApplyDO.getDown_payment_ratio_compare(), downPaymentRatio, confLoanApplyDO.getDown_payment_ratio(), "首付比例");
            }
            if (confLoanApplyDO.getFinancial_service_fee() != null && confLoanApplyDO.getFinancial_service_fee_compard() != null) {
                compardNum(confLoanApplyDO.getFinancial_service_fee_compard(), financialServiceFee.divide(new BigDecimal("10000")), confLoanApplyDO.getFinancial_service_fee(), "金融手续费");
            }
            if (confLoanApplyDO.getCar_ratio() != null && confLoanApplyDO.getCar_ratio_compard() != null) {
                compardNum(confLoanApplyDO.getCar_ratio_compard(), financialServiceFee, appLoanFinancialPlanParam.getCarPrice().multiply(confLoanApplyDO.getCar_ratio().divide(new BigDecimal("100"))), "金融手续费2");
            }
            if (confLoanApplyDO.getLoan_ratio() != null && confLoanApplyDO.getLoan_ratio_compare() != null) {
                compardNum(confLoanApplyDO.getLoan_ratio_compare(), loanRate, confLoanApplyDO.getLoan_ratio().divide(new BigDecimal("100")), "贷款比例");
            }
            if (confLoanApplyDO.getStaging_ratio() != null && confLoanApplyDO.getStaging_ratio_compard() != null) {
                compardNum(confLoanApplyDO.getStaging_ratio_compard(), stagingRatio, confLoanApplyDO.getStaging_ratio(), "银行分期比例");
            }
        }
    }
    //1大于，2小于，3大于等于，4小于等于
    public void compardNum(String flag, BigDecimal now, BigDecimal data, String reason) {
        int i = 0;
        i++;
        if ("1".equals(flag)) {
            if (now.compareTo(data) != 1) {
                throw new BizException(reason + "不能小于等于" + data);
            }
        } else if ("2".equals(flag)) {
            if (now.compareTo(data) != -1) {
                throw new BizException(reason + "不能大于等于" + data);
            }
        } else if ("3".equals(flag)) {
            if (now.compareTo(data) == -1) {
                throw new BizException(reason + "不能小于" + data);
            }
        } else if ("4".equals(flag)) {
            if (now.compareTo(data) == 1) {
                throw new BizException(reason + "不能大于" + data);
            }
        }
    }

    /**
     * update贷款金融方案
     *
     * @param appLoanFinancialPlanParam
     */
    @Override
    @Transactional
    public ResultBean<Void> updateLoanFinancialPlan(AppLoanFinancialPlanParam appLoanFinancialPlanParam) {
        Preconditions.checkNotNull(appLoanFinancialPlanParam, "金融方案不能为空");
        doAttachTask_loanApply(appLoanFinancialPlanParam.getOrderId(),appLoanFinancialPlanParam);
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

    private void convertLoanCustomer(CustomerParam customerParam, LoanCustomerDO loanCustomerDO) {
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
    private Long createLoanCustomer(CustomerParam customerParam) {
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

        // 是否可以弃单
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(loanOrderDO.getId());
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        if (TASK_PROCESS_DONE.equals(loanProcessDO.getRemitReview())) {
            // 已经打款确认
            appOrderProcessVO.setCanCancelTask(false);
        } else if (ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()) && !TASK_PROCESS_DONE.equals(loanProcessDO.getRemitReview())) {
            // 进行中 + 未打款确认
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

                        // 操作
                        String actionText = convertActionText(e.getAction());
                        task.setTaskStatusText(actionText);
                        task.setActionText(actionText);

                        // 审核员
                        task.setAuditor(e.getUserName());
                        // 审核备注
                        task.setApprovalInfo(e.getInfo());
                        // 审核员角色 OR 合伙人团队名称
                        task.setUserGroup(getUserGroup(e.getTaskDefinitionKey(), loanProcessDO.getTelephoneVerify(), loanOrderDO.getLoanBaseInfoId()));

                        taskList.add(task);
                    });

            appOrderProcessVO.setTaskList(taskList);
        } else {
            appOrderProcessVO.setTaskList(Collections.EMPTY_LIST);
        }
    }

    /**
     * 审核员角色 OR 合伙人团队名称
     *
     * @param taskDefinitionKey
     * @param telephoneVerify
     * @param loanBaseInfoId
     * @return
     */
    private String getUserGroup(String taskDefinitionKey, Byte telephoneVerify, Long loanBaseInfoId) {
        // 审单员角色
        String userGroup = TASK_USER_GROUP_MAP.get(taskDefinitionKey);

        // 电审角色
        if (TELEPHONE_VERIFY.getCode().equals(taskDefinitionKey)) {
            switch (telephoneVerify) {
                case 4:
                    userGroup = "电审专员";
                    break;
                case 5:
                    userGroup = "电审主管";
                    break;
                case 6:
                    userGroup = "电审经理";
                    break;
                case 7:
                    userGroup = "电审总监";
                    break;
            }
        }

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

    /**
     * 增补类型文本值
     *
     * @param supplementType
     * @return
     */
    private String getSupplementTypeText(Byte supplementType) {

        Map<String, String> kvMap = dictService.getKVMap("infoSupplementType");

        if (!CollectionUtils.isEmpty(kvMap)) {

            String supplementTypeText = kvMap.get(String.valueOf(supplementType));

            return supplementTypeText;
        }
        return null;
    }
}
