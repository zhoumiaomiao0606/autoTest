package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.domain.entity.BankOnlineTransDO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.BankOnlineTransDOMapper;
import com.yunche.loan.service.BankOnlineTransService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhengdu
 * @date 2019/2/15 下午1:55
 */
@Service
public class BankOnlineTransServiceImpl implements BankOnlineTransService{

    private static final Logger LOG = LoggerFactory.getLogger(BankOnlineTransService.class);


    @Autowired
    private BankOnlineTransDOMapper bankOnlineTransDOMapper;
    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;
    @Override
    public void registerransStatus(Long orderId, String transCode,String status) {
        BankOnlineTransDO onlineTransDO = bankOnlineTransDOMapper.selectByPrimaryKey(orderId);
        BankOnlineTransDO tmpDO = prePackage(onlineTransDO,orderId, transCode, status);
        if(IDict.K_BANK_JYZT.FAIL.equals(status)){
            tmpDO.setActionTimes(0);
        }
        if(onlineTransDO!=null){
            bankOnlineTransDOMapper.updateByPrimaryKeySelective(tmpDO);
        }else{
            bankOnlineTransDOMapper.insertSelective(tmpDO);
        }
    }

    @Override
    public Boolean check(Long orderId,String transCode) {
        BankOnlineTransDO bankOnlineTransDO = bankOnlineTransDOMapper.selectByPrimaryKey(orderId);

        if(bankOnlineTransDO == null ||  bankOnlineTransDO.getActionTimes()!=0 ){
            return false;
        }
        if(IDict.K_TRANS_CODE.APPLYCREDIT.equals(transCode)){

        }
        return true;
    }

    @Override
    public synchronized Void subActionTimes(Long orderId) {
        BankOnlineTransDO bankOnlineTransDO = bankOnlineTransDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(bankOnlineTransDO);
        Preconditions.checkNotNull(bankOnlineTransDO.getActionTimes(),"影响次数不能为空");

        if(bankOnlineTransDO.getActionTimes()>0){
            bankOnlineTransDO.setActionTimes(bankOnlineTransDO.getActionTimes()-1);
            bankOnlineTransDOMapper.updateByPrimaryKeySelective(bankOnlineTransDO);
        }
        return null;
    }

    @Override
    public synchronized Void addActionTimes(Long orderId) {
        BankOnlineTransDO bankOnlineTransDO = bankOnlineTransDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(bankOnlineTransDO);

        if(bankOnlineTransDO.getActionTimes()==null ){
            bankOnlineTransDO.setActionTimes(1);
        }else{
            bankOnlineTransDO.setActionTimes(bankOnlineTransDO.getActionTimes()+1);
        }
        bankOnlineTransDOMapper.updateByPrimaryKeySelective(bankOnlineTransDO);
        return null;
    }


    private BankOnlineTransDO prePackage(BankOnlineTransDO b ,Long orderId, String transCode,String status){

        if(b==null){
            b = new BankOnlineTransDO();
        }
        b.setOrderId(orderId);
        if(IDict.K_TRANS_CODE.APPLYCREDIT.equals(transCode)){
            if(!IDict.K_BANK_JYZT.FAIL.equals(b.getCreditStatus())){
                b.setCreditStatus(status);
            }
        }else if(IDict.K_TRANS_CODE.CREDITCARDAPPLY.equals(transCode)){
            if(!IDict.K_BANK_JYZT.FAIL.equals(b.getOpenCardStatus())){
                b.setOpenCardStatus(status);
            }

        }else if(IDict.K_TRANS_CODE.APPLYDIVIGENERAL.equals(transCode)){
            if(!IDict.K_BANK_JYZT.FAIL.equals(b.getCommonApplyStatus())){
                b.setCommonApplyStatus(status);
            }
        }else if(IDict.K_TRANS_CODE.MULTIMEDIAUPLOAD.equals(transCode)){
            if(!IDict.K_BANK_JYZT.FAIL.equals(b.getCommonApplyStatus())){
                b.setMultimediaStatus(status);
            }
        }
        return b;
    }
}
