package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.IConstant;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.RelationEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.config.util.ImageUtil;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.param.ICBCApiParam;
import com.yunche.loan.domain.vo.UniversalBankInterfaceSerialVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
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
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Resource
    private LoanCustomerDOMapper loanCustomerDOMapper;
    //征信自动提交
    @Override
    public void creditAutomaticCommit(@Validated @NotNull  Long orderId,@Validated @NotNull Long bankId) {
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
                //判断当前客户贷款银行是否为台州工行，如为杭州工行：
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
    //征信人工补偿
    @Override
    public void creditArtificialCompensation(@Validated @NotNull  Long orderId,@Validated @NotNull Long bankId,@Validated @NotNull Long customerId) {

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
                        process(orderId,phybrno,loanCustomerDO);
                    }
                }else{
                    process(orderId,phybrno,loanCustomerDO);
                }
            }
    }

    private void process(Long orderId,String phybrno,LoanCustomerDO loanCustomerDO){
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
        ICBCApiParam.ApplyCredit applyCredit = new ICBCApiParam.ApplyCredit();
        ICBCApiParam.Customer customer = new ICBCApiParam.Customer();
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
        List<ICBCApiParam.Picture> pictures = Lists.newArrayList();
        //File.separator
        //0004 【征信】授权书签字照片
        ICBCApiParam.Picture picture_1 = new ICBCApiParam.Picture();
        picture_1.setPicid("0004");
        picture_1.setPicnote("0004【征信】授权书签字照片");
        picture_1.setPicname(picName);
        //0005【征信】客户征信查询授权书+身份证正反面.doc
        ICBCApiParam.Picture picture_2 = new ICBCApiParam.Picture();
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
        //数据准备结束 end

        //发送银行接口
        //ResultBean creditcardapply = null
        icbcFeignClient.creditcardapply(bankOpenCardParam);
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
