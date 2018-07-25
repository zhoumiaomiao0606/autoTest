package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.vo.BankInterFaceSerialOrderStatusVO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanProcessDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanProcessLogService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import java.util.List;

@Service
public class LoanQueryServiceImpl implements LoanQueryService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanProcessLogService loanProcessLogService;

    @Resource
    private LoanProcessDOMapper loanProcessDOMapper;

    @Resource
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Override
    public UniversalCustomerDetailVO universalCustomerDetail(Long customerId) {

        return loanQueryDOMapper.selectUniversalCustomerDetail(loanQueryDOMapper.selectOrderIdbyPrincipalCustId(customerId),customerId);
    }

    @Override
    public String selectTelephoneVerifyLevel() {
        return loanQueryDOMapper.selectTelephoneVerifyLevel(SessionUtils.getLoginUser().getId());
    }

    @Override
    public Integer selectBankInterFaceSerialOrderStatusByOrderId(Long orderId,String transCode) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }

        List<BankInterFaceSerialOrderStatusVO> list =  loanQueryDOMapper.selectBankInterFaceSerialOrderStatusByOrderId(orderId,transCode);
        if(CollectionUtils.isEmpty(list)){
            return 1;
        }

        for(BankInterFaceSerialOrderStatusVO V : list){
            //如果有一个为退回，就会被打回去 打回的单子是可以操作的
            if("3".equals(V.getStatus())){
                return 1;
            }
        }

        for(BankInterFaceSerialOrderStatusVO V : list){
            if(!"200".equals(V.getApi_status())){
                return 3;
            }

            if((!"0".equals(V.getStatus()) && !"1".equals(V.getStatus()) && !"2".equals(V.getStatus()) && !"3".equals(V.getStatus()))){
                //有一个推送失败
                return 3;
            }
        }

        for(BankInterFaceSerialOrderStatusVO V : list){

            if("2".equals(V.getStatus()) && "200".equals(V.getApi_status())){
                //有一个是处理中 且其他不等于处理失败
                return 2;
            }
        }


        for(BankInterFaceSerialOrderStatusVO V : list){

            if(!("0".equals(V.getStatus()) || "1".equals(V.getStatus()) && "200".equals(V.getApi_status()))){
                //有一个是处理中 且其他不等于处理失败
                return 4;
            }
        }


        return 1;
    }

    @Override
    public Integer selectBankOpenCardStatusByOrderId(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此订单不存在");
        }
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Byte t = loanProcessDO.getTelephoneVerify();
        if(t == null){
            t = new Byte("0");
        }
        String x = t.toString();

        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(),new Byte("0"));
        String y = loanCustomerDO.getOpenCardOrder();
        if(StringUtils.isBlank(y)){
            throw new BizException("缺少开卡顺序");
        }

        //如果
        if(y.equals("0")){
            if(x.equals("1")){
                return 1;
            }else {
                return 0;
            }
        }else{
            return 1;
        }


    }


}
