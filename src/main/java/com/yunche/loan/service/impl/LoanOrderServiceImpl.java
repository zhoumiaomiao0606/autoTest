package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.impl.persistence.entity.HistoricTaskInstanceEntity;
import org.activiti.engine.task.TaskInfo;
import org.activiti.engine.task.TaskInfoQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanCustomerConst.CREDIT_TYPE_BANK;
import static com.yunche.loan.config.constant.LoanCustomerConst.CREDIT_TYPE_SOCIAL;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.INFO_SUPPLEMENT;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_ACTION;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_INFO_SUPPLEMENT_TYPE;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
public class LoanOrderServiceImpl implements LoanOrderService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

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
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private CarBrandDOMapper carBrandDOMapper;

    @Autowired
    private CarModelDOMapper carModelDOMapper;

    @Autowired
    private CarDetailDOMapper carDetailDOMapper;

    @Autowired
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private BaseAreaService baseAreaService;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private LoanProcessOrderService loanProcessOrderService;

    @Autowired
    private LoanCreditInfoService loanCreditInfoService;

    @Autowired
    private LoanCarInfoService loanCarInfoService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private VehicleInformationService vehicleInformationService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LoanQueryService loanQueryService;


    @Override
    public ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        // 订单基本信息
        CreditApplyOrderVO creditApplyOrderVO = new CreditApplyOrderVO();
        BeanUtils.copyProperties(loanOrderDO, creditApplyOrderVO);
        creditApplyOrderVO.setOrderId(String.valueOf(orderId));

        // 关联的-客户信息(主贷人/共贷人/担保人/紧急联系人)
        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId, null);
        BeanUtils.copyProperties(custDetailVOResultBean.getData(), creditApplyOrderVO);

        // 关联的-贷款基本信息
        ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        creditApplyOrderVO.setLoanBaseInfo(loanBaseInfoVOResultBean.getData());

        return ResultBean.ofSuccess(creditApplyOrderVO, "查询征信申请单详情成功");
    }

    @Override
    @Transactional
    public ResultBean<String> createCreditApplyOrder(CreditApplyOrderParam param) {
        Preconditions.checkNotNull(param, "不能为空");

        // 权限校验
        permissionService.checkTaskPermission(LoanProcessEnum.CREDIT_APPLY.getCode());

        // 是否已经禁用该银行
        checkDisableBank(param.getLoanBaseInfo());

        // 创建贷款基本信息
        Long baseInfoId = createLoanBaseInfo(param.getLoanBaseInfo());

        // 创建客户信息
        Long customerId = createLoanCustomer(param);

        // 创建订单
        Long orderId = createLoanOrder(baseInfoId, customerId);

        return ResultBean.ofSuccess(String.valueOf(orderId));
    }

    private void checkDisableBank(LoanBaseInfoParam loanBaseInfo) {
        Preconditions.checkArgument(null != loanBaseInfo && StringUtils.isNotBlank(loanBaseInfo.getBank()),
                "贷款银行不能为空");

        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfo.getPartnerId(), VALID_STATUS);
        Preconditions.checkNotNull(partnerDO, "合伙人不存在");

        String loanBank = loanBaseInfo.getBank();

        // 禁止查征信银行 校验
        String disableBankList = partnerDO.getDisableBankList();
        if (StringUtils.isNotBlank(disableBankList)) {
            String[] disableBankListArr = disableBankList.split("\\,");

            Preconditions.checkArgument(!Arrays.asList(disableBankListArr).contains(loanBank), "您当前[征信查询]银行已被禁，请联系管理员！");
        }

    }

    @Override
    @Transactional
    public ResultBean<Void> updateCreditApplyOrder(CreditApplyOrderParam param) {
        Preconditions.checkNotNull(param.getOrderId(), "业务单号不能为空");

        // 编辑贷款基本信息
        updateLoanBaseInfo(param.getLoanBaseInfo());

        // 编辑客户信息
        updateOrInsertLoanCustomer(param);

        return ResultBean.ofSuccess(null);
    }

    @Override
    public ResultBean<CreditRecordVO> creditRecordDetail(Long orderId, Byte creditType) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        Preconditions.checkNotNull(creditType, "征信类型不能为空");
        Preconditions.checkArgument(CREDIT_TYPE_BANK.equals(creditType) || CREDIT_TYPE_SOCIAL.equals(creditType), "征信类型有误");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        // 客户信息 & 征信信息
        ResultBean<CreditRecordVO> resultBean = loanCreditInfoService.detailAll(loanOrderDO.getLoanCustomerId(), creditType);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

        CreditRecordVO creditRecordVO = resultBean.getData();

        // 贷款基本信息
        ResultBean<LoanBaseInfoVO> loanBaseInfoResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkArgument(loanBaseInfoResultBean.getSuccess(), loanBaseInfoResultBean.getMsg());
        creditRecordVO.setLoanBaseInfo(loanBaseInfoResultBean.getData());

        return ResultBean.ofSuccess(creditRecordVO, "征信录入详情查询成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> createLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkNotNull(loanCarInfoParam.getOrderId(), "业务单号不能为空");

        // convert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        convertLoanCarInfo(loanCarInfoParam, loanCarInfoDO);
        // insert
        LoanOrderDO loanOrder = loanOrderDOMapper.selectByPrimaryKey(loanCarInfoParam.getOrderId());
        if (loanOrder.getLoanCarInfoId() != null) {
            ResultBean<Void> update = loanCarInfoService.update(loanCarInfoDO);
        } else {
            ResultBean<Long> createResultBean = loanCarInfoService.create(loanCarInfoDO);
            Preconditions.checkArgument(createResultBean.getSuccess(), createResultBean.getMsg());
            // 关联
            LoanOrderDO loanOrderDO = new LoanOrderDO();
            loanOrderDO.setId(loanCarInfoParam.getOrderId());
            loanOrderDO.setLoanCarInfoId(createResultBean.getData());
            ResultBean<Void> updateRelaResultBean = loanProcessOrderService.update(loanOrderDO);
            Preconditions.checkArgument(updateRelaResultBean.getSuccess(), updateRelaResultBean.getMsg());
        }

        VehicleInformationUpdateParam vehicleInformationUpdateParam = new VehicleInformationUpdateParam();
        vehicleInformationUpdateParam.setOrder_id(loanCarInfoParam.getOrderId().toString());
        vehicleInformationUpdateParam.setApply_license_plate_area(loanCarInfoParam.getApplyLicensePlateArea());
        vehicleInformationUpdateParam.setLicense_plate_type(loanCarInfoParam.getLicensePlateType());
        vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
        vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());

        vehicleInformationService.update(vehicleInformationUpdateParam);

        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
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
    @Transactional
    public ResultBean<Long> createCreditRecord(CreditRecordParam creditRecordParam) {
        LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
        BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);

        ResultBean<Long> resultBean = loanCreditInfoService.create(loanCreditInfoDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        return ResultBean.ofSuccess(resultBean.getData(), "征信结果录入成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> updateCreditRecord(CreditRecordParam creditRecordParam) {
        LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
        BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);

        ResultBean<Long> resultBean = loanCreditInfoService.update(loanCreditInfoDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        return ResultBean.ofSuccess(resultBean.getData(), "征信结果修改成功");
    }

    @Override
    public ResultBean<LoanSimpleInfoVO> simpleInfo(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        CustomerVO customerVO = loanCustomerService.getById(loanOrderDO.getLoanCustomerId());

        LoanSimpleInfoVO loanSimpleInfoVO = new LoanSimpleInfoVO();
        if (null != customerVO) {
            loanSimpleInfoVO.setCustomerId(customerVO.getId());
            loanSimpleInfoVO.setCustomerName(customerVO.getName());
            loanSimpleInfoVO.setIdCard(customerVO.getIdCard());
            loanSimpleInfoVO.setMobile(customerVO.getMobile());
        }

        ResultBean<LoanBaseInfoVO> loanBaseInfoVOResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkArgument(loanBaseInfoVOResultBean.getSuccess(), loanBaseInfoVOResultBean.getMsg());
        LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoVOResultBean.getData();

        loanSimpleInfoVO.setLoanAmount(loanBaseInfoVO.getActualLoanAmount());
        if (null != loanBaseInfoVO.getArea() && null != loanBaseInfoVO.getArea().getId()) {
            ResultBean<String> fullAreaNameResult = baseAreaService.getFullAreaName(loanBaseInfoVO.getArea().getId());
            Preconditions.checkArgument(fullAreaNameResult.getSuccess(), fullAreaNameResult.getMsg());
            loanSimpleInfoVO.setArea(fullAreaNameResult.getData());
        }
        loanSimpleInfoVO.setBank(loanBaseInfoVO.getBank());
        if (null != loanBaseInfoVO.getPartner()) {
            loanSimpleInfoVO.setPartnerId(loanBaseInfoVO.getPartner().getId());
            loanSimpleInfoVO.setPartnerName(loanBaseInfoVO.getPartner().getName());
        }

        // TODO 创建时间
        loanSimpleInfoVO.setCreateTime(new Date());
        loanSimpleInfoVO.setSalesMan(loanBaseInfoVO.getSalesman().getName());

        return ResultBean.ofSuccess(loanSimpleInfoVO);
    }

    @Override
    public ResultBean<List<LoanSimpleCustomerInfoVO>> simpleCustomerInfo(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId, null);
        Preconditions.checkArgument(custDetailVOResultBean.getSuccess(), custDetailVOResultBean.getMsg());


        List<LoanSimpleCustomerInfoVO> loanSimpleCustomerInfoVOS = Lists.newArrayList();
        CustDetailVO custDetailVO = custDetailVOResultBean.getData();
        if (null != custDetailVO) {
            if (null != custDetailVO.getPrincipalLender()) {
                // 封装用户信息并填充到容器
                fillLoanSimpleCustomerInfoVO(custDetailVO.getPrincipalLender(), loanSimpleCustomerInfoVOS);
            }

            if (!CollectionUtils.isEmpty(custDetailVO.getCommonLenderList())) {
                List<CustomerVO> commonLenderList = custDetailVO.getCommonLenderList();

                commonLenderList.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            // 封装用户信息并填充到容器
                            fillLoanSimpleCustomerInfoVO(e, loanSimpleCustomerInfoVOS);
                        });
            }

            if (!CollectionUtils.isEmpty(custDetailVO.getGuarantorList())) {
                List<CustomerVO> guarantorList = custDetailVO.getGuarantorList();

                guarantorList.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            // 封装用户信息并填充到容器
                            fillLoanSimpleCustomerInfoVO(e, loanSimpleCustomerInfoVOS);
                        });
            }

            if (!CollectionUtils.isEmpty(custDetailVO.getEmergencyContactList())) {
                List<CustomerVO> emergencyContactList = custDetailVO.getEmergencyContactList();

                emergencyContactList.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            // 封装用户信息并填充到容器
                            fillLoanSimpleCustomerInfoVO(e, loanSimpleCustomerInfoVOS);
                        });
            }

        }

        return ResultBean.ofSuccess(loanSimpleCustomerInfoVOS);
    }


    @Override
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanCarInfoVO loanCarInfoVO = new LoanCarInfoVO();

        Long loanCarInfoId = loanOrderDOMapper.getLoanCarInfoIdById(orderId);

        Long loanBaseInfoId = loanOrderDOMapper.selectByPrimaryKey(orderId).getLoanBaseInfoId();

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);

        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanCarInfoId);
        if (null != loanCarInfoDO) {
            BeanUtils.copyProperties(loanCarInfoDO, loanCarInfoVO);

            // 车型回填
            fillCascadeCarDetail(loanCarInfoDO.getCarDetailId(), loanCarInfoVO);

            // 合伙人账户信息
            LoanCarInfoVO.PartnerAccountInfo partnerAccountInfo = new LoanCarInfoVO.PartnerAccountInfo();
            BeanUtils.copyProperties(loanCarInfoDO, partnerAccountInfo);
            loanCarInfoVO.setPartnerAccountInfo(partnerAccountInfo);
        }

        Long vid = loanOrderDOMapper.getVehicleInformationIdById(orderId);
        VehicleInformationDO vehicleInformationDO = vehicleInformationDOMapper.selectByPrimaryKey(vid);
        if (vehicleInformationDO != null) {
            String tmpApplyLicensePlateArea = null;
            if (StringUtils.isNotBlank(vehicleInformationDO.getApply_license_plate_area())) {
                BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.valueOf(vehicleInformationDO.getApply_license_plate_area()), VALID_STATUS);
                loanCarInfoVO.setHasApplyLicensePlateArea(baseAreaDO);

                if (baseAreaDO != null) {
                    if (baseAreaDO.getParentAreaName() != null) {
                        tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                    } else {
                        tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                    }
                }
            }
            loanCarInfoVO.setApplyLicensePlateArea(tmpApplyLicensePlateArea);
            loanCarInfoVO.setNowDrivingLicenseOwner(vehicleInformationDO.getNow_driving_license_owner());
            loanCarInfoVO.setLicensePlateType(vehicleInformationDO.getLicense_plate_type() == null ? null : vehicleInformationDO.getLicense_plate_type().toString());
            loanCarInfoVO.setColor(vehicleInformationDO.getColor());
        }
        //增加业务员
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        if (universalInfoVO != null) {
            loanCarInfoVO.setSalesManName(universalInfoVO.getSalesman_name());
            loanCarInfoVO.setPartnerName(universalInfoVO.getPartner_name());
        }
        return ResultBean.ofSuccess(loanCarInfoVO);
    }


    @Override
    public ResultBean<LoanHomeVisitVO> homeVisitDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanOrderDO.getLoanHomeVisitId());
        LoanHomeVisitVO loanHomeVisitVO = new LoanHomeVisitVO();
        if (null != loanHomeVisitDO) {
            BeanUtils.copyProperties(loanHomeVisitDO, loanHomeVisitVO);
        }

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {

            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);

            // 主贷人 文件回填
            if ("1".equals(universalCustomerVO.getCust_type())) {

                if (!CollectionUtils.isEmpty(files)) {

                    List<FileVO> homeVisitFiles = Lists.newArrayList();

                    // 12-合影照片;13-家访视频; 16-家访照片; 17-车辆照片;18-其他资料;
                    files.stream()
//                            .filter(e -> "12".equals(e.getType()) || "13".equals(e.getType())
//                                    || "16".equals(e.getType()) || "17".equals(e.getType())
//                                    || "18".equals(e.getType()))
                            // 非空
                            .filter(e -> !CollectionUtils.isEmpty(e.getUrls()))
                            .forEach(e -> {

                                FileVO fileVO = new FileVO();
                                fileVO.setType(Byte.valueOf(e.getType()));
                                fileVO.setName(e.getName());
                                fileVO.setUrls(e.getUrls());

                                homeVisitFiles.add(fileVO);
                            });

                    loanHomeVisitVO.setFiles(homeVisitFiles);
                }
            }
        }
        loanHomeVisitVO.setCustomers(customers);

        return ResultBean.ofSuccess(loanHomeVisitVO);
    }

    @Override
    @Transactional
    public ResultBean<Long> createOrUpdateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam) {
        Preconditions.checkNotNull(loanHomeVisitParam, "上门家访资料不能为空");

        if (null == loanHomeVisitParam.getId()) {
            // 创建
            return createLoanHomeVisit(loanHomeVisitParam);
        } else {
            // 编辑
            return updateLoanHomeVisit(loanHomeVisitParam);
        }
    }

    /**
     * 打回 OR 未提交
     * 历史 action == 3   --> 打回
     *
     * @param loanOrderVO
     * @param processInstanceId
     */
    private void fillTaskTypeText(LoanOrderVO loanOrderVO, String processInstanceId) {
        if (null != loanOrderVO.getId()) {
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(loanOrderVO.getId()));
            if (null != loanOrderDO) {
//                String previousTaskDefKey = loanOrderDO.getPreviousTaskDefKey();
                String previousTaskDefKey = null;
                if (StringUtils.isNotBlank(previousTaskDefKey)) {

                    List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .taskDefinitionKey(previousTaskDefKey)
                            .orderByTaskCreateTime()
                            .desc()
                            .listPage(0, 1);

                    if (!CollectionUtils.isEmpty(historicTaskInstanceList)) {
                        HistoricTaskInstance historicTaskInstance = historicTaskInstanceList.get(0);

                        String taskVariableActionKey = previousTaskDefKey + ":" + processInstanceId + ":"
                                + historicTaskInstance.getExecutionId() + ":" + PROCESS_VARIABLE_ACTION;

                        HistoricVariableInstance actionHistoricVariableInstance = historyService.createHistoricVariableInstanceQuery()
                                .processInstanceId(processInstanceId).variableName(taskVariableActionKey).singleResult();

                        // 上一步action
                        if (null != actionHistoricVariableInstance) {
                            Integer action = (Integer) actionHistoricVariableInstance.getValue();
                            // 打回 OR 未提交
                            if (ACTION_REJECT_MANUAL.equals(action)) {
                                loanOrderVO.setTaskTypeText(TASK_TYPE_TEXT_REJECT);
                            } else if (ACTION_PASS.equals(action)) {
                                loanOrderVO.setTaskTypeText(TASK_TYPE_TEXT_UN_SUBMIT);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 根据流程实例ID获取并填充业务单基本信息
     *
     * @param loanOrderVO
     * @param processInstanceId
     * @return
     */
    private void fillBaseMsg(LoanOrderVO loanOrderVO, String processInstanceId) {
        // 业务单
        LoanOrderDO loanOrderDO = loanOrderDOMapper.getByProcessInstId(processInstanceId);
        if (null == loanOrderDO) {
            return;
        }

        // 业务单单号
        loanOrderVO.setId(String.valueOf(loanOrderDO.getId()));
        // 业务单创建时间
        loanOrderVO.setGmtCreate(loanOrderDO.getGmtCreate());

        // 主贷人信息
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            BaseVO customer = new BaseVO();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            // 主贷人
            loanOrderVO.setCustomer(customer);
            // 身份证
            loanOrderVO.setIdCard(loanCustomerDO.getIdCard());
            // 手机号
            loanOrderVO.setMobile(loanCustomerDO.getMobile());
        }

        // 合伙人 & 业务员
        ResultBean<LoanBaseInfoVO> loanBaseInfoResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkArgument(loanBaseInfoResultBean.getSuccess(), loanBaseInfoResultBean.getMsg());
        LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoResultBean.getData();
        if (null != loanBaseInfoVO) {
            // 合伙人
            loanOrderVO.setPartner(loanBaseInfoVO.getPartner());
            // 业务员
            loanOrderVO.setSalesman(loanBaseInfoVO.getSalesman());
        }

        // 金融方案
        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
        if (null != loanFinancialPlanDO) {
            // 银行
            loanOrderVO.setBank(loanFinancialPlanDO.getBank());
            // 贷款额
            loanOrderVO.setLoanAmount(loanFinancialPlanDO.getLoanAmount());
            // 贷款期限
            loanOrderVO.setLoanTime(loanFinancialPlanDO.getLoanTime());
            // 执行利率
            loanOrderVO.setSignRate(loanFinancialPlanDO.getSignRate());
            // 银行分期本金
            loanOrderVO.setBankPeriodPrincipal(loanFinancialPlanDO.getBankPeriodPrincipal());
            // 首付款
            loanOrderVO.setDownPaymentMoney(loanFinancialPlanDO.getDownPaymentMoney());
        }

        // 车辆信息
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
        if (null != loanCarInfoDO) {
            // 车辆类型：1-新车; 2-二手车;
            loanOrderVO.setCarType(loanCarInfoDO.getCarType());
        }

        // TODO 逾期次数
        loanOrderVO.setOverdueNum(0);
    }


    /**
     * 资料增补类型
     *
     * @param loanOrderVO
     * @param taskDefinitionKey
     * @param processInstanceId
     */
    private void fillInfoSupplementType(LoanOrderVO loanOrderVO, String taskDefinitionKey, String processInstanceId) {
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
                    loanOrderVO.setInfoSupplementType((Integer) typeHistoricVariableInstance.getValue());
                }
            }
        }
    }

    /**
     * 当前任务
     *
     * @param loanOrderVO
     * @param taskDefinitionKey
     */
    private void fillCurrentTask(LoanOrderVO loanOrderVO, String taskDefinitionKey) {
        String currentTask = LoanProcessEnum.getNameByCode(taskDefinitionKey);
        loanOrderVO.setCurrentTask(currentTask);
    }

    /**
     * TODO 还款状态： 1-正常还款;  2-非正常还款;  3-已结清;
     *
     * @param loanOrderVO
     * @param taskDefinitionKey
     * @param processInstanceId
     */
    private void fillRepayStatus(LoanOrderVO loanOrderVO, String taskDefinitionKey, String processInstanceId) {
        loanOrderVO.setRepayStatus(1);
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
     * @param loanOrderVO
     * @param processInstanceId
     * @return
     */
    private void fillMsg(LoanOrderVO loanOrderVO, String processInstanceId) {
        // 业务单
        LoanOrderDO loanOrderDO = loanOrderDOMapper.getByProcessInstId(processInstanceId);
        if (null == loanOrderDO) {
            return;
        }

        // 订单基本信息
        BeanUtils.copyProperties(loanOrderDO, loanOrderVO);
        loanOrderVO.setId(String.valueOf(loanOrderDO.getId()));

        // 关联的-客户信息(主贷人)
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
        if (null != loanCustomerDO) {
            BaseVO customer = new BaseVO();
            BeanUtils.copyProperties(loanCustomerDO, customer);
            loanOrderVO.setCustomer(customer);
            loanOrderVO.setIdCard(loanCustomerDO.getIdCard());
            loanOrderVO.setMobile(loanCustomerDO.getMobile());
        }

        // 关联的-贷款基本信息
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        if (null != loanBaseInfoDO) {
            // 业务员
            EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanBaseInfoDO.getSalesmanId(), null);
            if (null != employeeDO) {
                BaseVO salesman = new BaseVO();
                BeanUtils.copyProperties(employeeDO, salesman);
                loanOrderVO.setSalesman(salesman);
            }
            // 合伙人
            PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(), null);
            if (null != partnerDO) {
                BaseVO partner = new BaseVO();
                BeanUtils.copyProperties(partnerDO, partner);
                loanOrderVO.setPartner(partner);
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
    private ResultBean<Long> createLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam) {
        Preconditions.checkNotNull(loanHomeVisitParam.getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(loanHomeVisitParam.getCustomerId(), "客户ID不能为空");

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
        loanOrderDO.setLoanHomeVisitId(loanHomeVisitDO.getId());
        loanOrderDO.setGmtModify(new Date());

        int relaCount = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        Preconditions.checkArgument(relaCount > 0, "关联上门家访资料失败");

        // 文件保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(loanHomeVisitParam.getCustomerId(), loanHomeVisitParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        return ResultBean.ofSuccess(loanHomeVisitDO.getId(), "保存上门家访资料成功");
    }

    /**
     * 编辑上门家访资料
     *
     * @param loanHomeVisitParam
     */
    private ResultBean<Long> updateLoanHomeVisit(LoanHomeVisitParam loanHomeVisitParam) {
        LoanHomeVisitDO loanHomeVisitDO = new LoanHomeVisitDO();
        BeanUtils.copyProperties(loanHomeVisitParam, loanHomeVisitDO);
        loanHomeVisitDO.setGmtModify(new Date());

        int count = loanHomeVisitDOMapper.updateByPrimaryKeySelective(loanHomeVisitDO);
        Preconditions.checkArgument(count > 0, "编辑上门家访资料失败");

        // 文件保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(loanHomeVisitParam.getCustomerId(), loanHomeVisitParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        return ResultBean.ofSuccess(null, "保存上门家访资料成功");
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

    /**
     * 车型回填
     *
     * @param carDetailId
     * @param loanCarInfoVO
     */
    private void fillCascadeCarDetail(Long carDetailId, LoanCarInfoVO loanCarInfoVO) {
        if (null != carDetailId) {

            CarDetailDO carDetailDO = carDetailDOMapper.selectByPrimaryKey(carDetailId, null);
            if (null != carDetailDO) {
                BaseVO carDetail = new BaseVO();
                BeanUtils.copyProperties(carDetailDO, carDetail);
                // 填充车系
                fillCarModel(carDetailDO.getModelId(), Lists.newArrayList(carDetail), loanCarInfoVO);
            }
        }
    }

    /**
     * 填充车系
     *
     * @param carModelId
     * @param cascadeCarDetail
     * @param loanCarInfoVO
     */
    private void fillCarModel(Long carModelId, List<BaseVO> cascadeCarDetail, LoanCarInfoVO loanCarInfoVO) {
        if (null != carModelId) {
            CarModelDO carModelDO = carModelDOMapper.selectByPrimaryKey(carModelId, null);
            if (null != carModelDO) {
                BaseVO carModel = new BaseVO();
                BeanUtils.copyProperties(carModelDO, carModel);
                // 填充品牌
                cascadeCarDetail.add(carModel);
                fillCarBrand(carModelDO.getBrandId(), cascadeCarDetail, loanCarInfoVO);
            }
        }
    }

    /**
     * 填充品牌
     *
     * @param carBrandId
     * @param cascadeCarDetail
     * @param loanCarInfoVO
     */
    private void fillCarBrand(Long carBrandId, List<BaseVO> cascadeCarDetail, LoanCarInfoVO loanCarInfoVO) {
        if (null != carBrandId) {
            CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(carBrandId, null);
            if (null != carBrandDO) {
                BaseVO carBrand = new BaseVO();
                BeanUtils.copyProperties(carBrandDO, carBrand);
                cascadeCarDetail.add(carBrand);
            }
        }

        Collections.reverse(cascadeCarDetail);
        loanCarInfoVO.setCarDetail(cascadeCarDetail);
    }

    private void updateLoanBaseInfo(LoanBaseInfoParam loanBaseInfoParam) {
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(loanBaseInfoParam, loanBaseInfoDO);

        ResultBean<Void> updateResult = loanBaseInfoService.update(loanBaseInfoDO);
        Preconditions.checkArgument(updateResult.getSuccess(), updateResult.getMsg());
    }

    private void updateOrInsertLoanCustomer(CreditApplyOrderParam param) {
        AllCustDetailParam allCustDetailParam = new AllCustDetailParam();
        BeanUtils.copyProperties(param, allCustDetailParam);
        ResultBean<Long> resultBean = loanCustomerService.updateAll(allCustDetailParam);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

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

            ResultBean<Void> updateFileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(customerParam.getId(), customerParam.getFiles(), UPLOAD_TYPE_NORMAL);
            Preconditions.checkArgument(updateFileResultBean.getSuccess(), updateFileResultBean.getMsg());
        }
    }

    private Long createLoanOrder(Long baseInfoId, Long customerId) {
        ResultBean<Long> createLoanOrderResult = loanProcessOrderService.createLoanOrder(baseInfoId, customerId);
        Preconditions.checkArgument(createLoanOrderResult.getSuccess(), createLoanOrderResult.getMsg());
        return createLoanOrderResult.getData();
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

        // 文件KEY列表保存
        ResultBean<Void> insertFileResultBean = loanFileService.batchInsert(createCustomerResult.getData(), customerParam.getFiles());
        Preconditions.checkArgument(insertFileResultBean.getSuccess(), insertFileResultBean.getMsg());

        // 返回客户ID
        return createCustomerResult.getData();
    }


    private Long createLoanBaseInfo(LoanBaseInfoParam loanBaseInfoParam) {
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(loanBaseInfoParam, loanBaseInfoDO);

        ResultBean<Long> resultBean = loanBaseInfoService.create(loanBaseInfoDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        return resultBean.getData();
    }

    /**
     * 封装用户信息并填充到容器
     *
     * @param customerVO
     * @param loanSimpleCustomerInfoVOS
     */
    private void fillLoanSimpleCustomerInfoVO(CustomerVO customerVO, List<LoanSimpleCustomerInfoVO> loanSimpleCustomerInfoVOS) {
        // 客户信息
        LoanSimpleCustomerInfoVO simpleCustomerInfoVO = new LoanSimpleCustomerInfoVO();

        BeanUtils.copyProperties(customerVO, simpleCustomerInfoVO);

        // 征信信息
        if (null != customerVO.getId()) {
            List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(customerVO.getId(), null);

            if (!CollectionUtils.isEmpty(loanCreditInfoDOS)) {

                loanCreditInfoDOS.parallelStream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            if (CREDIT_TYPE_BANK.equals(e.getType())) {
                                simpleCustomerInfoVO.setBankCreditResult(e.getResult());
                            } else if (CREDIT_TYPE_SOCIAL.equals(e.getType())) {
                                simpleCustomerInfoVO.setSocialCreditResult(e.getResult());
                            }
                        });
            }
        }

        //根据客户号查询上传的文件
        ResultBean<List<FileVO>> listFileResultBean = loanFileService.listByCustomerIdAndUploadType(customerVO.getId(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(listFileResultBean.getSuccess(), listFileResultBean.getMsg());

        List<FileVO> fileVOS = listFileResultBean.getData().parallelStream()
                .filter(Objects::nonNull)
                .map(e -> {
                    if (e.getUrls() == null || e.getUrls().equals("")) {
                        return null;
                    } else {
                        FileVO fileVO = new FileVO();
                        BeanUtils.copyProperties(e, fileVO);
                        return fileVO;
                    }

                }).collect(Collectors.toList());

        simpleCustomerInfoVO.setFiles(fileVOS);

        loanSimpleCustomerInfoVOS.add(simpleCustomerInfoVO);
    }

}
