package com.yunche.loan.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.AreaCache;
import com.yunche.loan.config.cache.CarCache;
import com.yunche.loan.config.cache.DictMapCache;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.*;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.client.ICBCFeignNormal;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.request.group.*;
import com.yunche.loan.config.feign.response.*;
import com.yunche.loan.config.util.*;
import com.yunche.loan.config.util.Process;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.UniversalBankInterfaceSerialVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankSolutionService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanCustomerEnum.*;


@Service
@Transactional
public class BankSolutionServiceImpl implements BankSolutionService {
    private static final Logger LOG = LoggerFactory.getLogger(BankSolutionService.class);

    @Resource
    private ViolationUtil violationUtil;

    @Resource
    private SysConfig sysConfig;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private ICBCFeignClient icbcFeignClient;

    @Resource
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Resource
    private BankDOMapper bankDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Resource
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Resource
    private CarCache carCache;

    @Resource
    private VehicleInformationDOMapper vehicleInformationDOMapper;

    @Resource
    private ProductRateDOMapper productRateDOMapper;

    @Resource
    private FinancialProductDOMapper financialProductDOMapper;

    @Resource
    private BankFileListRecordDOMapper bankFileListRecordDOMapper;

    @Resource
    private AsyncUpload asyncUpload;

    @Autowired
    private ICBCFeignNormal icbcFeignNormal;

    @Autowired
    private DictMapCache dictMapCache;

    @Autowired
    private AreaCache areaCache;

    @Resource
    private CarBrandDOMapper carBrandDOMapper;

    @Resource
    private CarModelDOMapper carModelDOMapper;

    @Resource
    private CarDetailDOMapper carDetailDOMapper;


    @Resource
    private LoanQueryService loanQueryService;



    @Resource
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    //征信自动提交
    @Override
    public void creditAutomaticCommit(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }

        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }

        //征信银行
        Long bankId  =  bankDOMapper.selectIdByName(loanBaseInfoDO.getBank());
        if(bankId == null){
            throw new BizException("贷款银行不存在");
        }


        //紧急联系人不推送
        Set types  = Sets.newHashSet();
        types.add(new Byte("1"));
        types.add(new Byte("2"));
        types.add(new Byte("3"));
        List<LoanCustomerDO> customers = loanCustomerDOMapper.selectSelfAndRelevanceCustomersByCustTypes(orderId,types);
        if(CollectionUtils.isEmpty(customers)){
            throw new BizException("贷款客户信息不存在");
        }int value = bankId.intValue();
        switch (value) {
            case 1:
                //判断当前客户贷款银行是否为杭州工行，如为杭州工行：
                ICBCBankCreditProcess(orderId,sysConfig.getHzphybrno(),customers);
                break;
            case 3:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
                ICBCBankCreditProcess(orderId,sysConfig.getTzphybrno(),customers);
                break;
            default:
                return;
        }
    }

    @Override
    public void commonBusinessApply(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }

        Long baseId  = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }

        //征信银行
        Long bankId  =  bankDOMapper.selectIdByName(loanBaseInfoDO.getBank());
        if(bankId == null){
            throw new BizException("贷款银行不存在");
        }

        int value = bankId.intValue();
        switch (value) {
            case 1:
                //判断当前客户贷款银行是否为杭州工行，如为杭州工行：
                ICBCCommonBusinessApplyProcess(bankId,orderId,sysConfig.getHzphybrno());
                break;
            case 3:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
                ICBCCommonBusinessApplyProcess(bankId,orderId,sysConfig.getTzphybrno());
                break;
            default:
                return;
        }


    }

    @Override
    public void multimediaUpload(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }

        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }

        //征信银行
        Long bankId  =  bankDOMapper.selectIdByName(loanBaseInfoDO.getBank());
        if(bankId == null){
            throw new BizException("贷款银行不存在");
        }

        int value = bankId.intValue();
        switch (value) {
            case 1:
                //判断当前客户贷款银行是否为杭州工行，如为杭州工行：
                multimediaUploadProcess(orderId,sysConfig.getHzphybrno());
                break;
            case 3:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
                multimediaUploadProcess(orderId,sysConfig.getTzphybrno());
                break;
            default:
                return;
        }

    }


    public void multimediaUploadProcess(Long orderId,String phybrno){
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }

        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }

        Long customerId = loanOrderDO.getLoanCustomerId();
        if(customerId == null){
            throw new BizException("贷款人不存在");
        }
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(customerId,new Byte("0"));
        if(loanCustomerDO == null){
            throw new BizException("贷款人不存在");
        }

        List<ICBCApiRequest.Picture> pictures =  Lists.newArrayList();

        String path = loanQueryDOMapper.selectVideoFacePath(orderId);
        if(StringUtils.isBlank(path)){
            throw new BizException("缺少面签视频");
        }

        if(StringUtils.isBlank(path)){
            throw new BizException("缺少面签视频");
        }


        String picName = GeneratorIDUtil.execute()+ImageUtil.MP4_SUFFIX;
        ICBCApiRequest.Picture picture = new ICBCApiRequest.Picture();
        picture.setPicid(MultimediaUploadEnum.VIDEO_INTERVIEW.getKey());
        picture.setPicname(picName);
        picture.setPicnote(MultimediaUploadEnum.VIDEO_INTERVIEW.getValue());
        pictures.add(picture);

        if(pictures.size() == 0 ){
            throw new BizException("缺少面签视频");
        }

        loanQueryService.checkBankInterFaceSerialStatus(customerId,IDict.K_TRANS_CODE.MULTIMEDIAUPLOAD);


        String serNo = GeneratorIDUtil.execute();
        //多媒体补偿接口
        ICBCApiRequest.MultimediaUpload multimediaUpload = new ICBCApiRequest.MultimediaUpload();
        multimediaUpload.setPlatno(sysConfig.getPlatno());
        multimediaUpload.setGuestPlatno(sysConfig.getPlatno());
        multimediaUpload.setCmpseq(serNo);
        multimediaUpload.setZoneno(loanBaseInfoDO.getAreaId() == null?null:loanBaseInfoDO.getAreaId().toString().substring(0,4));
        multimediaUpload.setPhybrno(phybrno);
        multimediaUpload.setOrderno(orderId.toString());
        multimediaUpload.setAssurerno(sysConfig.getAssurerno());
        multimediaUpload.setCmpdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        multimediaUpload.setCmptime(new SimpleDateFormat("HHmmss").format(new Date()));
        multimediaUpload.setFileNum(String.valueOf(pictures.size()));
        multimediaUpload.setPictures(pictures);
        multimediaUpload.setCustomerId(customerId.toString());
        violationUtil.violation(multimediaUpload, MultimediaUploadValidated.class);

        asyncUpload.execute(new Process() {
            @Override
            public void process() {
                String str = "yunche-videosign.oss-cn-hangzhou.aliyuncs.com";
                int x = path.indexOf(str);
                String rpath = path.substring(x+str.length()+1,path.length());
                asyncUpload.upload(serNo,MultimediaUploadEnum.VIDEO_INTERVIEW.getKey(),picName,rpath);
                icbcFeignClient.multimediaUpload(multimediaUpload);
            }
        });
    }


    public void ICBCCommonBusinessApplyProcess(Long bankId, Long orderId, String phybrno){

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        //获取数据源
        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }

        Long customerId = loanOrderDO.getLoanCustomerId();
        if(customerId == null){
            throw new BizException("贷款人不存在");
        }
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(customerId,new Byte("0"));
        if(loanCustomerDO == null){
            throw new BizException("贷款人不存在");
        }

        loanQueryService.checkBankInterFaceSerialStatus(customerId,IDict.K_TRANS_CODE.APPLYDIVIGENERAL);

        Long carId = loanOrderDO.getLoanCarInfoId();
        if(carId == null){
            throw new BizException("贷款车辆不存在");
        }
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(carId);
        if(loanCarInfoDO == null){
            throw new BizException("贷款车辆不存在");
        }
        //数据映射层
        //所贷车辆类型：1-新车; 2-二手车; 3-不限;
        if(loanCarInfoDO.getCarType() == null){
            throw new BizException("此业务类型暂时不支持");
        }
        String busitype = BusitypeEnum.getValueByKey(loanCarInfoDO.getCarType());
        if(StringUtils.isBlank(busitype)){
            throw new BizException("此业务类型暂时不支持");
        }

        Long carDetailId = loanCarInfoDO.getCarDetailId();
        if(carDetailId == null){
            throw new BizException("贷款车辆不存在");
        }
        CarDetailDO carDetailDO = carDetailDOMapper.selectByPrimaryKey(carDetailId,null);
        if(carDetailDO == null){
            throw new BizException("贷款车辆不存在");
        }

        CarModelDO carModelDO = carModelDOMapper.selectByPrimaryKey(carDetailDO.getModelId(),null);
        if(carModelDO == null){
            throw new BizException("贷款车辆不存在");
        }

        CarBrandDO carBrandDO = carBrandDOMapper.selectByPrimaryKey(carModelDO.getBrandId(),null);
        if(carBrandDO == null){
            throw new BizException("贷款车辆不存在");
        }
        String carFullName = null;
        //城站只要宝马
        if(bankId.intValue() == 1){
            carFullName = carBrandDO.getName();
        }else if(bankId.intValue() == 3){
            carFullName =  carBrandDO.getName() + carModelDO.getFullName().replace(carBrandDO.getName(),"");
        }

        if(StringUtils.isBlank(carFullName)){
            if(carBrandDO == null){
                throw new BizException("贷款车辆不存在");
            }
        }


        Long planId  = loanOrderDO.getLoanFinancialPlanId();
        if(planId == null){
            throw new BizException("此订单金融方案不存在");
        }

        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(planId);
        if(loanFinancialPlanDO == null){
            throw new BizException("此订单金融方案不存在");
        }

        if(loanFinancialPlanDO.getCarPrice() == null){
            throw new BizException("车辆价格不能为空");
        }

        if(loanFinancialPlanDO.getCarPrice().compareTo(new BigDecimal("0")) == 0){
            throw new BizException("车辆价格不能为0");
        }

        Long vehId = loanOrderDO.getVehicleInformationId();
        if(vehId == null){
            throw new BizException("车辆信息不能为空");
        }
        VehicleInformationDO vehicleInformationDO = vehicleInformationDOMapper.selectByPrimaryKey(vehId);
        if(vehicleInformationDO == null){
            throw new BizException("车辆信息不能为空");
        }

        Long proId = loanFinancialPlanDO.getFinancialProductId();
        if(proId == null){
            throw new BizException("贷款产品为空");
        }

        FinancialProductDO financialProductDO = financialProductDOMapper.selectByPrimaryKey(proId);
        if(financialProductDO == null){
            throw new BizException("贷款产品为空");
        }

        ProductRateDOKey pk = new ProductRateDOKey();
        pk.setProdId(proId);
        pk.setLoanTime(loanFinancialPlanDO.getLoanTime() == null?new Integer(0):loanFinancialPlanDO.getLoanTime());

        ProductRateDO pkDO = productRateDOMapper.selectByPrimaryKey(pk);
        if(pkDO == null){
            throw new BizException("此产品银行基准利率为空");
        }




        BigDecimal dawnPaymentMoney = new BigDecimal("0");
        if(vehicleInformationDO.getInvoice_down_payment() != null ){
            dawnPaymentMoney = vehicleInformationDO.getInvoice_down_payment();
        }

        BigDecimal bankPeriodPrincipal = new BigDecimal("0");
        if(loanFinancialPlanDO.getBankPeriodPrincipal() != null){
            bankPeriodPrincipal = loanFinancialPlanDO.getBankPeriodPrincipal();
        }

        BigDecimal loanAmount = new BigDecimal("0");
        if(loanFinancialPlanDO.getLoanAmount() != null){
            loanAmount = loanFinancialPlanDO.getLoanAmount();
        }

        String danBaoFee = (bankPeriodPrincipal.subtract(loanAmount)).stripTrailingZeros().toPlainString();


        if(loanFinancialPlanDO.getBankPeriodPrincipal() != null){
            bankPeriodPrincipal = loanFinancialPlanDO.getBankPeriodPrincipal();
        }

        Integer loanTime = new Integer(0);
        if(loanFinancialPlanDO.getLoanTime() != null){
            loanTime = loanFinancialPlanDO.getLoanTime();
        }

        BigDecimal loanTimeFee = new BigDecimal("0");
        if(loanFinancialPlanDO.getBankFee() != null){
            loanTimeFee = pkDO.getBankRate();
        }


        String paidAmt = dawnPaymentMoney.stripTrailingZeros().toPlainString();
        String amount = bankPeriodPrincipal.stripTrailingZeros().toPlainString();
        String term = loanTime.toString();
        String interest = loanTimeFee.stripTrailingZeros().toPlainString();

        String lendCard = loanCustomerDO.getLendCard();




        //封装数据
        ICBCApiRequest.ApplyDiviGeneral applyDiviGeneral = new ICBCApiRequest.ApplyDiviGeneral();
        ICBCApiRequest.ApplyDiviGeneralInfo info = new ICBCApiRequest.ApplyDiviGeneralInfo();
        ICBCApiRequest.ApplyDiviGeneralCustomer customer = new ICBCApiRequest.ApplyDiviGeneralCustomer();
        ICBCApiRequest.ApplyDiviGeneralBusi busi = new ICBCApiRequest.ApplyDiviGeneralBusi();
        ICBCApiRequest.ApplyDiviGeneralCar car = new ICBCApiRequest.ApplyDiviGeneralCar();
        ICBCApiRequest.ApplyDiviGeneralDivi divi = new ICBCApiRequest.ApplyDiviGeneralDivi();
        List<ICBCApiRequest.Picture> pictures =  Lists.newArrayList();
        //start 封装
        List<ICBCApiRequest.PicQueue> queue = Lists.newLinkedList();


        for (TermFileEnum e : TermFileEnum.values()) {
            UniversalMaterialRecordVO authSignPic = loanQueryDOMapper.getUniversalCustomerFilesByType(customerId,e.getKey());
            if(authSignPic != null){
                if(CollectionUtils.isNotEmpty(authSignPic.getUrls())){
                        for(String str : authSignPic.getUrls()){
                            if(StringUtils.isNotBlank(str)){
                                String picName = GeneratorIDUtil.execute();
                                if(TermFileEnum.OTHER_ZIP.getKey().toString().equals(e.getKey().toString())){
                                    //zip
                                    picName = picName +ImageUtil.ZIP_SUFFIX;
                                }else if(TermFileEnum.VIDEO_INTERVIEW.getKey().toString().equals(e.getKey().toString())){
                                    //mp4
                                    picName = picName +ImageUtil.MP4_SUFFIX;
                                }else{
                                    //jpg
                                    picName = picName+ImageUtil.PIC_SUFFIX;
                                }

                                if(Integer.valueOf(e.getKey()).intValue()<59){
                                    ICBCApiRequest.Picture picture = new ICBCApiRequest.Picture();
                                    picture.setPicid(e.getValue());
                                    picture.setPicname(picName);
                                    picture.setPicnote(LoanFileEnum.getNameByCode(e.getKey()));
                                    pictures.add(picture);
                                }
                                ICBCApiRequest.PicQueue picQueue = new ICBCApiRequest.PicQueue();
                                picQueue.setPicId(e.getValue());
                                picQueue.setPicName(picName);
                                picQueue.setUrl(str);
                                queue.add(picQueue);
                            }
                        }
                    }
            }

        }

        if(CollectionUtils.isEmpty(pictures)){
            UniversalMaterialRecordVO authSignPic = loanQueryDOMapper.getUniversalCustomerFilesByType(customerId,new Byte("2"));
            if(authSignPic == null){
                throw new BizException("最少需要一张图片");
            }

            if(CollectionUtils.isEmpty(authSignPic.getUrls())){
                throw new BizException("最少需要一张图片");
            }
            if(StringUtils.isBlank(authSignPic.getUrls().get(0))){
                throw new BizException("最少需要一张图片");
            }

            ICBCApiRequest.Picture picture = new ICBCApiRequest.Picture();
            picture.setPicid(TermFileEnum.SELF_CERTIFICATE_FRONT.getValue());
            String picName = GeneratorIDUtil.execute();
            picture.setPicname(picName);
            picture.setPicnote(LoanFileEnum.getNameByCode(TermFileEnum.SELF_CERTIFICATE_FRONT.getKey()));
            pictures.add(picture);

            ICBCApiRequest.PicQueue picQueue = new ICBCApiRequest.PicQueue();
            picQueue.setPicId(TermFileEnum.SELF_CERTIFICATE_FRONT.getValue());
            picQueue.setPicName(picName);
            picQueue.setUrl(authSignPic.getUrls().get(0));
            queue.add(picQueue);
        }

        if(pictures.size() == 0){
            throw new BizException("最少需要一张图片");
        }


        Long useYear = new Long(0);
        try {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date old = simpleFormat.parse(simpleFormat.format(loanCarInfoDO.getFirstRegisterDate() == null?new Date():loanCarInfoDO.getFirstRegisterDate()));
            Date now = new Date();
            long l=now.getTime()-old.getTime();
            long day=l/(24*60*60*1000);
            long hour=day*24;
            long mon=day/30;
            long year=mon/12;
            useYear = new Long(mon);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String serNo = GeneratorIDUtil.execute();
        //pub
        applyDiviGeneral.setPlatno(sysConfig.getPlatno());
        applyDiviGeneral.setCmpseq(serNo);
        applyDiviGeneral.setZoneno(StringUtils.isBlank(vehicleInformationDO.getApply_license_plate_area())?null:vehicleInformationDO.getApply_license_plate_area().substring(0,4));
        applyDiviGeneral.setPhybrno(phybrno);
        applyDiviGeneral.setOrderno(orderId.toString());
        applyDiviGeneral.setAssurerno(sysConfig.getAssurerno());
        applyDiviGeneral.setCmpdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        applyDiviGeneral.setCmptime(new SimpleDateFormat("HHmmss").format(new Date()));
        applyDiviGeneral.setBusitype(busitype);
        applyDiviGeneral.setCustomerId(customerId.toString());
        applyDiviGeneral.setFileNum(String.valueOf(pictures.size()));

        //resultsum
        boolean check = bankInterfaceSerialDOMapper.checkRequestBussIsSucessByTransCodeOrderId(customerId,IDict.K_TRANS_CODE.APPLYDIVIGENERAL);
        info.setResubmit(check == true?"1":"0");
        if(bankId.intValue() == 1){
            info.setNote(" ");
        }else if(bankId.intValue() == 3){
            info.setNote("汽车附加消费分期  "+danBaoFee+"  元，其中担保服务费  "+danBaoFee+"  元");
        }

        //customer
        customer.setCustName(loanCustomerDO.getName());
        customer.setIdType(IDict.K_JJLX.IDCARD);
        customer.setIdNo(loanCustomerDO.getIdCard());
        customer.setMobile(loanCustomerDO.getMobile());
        customer.setAddress(loanCustomerDO.getCprovince() + loanCustomerDO.getCcity() + loanCustomerDO.getCcounty() + loanCustomerDO.getAddress());
        customer.setUnit(loanCustomerDO.getIncomeCertificateCompanyName());
        //busi
        //car
        car.setCarType(carFullName);
        car.setPrice(loanFinancialPlanDO.getCarPrice().stripTrailingZeros().toPlainString());
        car.setCarNo1(vehicleInformationDO.getVehicle_identification_number());
        car.setCarRegNo(vehicleInformationDO.getRegistration_certificate_number());
        car.setShorp4s(vehicleInformationDO.getInvoice_car_dealer());
        car.setCarNo2(vehicleInformationDO.getLicense_plate_number());
        car.setAssessPrice(loanFinancialPlanDO.getAppraisal() == null?null:loanFinancialPlanDO.getAppraisal().stripTrailingZeros().toPlainString());//车辆评估价格（元
        car.setAssessOrg(BusitypeEnum.SECOND.getKey().toString().equals(loanCarInfoDO.getCarType().toString())?"0":null);//评估机构
        car.setUsedYears(StringUtils.isBlank(vehicleInformationDO.getAssess_use_year())?useYear == 0 || useYear == null?null:useYear.toString():vehicleInformationDO.getAssess_use_year());//使用年限(月)
        divi.setPaidAmt(paidAmt);
        divi.setAmount(amount);
        divi.setTerm(term);
        divi.setInterest(interest);
        divi.setFeeMode(IDict.K_FEEMODE.TERM);
        divi.setIsPawn(IDict.K_ISPAWN.YES);


        //城站只要宝马
        if(bankId.intValue() == 1){
            carFullName = carBrandDO.getName();
        }else if(bankId.intValue() == 3){
            carFullName =  carBrandDO.getName() + carModelDO.getFullName().replace(carBrandDO.getName(),"");
        }
        divi.setPawnGoods(bankId.intValue() == 1 ? carFullName : bankId.intValue() == 3 ?vehicleInformationDO.getVehicle_identification_number() +" "+carFullName : null);
        divi.setIsAssure(IDict.K_ISASSURE.YES);
        divi.setCard(lendCard);
        divi.setTiexiFlag(IDict.K_TIEXIFLAG.NO);
        divi.setTiexiRate("0");

        //封装完毕
        //针对新 - 二手车进行校验
        if(BusitypeEnum.NEW.getKey().toString().equals(loanCarInfoDO.getCarType().toString())){
            violationUtil.violation(car,NewValidated.class);
            violationUtil.violation(divi,NewValidated.class);
        }else if(BusitypeEnum.SECOND.getKey().toString().equals(loanCarInfoDO.getCarType().toString())){
            violationUtil.violation(car,SecondValidated.class);
            violationUtil.violation(divi,SecondValidated.class);
        }else{
            throw new BizException("此业务类型暂时不支持");
        }
        busi.setCar(car);
        busi.setDivi(divi);
        applyDiviGeneral.setInfo(info);
        applyDiviGeneral.setBusi(busi);
        applyDiviGeneral.setCustomer(customer);
        applyDiviGeneral.setPictures(pictures);
        violationUtil.violation(applyDiviGeneral,ApplyDiviGeneralValidated.class);


        asyncUpload.execute(new Process() {
            @Override
            public void process() {
                asyncUpload.upload(serNo,queue);
                icbcFeignClient.applyDiviGeneral(applyDiviGeneral);
            }
        });
    }


    private void ICBCBankCreditProcess(Long orderId,String phybrno,List<LoanCustomerDO> customers){

        //①判断客户是否已提交了征信记录，且银行征信结果非退回，若满足，则不会推送该客户，否则继续②
        for(LoanCustomerDO loanCustomerDO:customers){
            UniversalBankInterfaceSerialVO result = loanQueryDOMapper.selectUniversalLatestBankInterfaceSerial(loanCustomerDO.getId(),IDict.K_TRANS_CODE.APPLYCREDIT);
            if(result!=null){
                //之前提交过
                //只有调用接口成功才算
                //非处理中 并且 非查询成功的可以进行推送
                if(!IDict.K_JJSTS.SUCCESS.equals(result.getStatus()) && !IDict.K_JJSTS.PROCESS.equals(result.getStatus()) && !IDict.K_JJSTS.SUCCESS_ERROR.equals(result.getStatus()) ) {
                    checkCustomerHavingCreditON14Day(loanCustomerDO);
                    bankCreditProcess(orderId,phybrno,loanCustomerDO);
                }
            }else{
                checkCustomerHavingCreditON14Day(loanCustomerDO);
                bankCreditProcess(orderId,phybrno,loanCustomerDO);
            }
        }
    }

    private void bankCreditProcess(Long orderId,String phybrno,LoanCustomerDO loanCustomerDO){
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        //获取数据源
        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }
        //获取用户授权书签字照
        UniversalMaterialRecordVO authSignPic = loanQueryDOMapper.getUniversalCustomerFilesByType(loanCustomerDO.getId(),new Byte("5"));
        if(authSignPic == null){
            throw new BizException(loanCustomerDO.getName()+"授权书签字照不存在");
        }

        if(CollectionUtils.isEmpty(authSignPic.getUrls())){
            throw new BizException(loanCustomerDO.getName()+"授权书签字照不存在");
        }

        //将身份证正反面、授权书、授权书签字照合成一个word（一个客户合成一张word，如征信申请中有2个客户，则合成2个word）。若存在一个客户合成失败，流程终止。
        //2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;
        Set types = Sets.newHashSet();
        types.add(new Byte("2"));
        types.add(new Byte("3"));
        types.add(new Byte("4"));
        types.add(new Byte("5"));
        List<UniversalMaterialRecordVO> list = loanQueryDOMapper.selectUniversalCustomerFiles(loanCustomerDO.getId(),types);
        list.sort(new Comparator<UniversalMaterialRecordVO>() {
            @Override
            public int compare(UniversalMaterialRecordVO o1, UniversalMaterialRecordVO o2) {
                return Integer.parseInt(o1.getType())-Integer.parseInt(o2.getType());
            }
        });
        List<String> mergeImages = Lists.newLinkedList();
        Set uniqueTypes = Sets.newHashSet();
        for(UniversalMaterialRecordVO V:list){
            uniqueTypes.add(V.getType());
            mergeImages.addAll(V.getUrls());
        }

        Preconditions.checkArgument(CollectionUtils.isNotEmpty(uniqueTypes), loanCustomerDO.getName()+"附件合成失败");
        Preconditions.checkArgument(uniqueTypes.size() == 4, loanCustomerDO.getName()+"附件合成失败");

        loanQueryService.checkBankInterFaceSerialStatus(loanCustomerDO.getId(),IDict.K_TRANS_CODE.APPLYCREDIT);

        List fileNumList = Lists.newArrayList();
        fileNumList.add(mergeImages);
        fileNumList.add(authSignPic.getUrls());

        //上传图片和doc
        String picName = GeneratorIDUtil.execute()+ImageUtil.PIC_SUFFIX;

        String docName = GeneratorIDUtil.execute()+ImageUtil.DOC_SUFFIX;

        String serNo = GeneratorIDUtil.execute();

        //第三方接口调用
        //数据封装
        ICBCApiRequest.ApplyCredit applyCredit = new ICBCApiRequest.ApplyCredit();
        ICBCApiRequest.ApplyCreditCustomer customer = new ICBCApiRequest.ApplyCreditCustomer();
        //pub
        applyCredit.setPlatno(sysConfig.getPlatno());
        applyCredit.setCmpseq(serNo);
        applyCredit.setZoneno(loanBaseInfoDO.getAreaId() == null?null:loanBaseInfoDO.getAreaId().toString().substring(0,4));
        applyCredit.setPhybrno(phybrno);
        applyCredit.setOrderno(orderId.toString());
        applyCredit.setAssurerno(sysConfig.getAssurerno());
        applyCredit.setCmpdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        applyCredit.setCmptime(new SimpleDateFormat("HHmmss").format(new Date()));
        applyCredit.setCustomerId(loanCustomerDO.getId().toString());
        applyCredit.setFileNum((String.valueOf(fileNumList.size())));
        //customer
        customer.setMastername(loanCustomerDO.getName());
        customer.setCustname(loanCustomerDO.getName());
        customer.setIdtype(IDict.K_JJLX.IDCARD);
        customer.setIdno(loanCustomerDO.getIdCard());
        customer.setRelation(convertRelation(loanCustomerDO));
        //pic
        List<ICBCApiRequest.Picture> pictures = Lists.newArrayList();
        //File.separator
        //0004 【征信】授权书签字照片
        ICBCApiRequest.Picture picture_1 = new ICBCApiRequest.Picture();
        picture_1.setPicid("0004");
        picture_1.setPicnote("0004【征信】授权书签字照片");
        picture_1.setPicname(picName);
        //0005【征信】客户征信查询授权书+身份证正反面.doc
        ICBCApiRequest.Picture picture_2 = new ICBCApiRequest.Picture();
        picture_2.setPicid("0005");
        picture_2.setPicnote("0005【征信】客户征信查询授权书+身份证正反面.doc");
        picture_2.setPicname(docName);

        pictures.add(picture_1);
        pictures.add(picture_2);

        //final
        applyCredit.setCustomer(customer);
        applyCredit.setPictures(pictures);
        //走你
        violationUtil.violation(applyCredit, ApplyCreditValidated.class);

        asyncUpload.execute(new Process() {
            @Override
            public void process() {
                asyncUpload.upload(serNo,"0004",picName,authSignPic.getUrls());
                asyncUpload.upload(serNo,"0005",docName,mergeImages);
                icbcFeignClient.applyCredit(applyCredit);
            }
        });
    }




    private String convertRelation(LoanCustomerDO loanCustomerDO){
        if(loanCustomerDO.getCustType() == null){
            throw new BizException(loanCustomerDO.getName()+"的客户类型不明");
        }
        if(GUARANTOR.getType().toString().equals(loanCustomerDO.getCustType().toString())){
            //担保人会将关系转化成反担保
            return "反担保";
        }

        if(PRINCIPAL_LENDER.getType().toString().equals(loanCustomerDO.getCustType().toString())){
            return "本人";
        }

        String custRelation =  RelationEnum.getValueByKey(loanCustomerDO.getCustRelation());

        return custRelation;
    }

    private void checkCustomerHavingCreditON14Day(LoanCustomerDO customers){
        Preconditions.checkArgument(StringUtils.isNotBlank(customers.getIdCard()), customers.getName()+"身份证号不能为空");
        if(loanQueryDOMapper.checkCustomerHavingCreditON14Day(customers.getIdCard())){
            throw new BizException(customers.getName()+"在14天内重复查询征信");
        }
    }



    /**
     * 银行开卡
     * @param bankOpenCardParam
     */
    public CreditCardApplyResponse creditcardapply(BankOpenCardParam bankOpenCardParam) {
        //数据准备
        ICBCApiRequest.ApplyBankOpenCard  applyBankOpenCard= new ICBCApiRequest.ApplyBankOpenCard();
        applyBankOpenCard.setOrderno(String.valueOf(bankOpenCardParam.getOrderId()));
        applyBankOpenCard.setFileNum(bankOpenCardParam.getFileNum());
        applyBankOpenCard.setCustomerId(bankOpenCardParam.getCustomerId());

        ICBCApiRequest.ApplyBankOpenCardCustomer customer =new ICBCApiRequest.ApplyBankOpenCardCustomer();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(bankOpenCardParam.getOrderId());
        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }

        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }


        // 客户信息
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(Long.parseLong(bankOpenCardParam.getCustomerId()), VALID_STATUS);
        Set types = Sets.newHashSet(EMERGENCY_CONTACT.getType());
        List<LoanCustomerDO> emergencys = loanCustomerDOMapper.selectSelfAndRelevanceCustomersByCustTypes(bankOpenCardParam.getOrderId(), types);

        applyBankOpenCard.setAssurerno(sysConfig.getAssurerno());
        applyBankOpenCard.setZoneno(String.valueOf(loanBaseInfoDO.getAreaId()).substring(0,4));
        applyBankOpenCard.setCmpdate(DateUtil.getDate());
        applyBankOpenCard.setCmptime(DateUtil.getTime());
        if(String.valueOf(bankOpenCardParam.getBankId()).equals(IDict.K_BANK.ICBC_HZCZ)){
            applyBankOpenCard.setPhybrno(sysConfig.getHzphybrno());
        }else if(String.valueOf(bankOpenCardParam.getBankId()).equals(IDict.K_BANK.ICBC_TZLQ)){
            applyBankOpenCard.setPhybrno(sysConfig.getTzphybrno());
        }
        applyBankOpenCard.setCmpseq(bankOpenCardParam.getCmpseq());
        applyBankOpenCard.setPlatno(sysConfig.getPlatno());

        // customer
        customer.setEngname(loanCustomerDO.getNamePinyin());//
        customer.setFcurrtyp(IDict.K_BZ.K_RMB);
        customer.setBirthdate(DateUtil.getDateTo8(loanCustomerDO.getBirth()));
        customer.setFeeamount(BigDecimalUtil.format(loanFinancialPlanDO.getBankFee(),0));
        customer.setLoanamount(BigDecimalUtil.format(loanFinancialPlanDO.getLoanAmount(),0));
        customer.setTerm(String.valueOf(loanFinancialPlanDO.getLoanTime()));
        BigDecimal loanratio = loanFinancialPlanDO.getBankPeriodPrincipal().divide(loanFinancialPlanDO.getCarPrice(),3, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        customer.setLoanratio(BigDecimalUtil.format(loanratio, 1));//贷款成数
        customer.setCarprice(BigDecimalUtil.format(loanFinancialPlanDO.getCarPrice(),0));

        ProductRateDOKey productRateDOKey = new ProductRateDOKey();
        productRateDOKey.setProdId(loanFinancialPlanDO.getFinancialProductId());
        productRateDOKey.setLoanTime(loanFinancialPlanDO.getLoanTime());
        ProductRateDO productRateDO = productRateDOMapper.selectByPrimaryKey(productRateDOKey);

        customer.setFeeratio(BigDecimalUtil.format(productRateDO.getBankRate(),6));// 银行费率

        customer.setCprovince(loanCustomerDO.getCprovince());
        customer.setCcounty(loanCustomerDO.getCcounty());//单位地址县
        customer.setCcity(loanCustomerDO.getCcity());//ccity	单位地址市

        customer.setHcity(loanCustomerDO.getHcity());//住宅地址市
        customer.setHcounty(loanCustomerDO.getHcounty());//hcounty	住宅地址县
        customer.setHprovince(loanCustomerDO.getHprovince());//hprovince	住宅地址省份
        customer.setAccaddrf(loanCustomerDO.getBillSendAddr());
        customer.setDrawaddr(loanCustomerDO.getCardSendAddrType());
        String identityValidity = loanCustomerDO.getIdentityValidity();
        String[] split = identityValidity.split("-");
        String endDate=null;
        if(split.length == 2){
            endDate = DateUtil.getDateTo8(split[1]);
        }else{
            throw new BizException("身份证有效期格式错误[YYYY.MM.DD-YYYY.MM.DD]");
        }
        customer.setStatdate(endDate);//证件有效期
        customer.setUnitname(loanCustomerDO.getCompanyName());//工作单位
        customer.setAccgetm(loanCustomerDO.getBillSendType());//对帐单寄送方式
        customer.setMvblno(loanCustomerDO.getMobile());//手机号码
        customer.setCaddress(loanCustomerDO.getCompanyAddress());
        customer.setAuthref(loanCustomerDO.getIssuingDepartment());//发证机关
        customer.setHaddress(loanCustomerDO.getAddress());//住宅地址
        customer.setMachgf(loanCustomerDO.getBalanceChangeRemind()); //主卡开通余额变动提醒
        customer.setMachgmobile(loanCustomerDO.getBalanceChangeTel());//主卡余额提醒发送手机号码
        customer.setJoindate(DateUtil.getDateTo6(loanCustomerDO.getEnrollmentDate()));//进入单位时间
        customer.setDrawmode(loanCustomerDO.getCardReceiveMode());//卡片领取方式
        customer.setChnsname(loanCustomerDO.getName());//姓名
        customer.setMrtlstat(dictMapCache.getValue(IConstant.MARITAL_STATUS, String.valueOf(loanCustomerDO.getMarry())));//婚姻状况
        customer.setModelcode(dictMapCache.getValue(IConstant.COMPANY_NATURE, String.valueOf(loanCustomerDO.getCompanyNature())));//modelcode

        customer.setIndate(DateUtil.getDateTo8(loanCustomerDO.getCheckInDate()));
        customer.setEdulvl(dictMapCache.getValue(IConstant.EDU_LEVEL,String.valueOf(loanCustomerDO.getEducation())));

        customer.setCadrchoic("3");//单位地址选择 1-预查询，2-修改，3-新增。默认送3
        customer.setHphoneno(loanCustomerDO.getMobile());//住宅电话号码
        customer.setHomezip(loanCustomerDO.getPostcode());//homezip	住宅邮编
        customer.setMamobile(loanCustomerDO.getMasterCardTel());//mamobile	主卡发送移动电话
        customer.setCustsort(IDict.K_JJLX.IDCARD);//custsort	证件类型
        customer.setCophoneno(loanCustomerDO.getCompanyPhone());//cophoneno	单位电话号码
        customer.setCorpzip(loanCustomerDO.getCompanyPostcode());//corpzip	单位邮编
        customer.setCustcode(loanCustomerDO.getIdCard());//custcode	证件号码
        customer.setMblchoic("3");//mblchoic	手机选择1-预查询，2-修改，3-新增。默认送3
        customer.setCophozono(loanCustomerDO.getCtelzone());//cophozono	单位电话区号
        customer.setCophonext("0");//cophonext	单位电话分机
        customer.setSex(String.valueOf(loanCustomerDO.getSex()));//性别
        customer.setHadrchoic("3");//hadrchoic	住宅地址选择1-预查询，2-修改，3-新增。默认送3
        customer.setOccptn(loanCustomerDO.getOccupation());//occptn	职业
        customer.setSmsphone(loanCustomerDO.getBellTel());//smsphone	发送短信帐单手机号码
        customer.setEmladdrf(loanCustomerDO.getOpenEmail());//emladdrf	开通email对账单

        //紧急联系人
        //联系人一
        customer.setReltname1(emergencys.get(0).getName());//姓名
        customer.setReltsex1(String.valueOf(emergencys.get(0).getSex()));//性别
        customer.setReltship1(String.valueOf(emergencys.get(0).getCustRelation()));//与主卡申请关系
        customer.setReltmobl1(StringUtil.isEmpty(emergencys.get(0).getMobile())?"0":emergencys.get(0).getMobile());//联系人一手机
        customer.setReltmobl1(StringUtil.isEmpty(emergencys.get(0).getMobile())?"0":emergencys.get(0).getMobile());//联系人一手机
        customer.setRelaphone1(StringUtil.isEmpty(emergencys.get(0).getMobile())?"0":emergencys.get(0).getMobile());//联系人一联系电话号
        //联系人二
        customer.setReltname2(emergencys.get(1).getName());//姓名
        customer.setReltsex2(String.valueOf(emergencys.get(1).getSex()));//性别
        customer.setReltship2(String.valueOf(emergencys.get(1).getCustRelation()));//与主卡申请关系
        customer.setReltmobl2(StringUtil.isEmpty(emergencys.get(1).getMobile())?"0":emergencys.get(0).getMobile());//联系人二手机
        customer.setRtcophon2(StringUtil.isEmpty(emergencys.get(1).getMobile())?"0":emergencys.get(0).getMobile());//联系人二联系电话号

        applyBankOpenCard.setCustomer(customer);
        applyBankOpenCard.setPictures(bankOpenCardParam.getPictures());


        asyncUpload.execute(new Process() {

            @Override
            public void process() {
                LOG.info("银行开卡异步调用开始......");
                List<ICBCApiRequest.Picture> pictures = bankOpenCardParam.getPictures();
                for (ICBCApiRequest.Picture tmp:pictures) {
                    asyncUpload.upload(bankOpenCardParam.getCmpseq(),tmp.getPicid(),tmp.getPicname(),tmp.getPicKeyList());
                }
                icbcFeignClient.creditcardapply(applyBankOpenCard);
                LOG.info("银行开卡异步调用结束");

            }
        });
        return null;
    }

    @Override
    public ApplyStatusResponse applystatus(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }

        Long planId  = loanOrderDO.getLoanFinancialPlanId();
        if(planId == null){
            throw new BizException("此订单金融方案不存在");
        }

        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(planId);
        if(loanFinancialPlanDO == null){
            throw new BizException("此订单金融方案不存在");
        }
        //贷款银行
        Long bankId  =  bankDOMapper.selectIdByName(loanFinancialPlanDO.getBank());
        if(bankId == null){
            throw new BizException("贷款银行不存在");
        }

        int value = bankId.intValue();
        switch (value) {
            case 1:
                //判断当前客户贷款银行是否为杭州工行，如为杭州工行：
                return applystatusProcess(orderId,sysConfig.getHzphybrno());
            case 3:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
                return applystatusProcess(orderId,sysConfig.getHzphybrno());
            default:
                throw new BizException("贷款银行不支持申请查询");
        }

    }

    private ApplyStatusResponse applystatusProcess(Long orderId,String phybrno){
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        //获取数据源
        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }

        Long customerId = loanOrderDO.getLoanCustomerId();
        if(customerId == null){
            throw new BizException("贷款人不存在");
        }
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(customerId,new Byte("0"));
        if(loanCustomerDO == null){
            throw new BizException("贷款人不存在");
        }

        ICBCApiRequest.Applystatus applystatus = new ICBCApiRequest.Applystatus();
        applystatus.setPlatno(sysConfig.getPlatno());
        applystatus.setCmpseq(GeneratorIDUtil.execute());
        applystatus.setZoneno("3301");
        applystatus.setPhybrno(phybrno);
        applystatus.setOrderno(orderId.toString());
        applystatus.setAssurerno(sysConfig.getAssurerno());
        applystatus.setCmpdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        applystatus.setCmptime(new SimpleDateFormat("HHmmss").format(new Date()));
        applystatus.setFileNum("0");
        violationUtil.violation(applystatus);
        ApplyStatusResponse applyStatusResponse = icbcFeignClient.applyStatus(applystatus);
        return applyStatusResponse;
    }

    @Override
    public ApplycreditstatusResponse applycreditstatus(ICBCApiRequest.Applycreditstatus applycreditstatus) {
        ApplycreditstatusResponse response = icbcFeignClient.applycreditstatus(applycreditstatus);
        return response;
    }


}
