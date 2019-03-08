package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.*;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanCreditExportQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.lang.Process;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.DOING_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.GuaranteeRelaConst.GUARANTOR_PERSONAL;
import static com.yunche.loan.config.constant.LoanCustomerConst.*;
import static com.yunche.loan.config.constant.LoanCustomerEnum.*;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileEnum.BANK_CREDIT_PIC;
import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
public class LoanOrderServiceImpl implements LoanOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(LoanOrderService.class);


    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private LoanBaseInfoService loanBaseInfoService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

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
    private VehicleInformationService vehicleInformationService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Autowired
    private LoanProcessLogService loanProcessLogService;

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private SecondHandCarEvaluateDOMapper secondHandCarEvaluateDOMapper;


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
        permissionService.checkTaskPermission(CREDIT_APPLY.getCode());

        // 校验客户是否在系统之前已经查过征信，如果已经查过了，则不允许再添加
        // 根据身份证号校验
        checkBankInterfaceSerial(param);

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

    /**
     * 校验征信是否查询
     *
     * @param param
     */
    private void checkBankInterfaceSerial(CreditApplyOrderParam param) {
//        //如果当前订单状态是 【打回】，则不用校验
//        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(param.getOrderId());
//        //如果是打回修改的单子则不用于校验14天
//        if(loanProcessDO!=null && loanProcessDO.getCreditApply().equals(TASK_PROCESS_REJECT)){
//            return;
//        }
        //主贷人校验
        if (param.getPrincipalLender() != null) {
            String idCard = param.getPrincipalLender().getIdCard();

            bankCredit(param.getOrderId(), idCard, param.getPrincipalLender().getName(), param.getLoanBaseInfo().getBank(), param.getPrincipalLender().getId());
        }

        //共待人校验
        if (param.getCommonLenderList() != null) {
            param.getCommonLenderList().stream().forEach(e -> {
                String idCard = e.getIdCard();
                String name = e.getName();
                bankCredit(param.getOrderId(), idCard, name, param.getLoanBaseInfo().getBank(), e.getId());

            });
        }

        //担保人校验
        if (param.getGuarantorList() != null) {
            param.getGuarantorList().stream().forEach(e -> {
                String idCard = e.getIdCard();
                String name = e.getName();

                bankCredit(param.getOrderId(), idCard, name, param.getLoanBaseInfo().getBank(), e.getId());

            });
        }

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

                    if (orderId == null || (orderId != null && !loanOrderDO.getId().equals(orderId))) {
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

        checkBankInterfaceSerial(param);
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
        CreditRecordVO creditRecordVO = loanCreditInfoService.detailAll(loanOrderDO.getLoanCustomerId(), creditType);

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
            Preconditions.checkArgument(update.getSuccess(), update.getMsg());
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
        //更新绑定
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanCarInfoParam.getOrderId());
        if (loanCarInfoParam.getCarType() == 0) {
            vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
            vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
            loanOrderDO.setSecond_hand_car_evaluate_id(null);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {

            if (loanCarInfoParam.getEvaluationType() == 2)//手工评估
            {
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());

                //更新vin码---车辆行驶证号码
                vehicleInformationUpdateParam.setVehicle_identification_number(loanCarInfoParam.getVin());

                loanOrderDO.setSecond_hand_car_evaluate_id(null);
                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

            } else if (loanCarInfoParam.getEvaluationType() == 1)//在线评估
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
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
                vehicleInformationUpdateParam.setVehicle_identification_number(secondHandCarEvaluateDO.getVin());
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());

                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            }

        }


        vehicleInformationUpdateParam.setOrder_id(loanCarInfoParam.getOrderId().toString());
        vehicleInformationUpdateParam.setApply_license_plate_area(loanCarInfoParam.getApplyLicensePlateArea());
        vehicleInformationUpdateParam.setLicense_plate_type(loanCarInfoParam.getLicensePlateType());

        vehicleInformationService.update(vehicleInformationUpdateParam);

        String s = loanCarInfoParam.getApplyLicensePlateArea();
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        loanBaseInfoDO.setAreaId(Long.valueOf(s));
        LoanBaseInfoDO loanBaseInfoDO1 = loanBaseInfoDOMapper.getTotalInfoByOrderId(loanCarInfoParam.getOrderId());
        loanBaseInfoDO.setId(loanBaseInfoDO1.getId());
        loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);

        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLoanCarInfo(LoanCarInfoParam loanCarInfoParam) {
        Preconditions.checkArgument(null != loanCarInfoParam && null != loanCarInfoParam.getId(), "车辆信息ID不能为空");
        Preconditions.checkNotNull(loanCarInfoParam.getOrderId(), "订单号不能为空");
        VehicleInformationUpdateParam vehicleInformationUpdateParam = new VehicleInformationUpdateParam();
        //更新绑定
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanCarInfoParam.getOrderId());
        if (loanCarInfoParam.getCarType() == 0) {
            vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
            vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
            loanOrderDO.setSecond_hand_car_evaluate_id(null);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {


            if (loanCarInfoParam.getEvaluationType() == 2)//手工评估
            {
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());

                //更新vin码---车辆行驶证号码
                vehicleInformationUpdateParam.setVehicle_identification_number(loanCarInfoParam.getVin());

                loanOrderDO.setSecond_hand_car_evaluate_id(null);
                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

            } else if (loanCarInfoParam.getEvaluationType() == 1)//在线评估
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
                vehicleInformationUpdateParam.setColor(loanCarInfoParam.getColor());
                vehicleInformationUpdateParam.setNow_driving_license_owner(loanCarInfoParam.getNowDrivingLicenseOwner());
                vehicleInformationUpdateParam.setVehicle_identification_number(secondHandCarEvaluateDO.getVin());

                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            } else {
                throw new BizException("估价类型有误");
            }

        }
        // convert
        LoanCarInfoDO loanCarInfoDO = new LoanCarInfoDO();
        convertLoanCarInfo(loanCarInfoParam, loanCarInfoDO);
        ResultBean<Void> resultBean = loanCarInfoService.update(loanCarInfoDO);

        vehicleInformationUpdateParam.setOrder_id(loanCarInfoParam.getOrderId().toString());
        vehicleInformationUpdateParam.setApply_license_plate_area(loanCarInfoParam.getApplyLicensePlateArea());
        vehicleInformationUpdateParam.setLicense_plate_type(loanCarInfoParam.getLicensePlateType());
        vehicleInformationService.update(vehicleInformationUpdateParam);

        String s = loanCarInfoParam.getApplyLicensePlateArea();
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        loanBaseInfoDO.setAreaId(Long.valueOf(s));
        LoanBaseInfoDO loanBaseInfoDO1 = loanBaseInfoDOMapper.getTotalInfoByOrderId(loanCarInfoParam.getOrderId());
        loanBaseInfoDO.setId(loanBaseInfoDO1.getId());
        loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);

        return resultBean;
    }


    @Override
    @Transactional
    public ResultBean<Long> createCreditRecord(CreditRecordParam creditRecordParam) {
        LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
        BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);

        Long id = loanCreditInfoService.create(loanCreditInfoDO);
        return ResultBean.ofSuccess(id, "征信结果录入成功");
    }

    @Override
    @Transactional
    public ResultBean<Long> updateCreditRecord(CreditRecordParam creditRecordParam) {
        LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
        BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);

        Long count = loanCreditInfoService.update(loanCreditInfoDO);
        return ResultBean.ofSuccess(count, "征信结果修改成功");
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
            /*ResultBean<String> fullAreaNameResult = baseAreaService.getFullAreaName(loanBaseInfoVO.getArea().getId());
            Preconditions.checkArgument(fullAreaNameResult.getSuccess(), fullAreaNameResult.getMsg());
            loanSimpleInfoVO.setArea(fullAreaNameResult.getData());*/
            loanSimpleInfoVO.setArea(loanBaseInfoVO.getArea().getName());
        }
        loanSimpleInfoVO.setBank(loanBaseInfoVO.getBank());
        if (null != loanBaseInfoVO.getPartner()) {
            loanSimpleInfoVO.setPartnerId(loanBaseInfoVO.getPartner().getId());
            loanSimpleInfoVO.setPartnerName(loanBaseInfoVO.getPartner().getName());
        }

        // 创建时间
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

            if (!CollectionUtils.isEmpty(custDetailVO.getSpecialContactList())) {
                List<CustomerVO> specialContactList = custDetailVO.getSpecialContactList();

                specialContactList.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {
                            // 封装用户信息并填充到容器
                            fillLoanSimpleCustomerInfoVO(e, loanSimpleCustomerInfoVOS);
                        });
            }

        }

        return ResultBean.ofSuccess(loanSimpleCustomerInfoVOS);
    }

    /**
     * 银行征信图片合成压缩包
     *
     * @param loanCreditExportQuery
     * @return
     */
    @Override
    public ResultBean createCreditDownreport(LoanCreditExportQuery loanCreditExportQuery) {
        OSSClient ossUnit = null;
        String resultName = null;
        String diskName = null;
        EmployeeDO loginUser = SessionUtils.getLoginUser();
        try {


            long start = System.currentTimeMillis();

            String name = loginUser.getName();

            if (IDict.K_YORN.K_YORN_NO.equals(loanCreditExportQuery.getIsForce())) {
                List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(loginUser.getId(), BANK_CREDIT_PIC.getType(), UPLOAD_TYPE_NORMAL);
                if (!CollectionUtils.isEmpty(loanFileDOS)) {
                    LoanFileDO loanFileDO = loanFileDOS.get(0);
                    if (null != loanFileDO) {
                        String path = loanFileDO.getPath();
                        List<String> url = JSON.parseArray(path, String.class);

                        loanFileDOMapper.deleteByPrimaryKey(loanFileDO.getId());
                        if (!CollectionUtils.isEmpty(url)) {
                            return ResultBean.ofSuccess(url.get(0));
                        } else {
                            throw new BizException("网络异常，请稍后重试");
                        }
                    }
                }

            }


            diskName = name + DateUtil.getTime();//图片存放的文件夹名称
            final String localPath = "/tmp/" + diskName;//文件夹绝对路径

            ossUnit = OSSUnit.getOSSClient();
            //查询符合要求的数据
            List<CreditPicExportVO> creditPicExportVOS = loanStatementDOMapper.selectCreditPicExport(loanCreditExportQuery);
            if (CollectionUtils.isEmpty(creditPicExportVOS)) {
                return ResultBean.ofError("筛选条件查询记录为空");
            }

            //先将文件状态改为进行中
            List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(loginUser.getId(), BANK_CREDIT_PIC.getType(), UPLOAD_TYPE_NORMAL);
            if (CollectionUtils.isEmpty(loanFileDOS)) {
                LoanFileDO loanFileDO = new LoanFileDO();
                loanFileDO.setStatus(DOING_STATUS);
                loanFileDO.setUploadType(UPLOAD_TYPE_NORMAL);
                loanFileDO.setType(BANK_CREDIT_PIC.getType());
                loanFileDO.setCustomerId(loginUser.getId());
                loanFileDOMapper.insertSelective(loanFileDO);
            } else {
                loanFileDOS.parallelStream().forEach(e -> {
                    e.setStatus(DOING_STATUS);
                    loanFileDOMapper.updateByPrimaryKeySelective(e);
                });
            }

            resultName = diskName + ".tar.gz";//压缩包文件名

            RuntimeUtils.exe("mkdir " + localPath);
            LOG.info("图片合成 开始时间：" + start);
            creditPicExportVOS.stream().filter(Objects::nonNull).forEach(e -> {
                //查图片
                Set types = Sets.newHashSet();
                //1:合成身份证图片 , 2:合成图片
                if ("1".equals(loanCreditExportQuery.getMergeFlag())) {
                    types.add(new Byte("2"));
                    types.add(new Byte("3"));
                } else {
                    types.add(new Byte("2"));
                    types.add(new Byte("3"));
                    types.add(new Byte("4"));
                    types.add(new Byte("5"));
                }
                String fileName = e.getOrderId() + e.getCustomerName() + e.getIdCard() + IDict.K_SUFFIX.K_SUFFIX_JPG;

                List<UniversalMaterialRecordVO> list = loanQueryDOMapper.selectUniversalCustomerFiles(e.getLoanCustomerId(), types);
                List<String> urls = Lists.newLinkedList();
                for (UniversalMaterialRecordVO V : list) {
                    urls.addAll(V.getUrls());
                }
                try {
                    ImageUtil.mergetImage2PicByConvert(localPath + File.separator, fileName, urls);
                    LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(e.getLoanCustomerId(), VALID_STATUS);
                    if (loanCustomerDO != null) {
                        loanCustomerDO.setCreditExpFlag(IDict.K_CREDIT_PIC_EXP.K_SUFFIX_JPG_YES);
                        loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
                    }
                } catch (Exception ex) {
                    LOG.info(e.getCustomerName() + "：图片合成失败[" + fileName + "]");
                }
            });

            Process exec = Runtime.getRuntime().exec("tar -cPf " + "/tmp/" + resultName + " " + localPath);
            exec.waitFor();
            if (exec.exitValue() != 0) {
                throw new BizException("压缩文件出错啦");
            }

            File file = new File("/tmp/" + resultName);


            OSSUnit.uploadObject2OSS(ossUnit, file, ossConfig.getBucketName(), ossConfig.getDownLoadDiskName() + File.separator);
            long end = System.currentTimeMillis();
            LOG.info("图片合成 结束时间：" + end);
            LOG.info("总用时：" + (end - start) / 1000);

            LOG.info("打包结束啦啦啦啦啦啦啦");

        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

        saveToLoanFile(loginUser.getId(), ossConfig.getDownLoadDiskName() + File.separator + resultName);
        return ResultBean.ofSuccess(ossConfig.getDownLoadDiskName() + File.separator + resultName);
    }

    /**
     * 检测图片是否合并完成
     *
     * @return
     */
    @Override
    public ResultBean picCheck() {
        MaterialDownloadParam materialDownloadParam = new MaterialDownloadParam();
        Long aLong = SessionUtils.getLoginUser().getId();
        // 是否已经存在文件了
        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(aLong, BANK_CREDIT_PIC.getType(), UPLOAD_TYPE_NORMAL);
        if (CollectionUtils.isEmpty(loanFileDOS)) {
            materialDownloadParam.setFileStatus("2");//文件不存在,需要强制重新打包
        } else {
            materialDownloadParam.setFileStatus("1");//文件处理中
            loanFileDOS.stream().filter(Objects::nonNull).forEach(e -> {
                if (e.getStatus() != null && e.getStatus().equals(BaseConst.VALID_STATUS)) {
                    materialDownloadParam.setFileStatus("0");//文件已经打包完成
                }
            });
        }
        return ResultBean.ofSuccess(materialDownloadParam);
    }

    @Override
    public RecombinationVO<UniversalInfoVO> newCreditRecordDetail(Long orderId) {
        Assert.notNull(orderId, "订单号不能为空");

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        List<UniversalCreditInfoVO> credits = loanQueryDOMapper.selectUniversalCreditInfo(orderId);
        for (UniversalCreditInfoVO universalCreditInfoVO : credits) {
            if (!StringUtils.isBlank(universalCreditInfoVO.getCustomer_id())) {
                universalCreditInfoVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderIdByCustomerId(orderId, Long.valueOf(universalCreditInfoVO.getCustomer_id())));
            }
        }

        RecombinationVO<UniversalInfoVO> recombinationVO = new RecombinationVO<>();
        recombinationVO.setCustomers(customers);
        recombinationVO.setCredits(credits);
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));

        return recombinationVO;
    }

    /**
     * zip包路径，存储到loan_file表
     *
     * @param customerId
     * @param path
     */
    private void saveToLoanFile(Long customerId, String path) {

        //先将文件状态改为进行中
        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, BANK_CREDIT_PIC.getType(), UPLOAD_TYPE_NORMAL);

        loanFileDOS.parallelStream().filter(Objects::nonNull).forEach(e -> {
            LOG.info("");
            e.setCustomerId(customerId);
            e.setUploadType(UPLOAD_TYPE_NORMAL);
            String s = JSON.toJSONString(path);
            e.setPath("[" + s + "]");
            e.setType(BANK_CREDIT_PIC.getType());
            e.setStatus(VALID_STATUS);
            e.setGmtCreate(new Date());
            loanFileDOMapper.updateByPrimaryKeySelective(e);
        });
    }

    @Override
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");

        LoanCarInfoVO loanCarInfoVO = new LoanCarInfoVO();

        Long loanCarInfoId = loanOrderDOMapper.getLoanCarInfoIdById(orderId);

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
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        VehicleInformationDO vehicleInformationDO = vehicleInformationDOMapper.selectByPrimaryKey(vid);

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (vehicleInformationDO != null) {
            loanCarInfoVO.setNowDrivingLicenseOwner(vehicleInformationDO.getNow_driving_license_owner());
            loanCarInfoVO.setLicensePlateType(vehicleInformationDO.getLicense_plate_type() == null ? null : vehicleInformationDO.getLicense_plate_type().toString());
            loanCarInfoVO.setColor(vehicleInformationDO.getColor());
            loanCarInfoVO.setVehicleCarCategory(vehicleInformationDO.getCar_category());
        }
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
        //增加业务员
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        if (loanOrderDO.getSecond_hand_car_evaluate_id() != null && !"".equals(loanOrderDO.getSecond_hand_car_evaluate_id())
                && loanCarInfoDO != null && loanCarInfoDO.getEvaluationType().equals(new Byte("1"))) {
            SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanOrderDO.getSecond_hand_car_evaluate_id());
            if (secondHandCarEvaluateDO != null) {
                loanCarInfoVO.setVin(secondHandCarEvaluateDO.getVin());
                loanCarInfoVO.setSecond_hand_car_evaluate_id(loanOrderDO.getSecond_hand_car_evaluate_id());
                loanCarInfoVO.setMileage(secondHandCarEvaluateDO.getMileage());
                loanCarInfoVO.setHasCityName(secondHandCarEvaluateDO.getCity_id());
            }

        }
        if (vehicleInformationDO != null) {
            loanCarInfoVO.setVin(vehicleInformationDO.getVehicle_identification_number());
        }


        if (loanCarInfoDO != null) {
            //人工
            if (new Byte("2").equals(loanCarInfoDO.getEvaluationType())) {
                Long area_id = loanCarInfoDO.getCityId();
                if (area_id != null) {
                    LoanCarInfoVO.SecondCityArea secondCityArea = new LoanCarInfoVO.SecondCityArea();
                    BaseAreaDO county = baseAreaDOMapper.selectByPrimaryKey(area_id, null);
                    /*if (county.getLevel().toString().equals("3")) {
                        BaseAreaDO city = baseAreaDOMapper.selectByPrimaryKey(county.getParentAreaId(), null);
                        //区
                        secondCityArea.setCountyId(county.getAreaId());
                        secondCityArea.setCountyName(county.getAreaName());

                        //市
                        secondCityArea.setCityId(city.getAreaId());
                        secondCityArea.setCityName(city.getAreaName());

                        //省
                        secondCityArea.setProvinceId(city.getParentAreaId());
                        secondCityArea.setProvinceName(city.getParentAreaName());
                    } else if (county.getLevel().toString().equals("2")) {

                        secondCityArea.setCityId(county.getAreaId());
                        secondCityArea.setCountyName(county.getAreaName());
                        secondCityArea.setProvinceId(county.getParentAreaId());
                        secondCityArea.setProvinceName(county.getParentAreaName());
                    }*/

                    //市
                    secondCityArea.setCityId(county.getAreaId());
                    secondCityArea.setCityName(county.getAreaName());

                    //省
                    secondCityArea.setProvinceId(county.getParentAreaId());
                    secondCityArea.setProvinceName(county.getParentAreaName());

                    //判断是否是直辖市
                    if (new Long("100000000000").equals(county.getParentAreaId()))
                    {
                        secondCityArea.setProvinceName(county.getAreaName());
                        secondCityArea.setCityName(null);
                    }

                    loanCarInfoVO.setSecondCityArea(secondCityArea);
                    loanCarInfoVO.setHasCityName(county.getAreaName());
                }

            }


        }
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
        ResultBean<Void> resultBean = loanCustomerService.updateAll(allCustDetailParam);
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
        List<CustomerParam> specialContactList = param.getSpecialContactList();

        createLoanCustomerList(principalLenderId, commonLenderList);


        if (checkGuarantor(principalLenderId, guarantorList)) {
            createLoanCustomerList(principalLenderId, guarantorList);
        } else {
            Preconditions.checkArgument(false, "您选择的担保人与主担保人关系有误，请核查");
        }


        createLoanCustomerList(principalLenderId, specialContactList);
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

    /**
     * @param relaCustomerList
     * @return
     */
    private boolean checkGuarantor(Long principalLenderId, List<CustomerParam> relaCustomerList) {

        List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.listByPrincipalCustIdAndType(principalLenderId, CUST_TYPE_GUARANTOR, VALID_STATUS);

        //统计 是 银行担保 && 与担保人关系为本人的数据
        List collect = relaCustomerList.stream().filter(Objects::nonNull)
                .filter(e -> e.getGuaranteeType().equals(GUARANTEE_TYPE_BANK) && e.getGuaranteeRela().equals(String.valueOf(GUARANTOR_PERSONAL)))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(loanCustomerDOS)) {
            if (!CollectionUtils.isEmpty(collect) && collect.size() != 1) {
                return false;
            }
        } else {
            if (!CollectionUtils.isEmpty(collect)) {
                return false;
            }
        }
        return true;

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
        List<FileVO> fileVOList = loanFileService.listByCustomerIdAndUploadType(customerVO.getId(), UPLOAD_TYPE_NORMAL);

        List<FileVO> fileVOS = fileVOList.parallelStream()
                .filter(Objects::nonNull)
                .map(e -> {

                    if (CollectionUtils.isEmpty(e.getUrls())) {
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
