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

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CustomerConst.CREDIT_TYPE_BANK;
import static com.yunche.loan.config.constant.CustomerConst.CREDIT_TYPE_SOCIAL;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_SUPPLEMENT;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.INFO_SUPPLEMENT;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_ACTION;
import static com.yunche.loan.config.constant.LoanProcessVariableConst.PROCESS_VARIABLE_INFO_SUPPLEMENT_TYPE;
import static com.yunche.loan.config.constant.MultipartTypeConst.MULTIPART_TYPE_CUSTOMER_LOAN_DONE;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
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
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private LoanInfoSupplementDOMapper loanInfoSupplementDOMapper;

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
    private LoanFinancialPlanService loanFinancialPlanService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private VehicleInformationService vehicleInformationService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PartnerRelaAreaDOMapper partnerRelaAreaDOMapper;


    @Override
    public ResultBean<List<LoanOrderVO>> query(LoanOrderQuery query) {
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
                List<LoanOrderVO> loanOrderVOList = tasks.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstanceId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                LoanOrderVO loanOrderVO = new LoanOrderVO();
                                // 填充订单信息
                                fillOrderMsg(e, loanOrderVO, processInstanceId, query.getTaskDefinitionKey(), query.getTaskStatus(), query.getMultipartType());
                                return loanOrderVO;
                            }

                            return null;
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(LoanOrderVO::getGmtCreate).reversed())
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(loanOrderVOList, (int) totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, (int) totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<List<LoanOrderVO>> multipartQuery(LoanOrderQuery query) {
        Preconditions.checkNotNull(query.getMultipartType(), "多节点查询类型不能为空");

        int totalNum = loanOrderDOMapper.countMultipartQuery(query);
        if (totalNum > 0) {

            List<LoanOrderDO> loanOrderDOList = loanOrderDOMapper.listMultipartQuery(query);
            if (!CollectionUtils.isEmpty(loanOrderDOList)) {

                List<LoanOrderVO> loanOrderVOList = loanOrderDOList.parallelStream()
                        .filter(Objects::nonNull)
                        .map(e -> {

                            // 流程实例ID
                            String processInstanceId = e.getProcessInstId();
                            if (StringUtils.isNotBlank(processInstanceId)) {
                                // 业务单
                                LoanOrderVO loanOrderVO = new LoanOrderVO();
                                // 填充订单信息
                                fillOrderMsg(null, loanOrderVO, processInstanceId, null, query.getTaskStatus(), null);
                                return loanOrderVO;
                            }

                            return null;
                        })
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(LoanOrderVO::getGmtCreate).reversed())
                        .collect(Collectors.toList());

                return ResultBean.ofSuccess(loanOrderVOList, totalNum, query.getPageIndex(), query.getPageSize());
            }
        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, totalNum, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
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

        // 创建贷款基本信息
        Long baseInfoId = createLoanBaseInfo(param.getLoanBaseInfo());

        // 创建客户信息
        Long customerId = createLoanCustomer(param);

        // 创建订单
        Long orderId = createLoanOrder(baseInfoId, customerId);

        return ResultBean.ofSuccess(String.valueOf(orderId));
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

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
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

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        ResultBean<CustomerVO> customerVOResultBean = loanCustomerService.getById(loanOrderDO.getLoanCustomerId());
        Preconditions.checkArgument(customerVOResultBean.getSuccess(), customerVOResultBean.getMsg());
        CustomerVO customerVO = customerVOResultBean.getData();

        LoanSimpleInfoVO loanSimpleInfoVO = new LoanSimpleInfoVO();
        loanSimpleInfoVO.setCustomerId(customerVO.getId());
        loanSimpleInfoVO.setCustomerName(customerVO.getName());
        loanSimpleInfoVO.setIdCard(customerVO.getIdCard());
        loanSimpleInfoVO.setMobile(customerVO.getMobile());

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

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
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
    public ResultBean<InfoSupplementVO> infoSupplementDetail(Long supplementOrderId) {
        Preconditions.checkNotNull(supplementOrderId, "增补单不能为空");

        LoanInfoSupplementDO loanInfoSupplementDO = loanInfoSupplementDOMapper.selectByPrimaryKey(supplementOrderId);
        Preconditions.checkNotNull(loanInfoSupplementDO, "增补单不存在");

        InfoSupplementVO infoSupplementVO = new InfoSupplementVO();

        // 增补信息
        infoSupplementVO.setSupplementOrderId(supplementOrderId);
        infoSupplementVO.setSupplementType(loanInfoSupplementDO.getType());
        infoSupplementVO.setSupplementTypeText(getSupplementTypeText(loanInfoSupplementDO.getType()));
        infoSupplementVO.setSupplementInfo(loanInfoSupplementDO.getInfo());
        infoSupplementVO.setSupplementContent(loanInfoSupplementDO.getContent());
        infoSupplementVO.setSupplementStartDate(loanInfoSupplementDO.getStartTime());
        infoSupplementVO.setRemark(loanInfoSupplementDO.getRemark());

        Long orderId = loanInfoSupplementDO.getOrderId();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单号不存在");

        // 客户信息
        if (null != loanOrderDO.getLoanCustomerId()) {
            ResultBean<CustomerVO> customerVOResultBean = loanCustomerService.getById(loanOrderDO.getLoanCustomerId());
            Preconditions.checkArgument(customerVOResultBean.getSuccess(), customerVOResultBean.getMsg());
            CustomerVO customerVO = customerVOResultBean.getData();

            infoSupplementVO.setOrderId(String.valueOf(orderId));
            infoSupplementVO.setCustomerId(customerVO.getId());
            infoSupplementVO.setCustomerName(customerVO.getName());
            infoSupplementVO.setIdCard(customerVO.getIdCard());
        }

        // 业务员信息
        ResultBean<LoanBaseInfoVO> loanBaseInfoResultBean = loanBaseInfoService.getLoanBaseInfoById(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkArgument(loanBaseInfoResultBean.getSuccess(), loanBaseInfoResultBean.getMsg());
        LoanBaseInfoVO loanBaseInfoVO = loanBaseInfoResultBean.getData();
        if (null != loanBaseInfoVO) {
            BaseVO salesman = loanBaseInfoVO.getSalesman();
            if (null != salesman) {
                infoSupplementVO.setSalesmanId(salesman.getId());
                infoSupplementVO.setSalesmanName(salesman.getName());
            }
            BaseVO partner = loanBaseInfoVO.getPartner();
            if (null != partner) {
                infoSupplementVO.setPartnerId(partner.getId());
                infoSupplementVO.setPartnerName(partner.getName());
            }
        }

        // 客户及文件分类列表
        fillCustomerAndFile(infoSupplementVO, orderId);

        return ResultBean.ofSuccess(infoSupplementVO);
    }


    @Override
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanCarInfoVO loanCarInfoVO = new LoanCarInfoVO();

        Long loanCarInfoId = loanOrderDOMapper.getLoanCarInfoIdById(orderId);

        Long loanBaseInfoId = loanOrderDOMapper.selectByPrimaryKey(orderId, null).getLoanBaseInfoId();

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
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.valueOf(vehicleInformationDO.getApply_license_plate_area()), VALID_STATUS);
            loanCarInfoVO.setHasApplyLicensePlateArea(baseAreaDO);
            String tmpApplyLicensePlateArea=null;
            if(baseAreaDO!=null){
                if(baseAreaDO.getParentAreaName()!=null){
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName()+" "+baseAreaDO.getAreaName();
                }else{
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
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

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, null);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");

        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanOrderDO.getLoanHomeVisitId());
        LoanHomeVisitVO loanHomeVisitVO = new LoanHomeVisitVO();
        if (null != loanHomeVisitDO) {
            BeanUtils.copyProperties(loanHomeVisitDO, loanHomeVisitVO);
        }

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {

            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);

            // 主贷人 文件回填
            if ("1".equals(universalCustomerVO.getCust_type())) {

                if (!CollectionUtils.isEmpty(files)) {

                    List<FileVO> homeVisitFiles = Lists.newArrayList();

                    // 12-合影照片;13-家访视频; 16-家访照片; 17-车辆照片;18-其他资料;
                    files.stream()
                            .filter(e -> "12".equals(e.getType()) || "13".equals(e.getType())
                                    || "16".equals(e.getType()) || "17".equals(e.getType())
                                    || "18".equals(e.getType()))
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


    @Override
    public ResultBean<Void> infoSupplementUpload(InfoSupplementParam infoSupplementParam) {
        Preconditions.checkNotNull(infoSupplementParam.getCustomerId(), "客户ID不能为空");
        Preconditions.checkNotNull(infoSupplementParam.getSupplementOrderId(), "增补单ID不能为空");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(infoSupplementParam.getFiles()) ||
                StringUtils.isNotBlank(infoSupplementParam.getRemark()), "资料信息或备注为空");

        Long suppermentOrderId = infoSupplementParam.getSupplementOrderId();
        String remark = infoSupplementParam.getRemark();
        LoanInfoSupplementDO loanInfoSupplementDO = new LoanInfoSupplementDO();
        loanInfoSupplementDO.setRemark(remark);
        loanInfoSupplementDO.setId(suppermentOrderId);
        int count = loanInfoSupplementDOMapper.updateByPrimaryKeySelective(loanInfoSupplementDO);
        Preconditions.checkArgument(count > 0, "增补失败");

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

    /**
     * 填充订单信息
     *
     * @param taskInfo
     * @param loanOrderVO
     * @param processInstanceId
     * @param taskDefinitionKey
     * @param taskStatus
     * @param multipartType
     */
    private void fillOrderMsg(TaskInfo taskInfo, LoanOrderVO loanOrderVO, String processInstanceId, String taskDefinitionKey,
                              Byte taskStatus, Integer multipartType) {
        // 任务状态
        if (null == taskInfo) {
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .taskDefinitionKey(taskDefinitionKey)
                    .orderByTaskCreateTime()
                    .desc()
                    .listPage(0, 1);

            if (!CollectionUtils.isEmpty(historicTaskInstanceList)) {
                taskInfo = historicTaskInstanceList.get(0);
            }
        }
        loanOrderVO.setTaskStatus(getTaskStatus(taskInfo, taskStatus));

        // 贷款客户基本信息填充
        fillBaseMsg(loanOrderVO, processInstanceId);

        // 资料增补类型
        if (INFO_SUPPLEMENT.getCode().equals(taskDefinitionKey)) {
            fillInfoSupplementType(loanOrderVO, taskDefinitionKey, processInstanceId);
        }

        // 打回 OR 未提交
        fillTaskTypeText(loanOrderVO, processInstanceId);

        if (null != multipartType) {
            // 当前任务节点
            fillCurrentTask(loanOrderVO, taskDefinitionKey);

            // 还款状态
            if (MULTIPART_TYPE_CUSTOMER_LOAN_DONE.equals(multipartType)) {
                fillRepayStatus(loanOrderVO, taskDefinitionKey, processInstanceId);
            }
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
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(loanOrderVO.getId()), null);
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

    /**
     * 填充客户及文件信息
     *
     * @param infoSupplementVO
     * @param orderId
     */
    private void fillCustomerAndFile(InfoSupplementVO infoSupplementVO, Long orderId) {

        ResultBean<CustDetailVO> custDetailVOResultBean = loanCustomerService.detailAll(orderId, UPLOAD_TYPE_SUPPLEMENT);
        Preconditions.checkArgument(custDetailVOResultBean.getSuccess(), custDetailVOResultBean.getMsg());

        CustDetailVO custDetailVO = custDetailVOResultBean.getData();
        if (null != custDetailVO) {

            CustomerVO principalLenderVO = custDetailVO.getPrincipalLender();
//            List<CustomerVO> commonLenderVOList = custDetailVO.getCommonLenderList();
//            List<CustomerVO> guarantorVOList = custDetailVO.getGuarantorList();
//            List<CustomerVO> emergencyContactVOList = custDetailVO.getEmergencyContactList();

            if (null != principalLenderVO) {
                InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
                fillCustomerFile(principalLenderVO, customerFile);
                infoSupplementVO.setPrincipalLender(customerFile);
            }

//            if (!CollectionUtils.isEmpty(commonLenderVOList)) {
//
//                List<InfoSupplementVO.CustomerFile> commonLenderList = Lists.newArrayList();
//                commonLenderVOList.parallelStream()
//                        .filter(Objects::nonNull)
//                        .forEach(e -> {
//                            InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
//                            fillCustomerFile(e, customerFile);
//                            commonLenderList.add(customerFile);
//                        });
//                infoSupplementVO.setCommonLenderList(commonLenderList);
//            }
//
//            if (!CollectionUtils.isEmpty(guarantorVOList)) {
//
//                List<InfoSupplementVO.CustomerFile> guarantorList = Lists.newArrayList();
//                guarantorVOList.parallelStream()
//                        .filter(Objects::nonNull)
//                        .forEach(e -> {
//                            InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
//                            fillCustomerFile(e, customerFile);
//                            guarantorList.add(customerFile);
//                        });
//                infoSupplementVO.setGuarantorList(guarantorList);
//            }
//
//            if (!CollectionUtils.isEmpty(emergencyContactVOList)) {
//                List<InfoSupplementVO.CustomerFile> emergencyContactList = Lists.newArrayList();
//                emergencyContactVOList.parallelStream()
//                        .filter(Objects::nonNull)
//                        .forEach(e -> {
//                            InfoSupplementVO.CustomerFile customerFile = new InfoSupplementVO.CustomerFile();
//                            fillCustomerFile(e, customerFile);
//                            emergencyContactList.add(customerFile);
//                        });
//                infoSupplementVO.setEmergencyContactList(emergencyContactList);
//            }
        }
    }

    private void fillCustomerFile(CustomerVO customerVO, InfoSupplementVO.CustomerFile customerFile) {
        customerFile.setCustomerId(customerVO.getId());
        customerFile.setCustomerName(customerVO.getName());
        customerFile.setFiles(customerVO.getFiles());
    }

    /**
     * 1-电审增补;2-送银行资料缺少;3-银行退件;4-上门家访资料增补;5-费用调整;
     *
     * @param supplementType
     * @return
     */
    public static String getSupplementTypeText(Byte supplementType) {

        String supplementTypeText = null;

        switch (supplementType) {
            case 1:
                supplementTypeText = "电审增补";
                break;
            case 2:
                supplementTypeText = "送银行资料缺少";
                break;
            case 3:
                supplementTypeText = "银行退件";
                break;
            case 4:
                supplementTypeText = "上门家访资料增补";
                break;
            case 5:
                supplementTypeText = "费用调整";
                break;
        }

        return supplementTypeText;
    }
}
