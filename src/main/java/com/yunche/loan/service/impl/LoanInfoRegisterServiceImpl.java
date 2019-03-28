package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.LoanInfoRegisterParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.LoanFileService;
import com.yunche.loan.service.LoanInfoRegisterService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_AREA;
import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileEnum.FACE_ONE;
import static com.yunche.loan.config.constant.LoanFileEnum.FACE_SIGNATURE;

@Service
public class LoanInfoRegisterServiceImpl implements LoanInfoRegisterService {

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Autowired
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private SecondHandCarEvaluateDOMapper secondHandCarEvaluateDOMapper;


    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private LoanFileService loanFileService;


    @Override
    public ResultBean detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "参数有误");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);

        //客户基本信息
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        //车辆详情
        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);

        //判断估价类型
        if (universalCarInfoVO!=null
                && universalCarInfoVO.getCar_type()!=null
                && !"".equals(universalCarInfoVO.getCar_type())
                && Byte.valueOf(universalCarInfoVO.getCar_type())==1) {
            if (universalCarInfoVO.getEvaluation_type() != null && universalCarInfoVO.getEvaluation_type() == 2) {
                universalCarInfoVO.setVin(universalCarInfoVO.getVehicle_vehicle_identification_number());
                BaseAreaDO county = baseAreaDOMapper.selectByPrimaryKey(universalCarInfoVO.getCar_city_id(), null);

                if(county!=null){
                    if (county.getLevel().toString().equals("3")) {
                        BaseAreaDO city = baseAreaDOMapper.selectByPrimaryKey(county.getParentAreaId(), null);
                        universalCarInfoVO.setCar_city_name(city.getParentAreaName()+city.getAreaName()+county.getAreaName());
                    } else if (county.getLevel().toString().equals("2")) {
                        universalCarInfoVO.setCar_city_name(county.getParentAreaName()+county.getAreaName());
                    }else if(county.getLevel().toString().equals("1")){
                        universalCarInfoVO.setCar_city_name(county.getAreaName());
                    }

                }

            }else{
                if(loanOrderDO.getSecond_hand_car_evaluate_id()!=null){
                    SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanOrderDO.getSecond_hand_car_evaluate_id());
                    if(secondHandCarEvaluateDO!=null){

                        if(secondHandCarEvaluateDO.getArea_id()!=null){
                            BaseAreaDO county = baseAreaDOMapper.selectByPrimaryKey(secondHandCarEvaluateDO.getArea_id(), null);
                            if(county!=null){
                                if (county.getLevel().toString().equals("3")) {
                                    BaseAreaDO city = baseAreaDOMapper.selectByPrimaryKey(county.getParentAreaId(), null);
                                    universalCarInfoVO.setCar_city_name(city.getParentAreaName()+city.getAreaName()+county.getAreaName());
                                } else if (county.getLevel().toString().equals("2")) {
                                    universalCarInfoVO.setCar_city_name(county.getParentAreaName()+county.getAreaName());
                                }else if(county.getLevel().toString().equals("1")){
                                    universalCarInfoVO.setCar_city_name(county.getAreaName());
                                }
                            }
                        }
                        universalCarInfoVO.setMileage(secondHandCarEvaluateDO.getMileage());
                    }
                }
            }

        }
        //金融方案信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);




        RecombinationVO recombinationVO = new RecombinationVO<>();

        //设置城市id--用于判断是否是台州
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.parseLong(universalInfoVO.getBase_apply_license_plate_area_id()), null);
        if (null != baseAreaDO) {
            if(LEVEL_AREA.equals(baseAreaDO.getLevel())){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                universalInfoVO.setCityId(cityDO.getAreaId());
            }
            if (LEVEL_CITY.equals(baseAreaDO.getLevel())) {
                BaseAreaDO parentAreaDO = baseAreaDOMapper.selectByPrimaryKey(baseAreaDO.getParentAreaId(), null);
                if (null != parentAreaDO) {
                    universalInfoVO.setCityId(baseAreaDO.getAreaId());
                }
            }
        }

//根据客户号查询上传的文件
        List<FileVO> fileVOList = loanFileService.listByCustomerIdAndUploadType(loanOrderDO.getLoanCustomerId(), UPLOAD_TYPE_NORMAL);

        List<FileVO> fileVOS = fileVOList.stream()
                .filter(Objects::nonNull)
                .filter(f ->FACE_SIGNATURE.equals(f.getType()))
                .map(e -> {

                    if (CollectionUtils.isEmpty(e.getUrls())) {
                        return null;
                    } else {
                        FileVO fileVO = new FileVO();
                        BeanUtils.copyProperties(e, fileVO);
                        return fileVO;
                    }

                }).collect(Collectors.toList());

        recombinationVO.setFiles(fileVOS);
        recombinationVO.setInfo(universalInfoVO);

        recombinationVO.setCar(universalCarInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);

        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 贷款信息注册 更新
     *
     * @param loanInfoRegisterParam
     * @return
     */
    @Override
    @Transactional
    public ResultBean update(LoanInfoRegisterParam loanInfoRegisterParam) {
        Preconditions.checkNotNull(loanInfoRegisterParam, "参数有误");
        Preconditions.checkNotNull(loanInfoRegisterParam.getOrderId(), "业务单号未传");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanInfoRegisterParam.getOrderId());
        Preconditions.checkNotNull(loanOrderDO, "订单信息不存在");
        VehicleInformationDO vehicleInformationDO = new VehicleInformationDO();
        //更新vin码绑定
            if (loanInfoRegisterParam.getCarType() ==0 )
            {
                loanOrderDO.setSecond_hand_car_evaluate_id(null);
                loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
                vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());
            }else {

                if (loanInfoRegisterParam.getEvaluationType()==2)//手工评估
                {
                    vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());

                    //更新vin码---车辆行驶证号码
                    vehicleInformationDO.setVehicle_identification_number(loanInfoRegisterParam.getVin());

                    loanOrderDO.setSecond_hand_car_evaluate_id(null);
                    loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

                }else if(loanInfoRegisterParam.getEvaluationType()==1)//在线评估
                {
                    SecondHandCarEvaluateDO secondHandCarEvaluateDO = secondHandCarEvaluateDOMapper.selectByPrimaryKey(loanInfoRegisterParam.getSecond_hand_car_evaluate_id());

                    // #车牌号码 #车辆类型（小型轿车）  #所有人名称  #发动机号码  #注册日期   #车型颜色
                    //vehicleInformationDO.setLicense_plate_number(secondHandCarEvaluateDO.getPlate_num());
                    vehicleInformationDO.setCar_category(secondHandCarEvaluateDO.getVehicle_type());
                    //vehicleInformationDO.setNow_driving_license_owner(secondHandCarEvaluateDO.getOwner());
                    vehicleInformationDO.setEngine_number(secondHandCarEvaluateDO.getEngine_num());
                    vehicleInformationDO.setRegister_date(secondHandCarEvaluateDO.getRegister_date());
                    /*vehicleInformationDO.setColor(secondHandCarEvaluateDO.getStyle_color());*/
                    vehicleInformationDO.setColor(loanInfoRegisterParam.getColor());
                    vehicleInformationDO.setVehicle_identification_number(secondHandCarEvaluateDO.getVin());
                    loanOrderDO.setSecond_hand_car_evaluate_id(loanInfoRegisterParam.getSecond_hand_car_evaluate_id());
                }else{
                    throw new BizException("估价类型有误");
                }

            }

            //更新
        loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

        // loanCarInfo
        Long loanCarInfoId = loanOrderDO.getLoanCarInfoId();

        LoanCarInfoDO carInfoDO = new LoanCarInfoDO();
        carInfoDO.setVehicleProperty(loanInfoRegisterParam.getVehicleProperty());
        carInfoDO.setCarType(loanInfoRegisterParam.getCarType());
        carInfoDO.setEvaluationType(loanInfoRegisterParam.getEvaluationType());
        carInfoDO.setCarDetailId(loanInfoRegisterParam.getCarDetail().getId());
        carInfoDO.setCarDetailName(loanInfoRegisterParam.getCarDetail().getName());

        carInfoDO.setFirstRegisterDate(loanInfoRegisterParam.getFirstRegisterDate());
        carInfoDO.setMileage(loanInfoRegisterParam.getMileage());
        carInfoDO.setCityId(loanInfoRegisterParam.getCityId());
        carInfoDO.setVin(loanInfoRegisterParam.getVin());

        if (null != loanCarInfoId) {

            // update
            carInfoDO.setId(loanCarInfoId);
            carInfoDO.setGmtModify(new Date());
            int count = loanCarInfoDOMapper.updateByPrimaryKeySelective(carInfoDO);
            Preconditions.checkArgument(count > 0, "保存车辆信息失败");

        } else {

            // insert
            Long loanBaseInfoId = loanOrderDO.getLoanBaseInfoId();
            LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
            Long partnerId = loanBaseInfoDO.getPartnerId();
            carInfoDO.setPartnerId(partnerId);
            carInfoDO.setGmtCreate(new Date());
            carInfoDO.setGmtModify(new Date());

            int count = loanCarInfoDOMapper.insertSelective(carInfoDO);
            Preconditions.checkArgument(count > 0, "保存车辆信息失败");

            loanOrderDO.setLoanCarInfoId(carInfoDO.getId());
            loanOrderDO.setGmtModify(new Date());
            int count2 = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count2 > 0, "保存车辆信息失败");
        }


        // vehicleInformation
        Long vehicleInformationId = loanOrderDO.getVehicleInformationId();

        vehicleInformationDO.setId(vehicleInformationId);


        if (null != vehicleInformationId) {

            // update
            vehicleInformationDO.setId(vehicleInformationId);
            int count = vehicleInformationDOMapper.updateByPrimaryKeySelective(vehicleInformationDO);
            Preconditions.checkArgument(count > 0, "保存提车资料失败");

        } else {

            // insert
            int count1 = vehicleInformationDOMapper.insertSelective(vehicleInformationDO);
            Preconditions.checkArgument(count1 > 0, "保存提车资料失败");

            loanOrderDO.setVehicleInformationId(vehicleInformationDO.getId());
            loanOrderDO.setGmtModify(new Date());
            int count2 = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count2 > 0, "保存提车资料失败");
        }


        // loanFinancialPlan
        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();

        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        BeanUtils.copyProperties(loanInfoRegisterParam.getLoanFinancialPlanParam(), loanFinancialPlanDO);

        if (null != loanFinancialPlanId) {

            // update
            loanFinancialPlanDO.setId(loanFinancialPlanId);
            loanFinancialPlanDO.setGmtModify(new Date());
            int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
            Preconditions.checkArgument(count > 0, "插入金融方案计划失败");

        } else {

            // insert
            loanFinancialPlanDO.setGmtCreate(new Date());
            loanFinancialPlanDO.setGmtModify(new Date());
            int count = loanFinancialPlanDOMapper.insertSelective(loanFinancialPlanDO);
            Preconditions.checkArgument(count > 0, "插入金融方案计划失败");

            loanOrderDO.setLoanFinancialPlanId(loanFinancialPlanDO.getId());
            loanOrderDO.setGmtModify(new Date());
            int count2 = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
            Preconditions.checkArgument(count2 > 0, "插入金融方案计划失败");
        }
        if(loanInfoRegisterParam.getWorkCompanyName() !=null){
            Long cusId = loanOrderDO.getLoanCustomerId();
            LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
            loanCustomerDO.setId(cusId);
            if(loanInfoRegisterParam.getMonthIncome() !=null && !"".equals(loanInfoRegisterParam.getMonthIncome())){
                loanCustomerDO.setMonthIncome(new BigDecimal(loanInfoRegisterParam.getMonthIncome()));
            }
            loanCustomerDO.setIncomeCertificateCompanyName(loanInfoRegisterParam.getWorkCompanyName());
            loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
        }


        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(loanOrderDO.getLoanCustomerId(), loanInfoRegisterParam.getFiles(), UPLOAD_TYPE_NORMAL);
         Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

        return ResultBean.ofSuccess(null, "保存成功");
    }
}
