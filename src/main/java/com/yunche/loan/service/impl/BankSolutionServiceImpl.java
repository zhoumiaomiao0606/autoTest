package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.RelationEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.config.util.ImageUtil;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.UniversalBankInterfaceSerialVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankSolutionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        //封装数据
        ICBCApiRequest.ApplyDiviGeneral applyDiviGeneral = new ICBCApiRequest.ApplyDiviGeneral();
        ICBCApiRequest.ApplyDiviGeneralCustomer customer = new ICBCApiRequest.ApplyDiviGeneralCustomer();
        ICBCApiRequest.ApplyDiviGeneralBusi busi = new ICBCApiRequest.ApplyDiviGeneralBusi();
        ICBCApiRequest.ApplyDiviGeneralCar car = new ICBCApiRequest.ApplyDiviGeneralCar();
        ICBCApiRequest.ApplyDiviGeneralDivi divi = new ICBCApiRequest.ApplyDiviGeneralDivi();



        icbcFeignClient.applyDiviGeneral(applyDiviGeneral);
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
        //数据准备
        bankOpenCardParam.setCmpseq(GeneratorIDUtil.execute());
        ICBCApiRequest.ApplyBankOpenCard  applyBankOpenCard= new ICBCApiRequest.ApplyBankOpenCard();
        BeanUtils.copyProperties(applyBankOpenCard,bankOpenCardParam);
        //发送银行接口
        icbcFeignClient.creditcardapply(applyBankOpenCard);
    }


}
