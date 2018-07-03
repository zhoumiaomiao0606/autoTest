package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.CarCache;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.BusitypeEnum;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.RelationEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.feign.request.group.New;
import com.yunche.loan.config.feign.request.group.Second;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.config.util.ImageUtil;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.UniversalBankInterfaceSerialVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankSolutionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunche.loan.config.constant.LoanCustomerEnum.GUARANTOR;
import static com.yunche.loan.config.constant.LoanCustomerEnum.PRINCIPAL_LENDER;


@Service
@Transactional
public class BankSolutionServiceImpl implements BankSolutionService {

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

    //征信自动提交
    @Override
    public void creditAutomaticCommit(@Validated @NotNull  Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));
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
        }
        checkCustomerHavingCreditON14Day(customers);
        int value = bankId.intValue();
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
    public void commonBusinessApply(@Validated @NotNull Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));
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
                ICBCCommonBusinessApplyProcess(orderId,sysConfig.getHzphybrno());
                break;
            case 3:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
                ICBCCommonBusinessApplyProcess(orderId,sysConfig.getHzphybrno());
                break;
            default:
                return;
        }


    }

    //征信人工补偿
    @Override
    public void creditArtificialCompensation(@Validated @NotNull  Long orderId,@Validated @NotNull Long bankId,@Validated @NotNull Long customerId) {
        //todo
    }

    public void ICBCCommonBusinessApplyProcess(Long orderId,String phybrno){
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));
        //获取数据源
        Long baseId = loanOrderDO.getLoanBaseInfoId();
        if(baseId == null){
            throw new BizException("征信信息不存在");
        }

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(baseId);
        if(loanBaseInfoDO == null){
            throw new BizException("征信信息不存在");
        }
        //数据映射层
        //所贷车辆类型：1-新车; 2-二手车; 3-不限;
        String busitype = BusitypeEnum.getValueByKey(loanBaseInfoDO.getCarType());
        if(StringUtils.isBlank(busitype)){
            throw new BizException("此业务类型暂时不支持");
        }

        Long customerId = loanOrderDO.getLoanCustomerId();
        if(customerId == null){
            throw new BizException("贷款人不存在");
        }
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(customerId,new Byte("0"));
        if(loanCustomerDO == null){
            throw new BizException("贷款人不存在");
        }

        Long carId = loanOrderDO.getLoanCarInfoId();
        if(carId == null){
            throw new BizException("贷款车辆不存在");
        }
        LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(carId);
        if(loanCarInfoDO == null){
            throw new BizException("贷款车辆不存在");
        }
        Long carDetailId = loanCarInfoDO.getCarDetailId();
        if(carDetailId == null){
            throw new BizException("贷款车辆不存在");
        }
        CarDetailDO carDetailDO = carCache.getCarDetail(carDetailId);
        if(carDetailDO == null){
            throw new BizException("贷款车辆不存在");
        }

        CarModelDO carModelDO = carCache.getCarModel(carDetailDO.getModelId());
        if(carModelDO == null){
            throw new BizException("贷款车辆不存在");
        }

        CarBrandDO carBrandDO = carCache.getCarBrand(carModelDO.getBrandId());
        if(carBrandDO == null){
            throw new BizException("贷款车辆不存在");
        }

        String carFullName = carBrandDO.getName() + carModelDO.getName() + carDetailDO.getName();
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
        pk.setLoanTime(pk.getLoanTime() == null?new Integer(0):pk.getLoanTime());

        ProductRateDO pkDO = productRateDOMapper.selectByPrimaryKey(pk);
        if(pkDO == null){
            throw new BizException("此产品银行基准利率为空");
        }

        Long useYear = new Long(0);
        try {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
            Date old = simpleFormat.parse(simpleFormat.format(loanCarInfoDO.getFirstRegisterDate() == null?new Date():loanCarInfoDO.getFirstRegisterDate()));
            Date now = new Date();
            long l=now.getTime()-old.getTime();
            long day=l/(24*60*60*1000);
            long hour=day*24;
            long mon=day/30;
            long year=mon/12;
            useYear = new Long(year);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        BigDecimal dawnPaymentMoney = new BigDecimal("0");
        if(loanFinancialPlanDO.getDownPaymentMoney() != null ){
            dawnPaymentMoney = vehicleInformationDO.getInvoice_down_payment();
        }

        BigDecimal bankPeriodPrincipal = new BigDecimal("0");
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


        String paidAmt = dawnPaymentMoney.toString();
        String amount = bankPeriodPrincipal.toString();
        String term = loanTime.toString();
        String interest = loanTimeFee.toString();


        //封装数据
        ICBCApiRequest.ApplyDiviGeneral applyDiviGeneral = new ICBCApiRequest.ApplyDiviGeneral();
        ICBCApiRequest.ApplyDiviGeneralCustomer customer = new ICBCApiRequest.ApplyDiviGeneralCustomer();
        ICBCApiRequest.ApplyDiviGeneralBusi busi = new ICBCApiRequest.ApplyDiviGeneralBusi();
        ICBCApiRequest.ApplyDiviGeneralCar car = new ICBCApiRequest.ApplyDiviGeneralCar();
        ICBCApiRequest.ApplyDiviGeneralDivi divi = new ICBCApiRequest.ApplyDiviGeneralDivi();
        //start 封装
        //pub
        applyDiviGeneral.setPlatno(sysConfig.getPlatno());
        applyDiviGeneral.setCmpseq(GeneratorIDUtil.execute());
        applyDiviGeneral.setZoneno("1202");
        applyDiviGeneral.setPhybrno(phybrno);
        applyDiviGeneral.setOrderno(orderId.toString());
        applyDiviGeneral.setAssurerno(sysConfig.getAssurerno());
        applyDiviGeneral.setCmpdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        applyDiviGeneral.setCmptime(new SimpleDateFormat("HHmmss").format(new Date()));
        applyDiviGeneral.setBusitype(busitype);
        //customer
        customer.setCustName(loanCustomerDO.getName());
        customer.setIdType(IDict.K_JJLX.IDCARD);
        customer.setIdNo(loanCustomerDO.getIdCard());
        customer.setMobile(loanCustomerDO.getMobile());
        customer.setAddress(loanCustomerDO.getAddress());
        customer.setUnit(loanCustomerDO.getIncomeCertificateCompanyName());
        customer.setNote(loanCustomerDO.getName()+"申请分期");
        //busi
        //car
        car.setCarType(carFullName);
        car.setPrice(loanFinancialPlanDO.getCarPrice().toString());
        car.setCarNo1(vehicleInformationDO.getVehicle_identification_number());
        car.setCarRegNo(vehicleInformationDO.getRegistration_certificate_number());
        car.setShorp4s(vehicleInformationDO.getInvoice_car_dealer());
        car.setCarNo2(vehicleInformationDO.getLicense_plate_number());
        car.setAssessPrice(carDetailDO.getPrice());//车辆评估价格（元
        car.setAssessOrg(vehicleInformationDO.getAssess_org());//评估机构
        car.setUsedYears(useYear.toString());//使用年限(月)

        divi.setPaidAmt(paidAmt);
        divi.setAmount(amount);
        divi.setTerm(term);
        divi.setInterest(interest);
        divi.setFeeMode(IDict.K_FEEMODE.TERM);
        divi.setIsPawn(IDict.K_ISPAWN.YES);
        divi.setPawnGoods(vehicleInformationDO.getVehicle_identification_number()+carFullName);
        divi.setIsAssure(IDict.K_ISASSURE.YES);
        divi.setCard();
        divi.setTiexiFlag(IDict.K_TIEXIFLAG.NO);
        divi.setTiexiRate("0");


        //封装完毕
        //针对新 - 二手车进行校验
        if(BusitypeEnum.NEW.getKey().toString().equals(loanCarInfoDO.getCarType())){
            autoCheckNewProcess(car,divi);
        }else if(BusitypeEnum.SECOND.getKey().toString().equals(loanCarInfoDO.getCarType())){
            autoCheckSecondProcess(car,divi);
        }else{
            throw new BizException("此业务类型暂时不支持");
        }
        busi.setCar(car);
        busi.setDivi(divi);
        applyDiviGeneral.setBusi(busi);
        applyDiviGeneral.setCustomer(customer);
        icbcFeignClient.applyDiviGeneral(applyDiviGeneral);
    }

    private void autoCheckNewProcess(@Validated(New.class) @NotNull ICBCApiRequest.ApplyDiviGeneralCar car,@Validated(New.class) @NotNull ICBCApiRequest.ApplyDiviGeneralDivi divi){
        //这个方法只是用spring jpa 触发校验
    }

    private void autoCheckSecondProcess(@Validated(Second.class) ICBCApiRequest.ApplyDiviGeneralCar car,@Validated(Second.class) ICBCApiRequest.ApplyDiviGeneralDivi divi){
        //这个方法只是用spring jpa 触发校验
    }



    private void ICBCBankCreditProcess(Long orderId,String phybrno,List<LoanCustomerDO> customers){
            //①判断客户是否已提交了征信记录，且银行征信结果非退回，若满足，则不会推送该客户，否则继续②
            for(LoanCustomerDO loanCustomerDO:customers){
                UniversalBankInterfaceSerialVO result = loanQueryDOMapper.selectUniversalLatestBankInterfaceSerial(loanCustomerDO.getId());
                if(result!=null){
                    //之前提交过
                    //只有调用接口成功才算
                    //非处理中 并且 非查询成功的可以进行推送
                    if(!IDict.K_JJSTS.SUCCESS.equals(result.getStatus()) && !IDict.K_JJSTS.PROCESS.equals(result.getStatus())) {
                        bankCreditProcess(orderId,phybrno,loanCustomerDO);
                    }
                }else{
                    bankCreditProcess(orderId,phybrno,loanCustomerDO);
                }
            }
    }

    private void bankCreditProcess(Long orderId,String phybrno,LoanCustomerDO loanCustomerDO){
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


        //上传图片和doc
        String picName = GeneratorIDUtil.execute()+ImageUtil.PIC_SUFFIX;
        String picPath = ImageUtil.mergeImage2Pic(picName,authSignPic.getUrls());

        String docName = GeneratorIDUtil.execute()+ImageUtil.DOC_SUFFIX;
        String docPath = ImageUtil.mergeImage2Doc(docName,mergeImages);


        //第三方接口调用
        //数据封装
        ICBCApiRequest.ApplyCredit applyCredit = new ICBCApiRequest.ApplyCredit();
        ICBCApiRequest.ApplyCreditCustomer customer = new ICBCApiRequest.ApplyCreditCustomer();
        //pub
        applyCredit.setPlatno(sysConfig.getPlatno());
        applyCredit.setCmpseq(GeneratorIDUtil.execute());
        applyCredit.setZoneno("1202");
        applyCredit.setPhybrno(phybrno);
        applyCredit.setOrderno(orderId.toString());
        applyCredit.setAssurerno(sysConfig.getAssurerno());
        applyCredit.setCmpdate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        applyCredit.setCmptime(new SimpleDateFormat("HHmmss").format(new Date()));
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
        icbcFeignClient.applyCredit(applyCredit);
        //上传
        uploadFile(picPath);
        uploadFile(docPath);
    }

    private String path2Name(String path){
        String[] strs = path.split(File.separator);
        return  strs[strs.length-1];
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

    private void checkCustomerHavingCreditON14Day(List<LoanCustomerDO> customers){
        for(LoanCustomerDO loanCustomerDO:customers){
            Preconditions.checkArgument(StringUtils.isNotBlank(loanCustomerDO.getIdCard()), loanCustomerDO.getName()+"身份证号不能为空");
            if(loanQueryDOMapper.checkCustomerHavingCreditON14Day(loanCustomerDO.getIdCard())){
                throw new BizException(loanCustomerDO.getName()+"在14天内重复查询征信");
            }
        }
    }

    private void uploadFile(String path){
        FtpUtil.icbcUpload(path);
    }


    /**
     * 银行开卡
     * @param bankOpenCardParam
     */
    public void creditcardapply(BankOpenCardParam bankOpenCardParam) {
        //记录银行开发流水信息
        //BankInterfaceSerialDO serialDO = new BankInterfaceSerialDO();
        //BankInterfaceSerialDO bankInterfaceSerialDO = bankInterfaceSerialDOMapper.selectByCustomerIdAndTransCode(bankOpenCardParam.getCustomerId(), IDict.K_API.CREDITCARDAPPLY);
        //if(bankInterfaceSerialDO==null){
        //    serialDO.setSerialNo(GeneratorIDUtil.execute());
        //    serialDO.setCustomerId(bankOpenCardParam.getCustomerId());
        //    serialDO.setTransCode(IDict.K_API.CREDITCARDAPPLY);
        //    serialDO.setStatus(IDict.K_JYZT.PROCESS);
        //    int count = bankInterfaceSerialDOMapper.insertSelective(serialDO);
        //    Preconditions.checkArgument(count>0,"插入银行开卡流水失败");
        //}else{
        //    BeanUtils.copyProperties(bankInterfaceSerialDO,serialDO);
        //}
        //数据准备    beg
        bankOpenCardParam.setCmpseq(GeneratorIDUtil.execute());
        ICBCApiRequest.ApplyBankOpenCard  applyBankOpenCard= new ICBCApiRequest.ApplyBankOpenCard();
        //数据准备结束 end
        BeanUtils.copyProperties(applyBankOpenCard,bankOpenCardParam);
        //发送银行接口
        //ResultBean creditcardapply = null
        icbcFeignClient.creditcardapply(applyBankOpenCard);
        //应答数据
        //Map<String,String> data = (Map)creditcardapply.getData();

        //if(IConstant.SUCCESS.equals(data.get(IConstant.RETURN_CODE)) && IConstant.API_SUCCESS.equals(data.get(IConstant.ICBC_API_RETCODE))){
        //    serialDO.setApiStatus(IDict.K_JYZT.REQ_SUCC);

        //    int count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(serialDO);//更新状态
        //    Preconditions.checkArgument(count>0,"更新银行开卡流水失败");
        //    return creditcardapply;
        //}else{
        //    serialDO.setApiStatus(IDict.K_JYZT.REQ_FAIL);
        //    int count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(serialDO);//更新状态
        //    Preconditions.checkArgument(count>0,"更新银行开卡流水失败");
        //    throw  new BizException("银行开卡失败");
        //}

    }


}
