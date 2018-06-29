package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.IConstant;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.param.BankReturnParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.BankSolutionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;


@Service
@Transactional
public class BankSolutionServiceImpl implements BankSolutionService {

    @Resource
    private SysConfig sysConfig;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private ICBCFeignClient icbcFeignClient;

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Override
    public void creditAutomaticCommit(@Validated @NotNull  Long orderId,@Validated @NotNull Long bankId, @Validated @NotNull List<LoanCustomerDO> customers) {
        checkCustomerHavingCreditON14Day(customers);
        int value = bankId.intValue();
        switch (value) {
            case 4:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
//                tzICBCBankCreditProcess(customers);
                break;
            default:
                return;
        }
    }

    @Override
    public void creditArtificialCompensation(@Validated @NotNull  Long orderId,@Validated @NotNull Long bankId,@Validated @NotNull Long customerId) {

    }


    private void tzICBCBankCreditProcess(Long orderId,List<LoanCustomerDO> customers){
//            //①判断客户是否已提交了征信记录，且银行征信结果非退回，若满足，则不会推送该客户，否则继续②
//            for(LoanCustomerDO loanCustomerDO:customers){
//                UniversalBankInterfaceSerialVO result = loanQueryDOMapper.selectUniversalLatestBankInterfaceSerial(loanCustomerDO.getId());
//                if(result!=null){
//                    //之前提交过
//                    //非处理中 并且 非查询成功的可以进行推送
//                    if(!result.getStatus().equals(IDict.K_JJSTS.SUCCESS) && !result.getStatus().equals(IDict.K_JJSTS.PROCESS)){
//                        //将身份证正反面、授权书、授权书签字照合成一个word（一个客户合成一张word，如征信申请中有2个客户，则合成2个word）。若存在一个客户合成失败，流程终止。
//                        //2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;
//                        Set types = Sets.newHashSet();
//                        types.add(new Byte("2"));
//                        types.add(new Byte("3"));
//                        types.add(new Byte("4"));
//                        types.add(new Byte("5"));
//                        List<UniversalMaterialRecordVO> list = loanQueryDOMapper.selectUniversalCustomerFiles(loanCustomerDO.getId(),types);
//                        list.sort(new Comparator<UniversalMaterialRecordVO>() {
//                            @Override
//                            public int compare(UniversalMaterialRecordVO o1, UniversalMaterialRecordVO o2) {
//                                return Integer.parseInt(o1.getType())-Integer.parseInt(o2.getType());
//                            }
//                        });
//                        List<String> mergeImages = Lists.newLinkedList();
//                        Set uniqueTypes = Sets.newHashSet();
//                        for(UniversalMaterialRecordVO V:list){
//                            uniqueTypes.add(V.getType());
//                            mergeImages.addAll(V.getUrls());
//                        }
//
//                        Preconditions.checkArgument(CollectionUtils.isNotEmpty(uniqueTypes), loanCustomerDO.getName()+"附件合成失败");
//                        Preconditions.checkArgument(uniqueTypes.size() == 4, loanCustomerDO.getName()+"附件合成失败");
//                        String localFilePath = ImageUtil.mergeImage2Doc(mergeImages);
//                        //第三方接口调用
//                        //数据封装
//                        ICBCApiParam.ApplyCredit applyCredit = new ICBCApiParam.ApplyCredit();
//                        ICBCApiParam.Customer customer = new ICBCApiParam.Customer();
//                        ICBCApiParam.Picture picture = new ICBCApiParam.Picture();
//                        //start
//                        applyCredit.setPlatno();
//                        applyCredit.setCmpseq();
//                        applyCredit.setZoneno();
//                        applyCredit.setPhybrno();
//                        applyCredit.setOrderno();
//                        applyCredit.setAssurerno();
//                        applyCredit.setCmpdate();
//                        applyCredit.setCmptime();
//                        //customer
//                        customer.setMastername()
//                        customer.setCustname();
//                        customer.setIdtype();
//                        customer.setIdno();
//                        customer.setRelation();
//                        //pic
//
//
//
//                        List<ICBCApiParam.Picture> pictures = Lists.newArrayList();
//
//
//
//
//                        applyCredit.setCustomer(customer);
//                        applyCredit.setPictures(pictures);
//                        icbcFeignClient.applyCredit(applyCredit);
//
//
//
//
//
//                        DesEncryptUtil.decryptBasedDes(sysConfig.getPassword());
//                        FtpUtil.upload(localFilePath,sysConfig.getServerpath(),sysConfig.getServierIP(),sysConfig.getPort(),sysConfig.getUserName(),sysConfig.getPassword(),"");
//
//                    }
//                }
//
//
//            }


    }

    private void checkCustomerHavingCreditON14Day(List<LoanCustomerDO> customers){
        for(LoanCustomerDO loanCustomerDO:customers){
            Preconditions.checkArgument(StringUtils.isNotBlank(loanCustomerDO.getIdCard()), loanCustomerDO.getName()+"身份证号不能为空");
            if(loanQueryDOMapper.checkCustomerHavingCreditON14Day(loanCustomerDO.getIdCard())){
                throw new BizException(loanCustomerDO.getName()+"在14天内重复查询征信");
            }
        }
    }


    /**
     * 银行开卡
     * @param bankOpenCardParam
     */
    public ResultBean creditcardapply(BankOpenCardParam bankOpenCardParam){
        //记录银行开发流水信息
        BankInterfaceSerialDO serialDO = new BankInterfaceSerialDO();
        //TODO 生成流水号
        serialDO.setSerialNo("12242423423423423423423");
        serialDO.setCustomerId(bankOpenCardParam.getCustomerId());
        serialDO.setTransCode(IDict.K_API.CREDITCARDAPPLY);
        serialDO.setStatus(IDict.K_JJZT.PROCESS);
        int count = bankInterfaceSerialDOMapper.insertSelective(serialDO);
        Preconditions.checkArgument(count>0,"插入银行开卡流水失败");

        //发送银行接口
        ResultBean creditcardapply = icbcFeignClient.creditcardapply(bankOpenCardParam);
        //应答数据
        BankReturnParam returnParam = (BankReturnParam)creditcardapply.getData();
        if(IConstant.SUCCESS.equals(returnParam.getReturnCode()) && IConstant.API_SUCCESS.equals(returnParam.getIcbcApiRetcode())){
            serialDO.setApiStatus(IDict.K_JJZT.REQ_SUCC);
            count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(serialDO);//更新状态
            Preconditions.checkArgument(count>0,"更新银行开卡流水失败");
            return ResultBean.ofSuccess(returnParam);
        }else{
            serialDO.setApiStatus(IDict.K_JJZT.REQ_FAIL);
            count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(serialDO);//更新状态
            Preconditions.checkArgument(count>0,"更新银行开卡流水失败");
            throw  new BizException("发送银行开卡流水失败");
        }

    }


}
