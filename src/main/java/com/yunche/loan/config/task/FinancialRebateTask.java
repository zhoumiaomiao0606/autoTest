package com.yunche.loan.config.task;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.domain.entity.FinancialRebateDetailDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.vo.FSysRebateVO;
import com.yunche.loan.mapper.CustomersLoanFinanceInfoByPartnerMapper;
import com.yunche.loan.mapper.FinancialRebateDetailDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class FinancialRebateTask {

    private static final Logger logger = LoggerFactory.getLogger(FinancialRebateTask.class);

    @Autowired
    private CustomersLoanFinanceInfoByPartnerMapper customersLoanFinanceInfoByPartnerMapper;
    @Autowired
    private FinancialRebateDetailDOMapper financialRebateDetailDOMapper;
    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;


    @Scheduled(cron = "0 0 0 15 * ?")
//    @Scheduled(cron = "0 0/1 * * * ?")
//    @DistributedLock(60)
    @Transactional(rollbackFor = Exception.class)
    public void doAutoGenRebate(){
        logger.info("返利记录录入开始-"+ DateUtil.getDate());
        List<FSysRebateVO> rebateVOS = customersLoanFinanceInfoByPartnerMapper.generateCurrRebateRecord();
        logger.info("返利记录录入处理中-"+ JSONObject.toJSON(rebateVOS).toString());
        HashMap<Long, BigDecimal> rebatesMap = Maps.newHashMap();

        for(FSysRebateVO f :rebateVOS){
            if(rebatesMap.get(f.getPartnerId())==null){
                rebatesMap.put(f.getPartnerId(),f.getAmount());
            }else{
                BigDecimal  tmp = rebatesMap.get(f.getPartnerId());
                rebatesMap.put(f.getPartnerId(), tmp.add(f.getAmount()));
            }
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(f.getOrderId());
            if(loanOrderDO!=null){
                loanOrderDO.setRebatePeriods(f.getPeriods());
            }
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }

        Long tmpPartnerId=null;
        for(FSysRebateVO f :rebateVOS){
            if(f.getPartnerId()!=tmpPartnerId){
                FinancialRebateDetailDO rebateDetailDO = new FinancialRebateDetailDO();
                rebateDetailDO.setPartnerId(f.getPartnerId());
                rebateDetailDO.setPeriods(f.getPeriods());
                rebateDetailDO.setRebateAmount(rebatesMap.get(f.getPartnerId()));
                rebateDetailDO.setEnterAccountFlag(new Byte("1"));//未入账
                FinancialRebateDetailDO detailDO = financialRebateDetailDOMapper.selectByPrimaryKey(rebateDetailDO);
                if(detailDO==null){
                    int count = financialRebateDetailDOMapper.insertSelective(rebateDetailDO);
                    Preconditions.checkArgument(count>0,"返利列表新增失败");
                }else{
                    rebateDetailDO.setGmtModify(new Date());
                    int count = financialRebateDetailDOMapper.updateByPrimaryKeySelective(rebateDetailDO);
                    Preconditions.checkArgument(count>0,"返利列表更新失败");
                }
            }
            tmpPartnerId=f.getPartnerId();
        }
        logger.info("返利记录录入结束-"+ DateUtil.getDate());


    }
}
