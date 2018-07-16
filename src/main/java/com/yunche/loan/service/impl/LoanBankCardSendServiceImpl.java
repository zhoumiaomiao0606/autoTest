package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.vo.UniversalBankCardSendVO;
import com.yunche.loan.mapper.LoanBankCardSendDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanBankCardSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Service
public class LoanBankCardSendServiceImpl implements LoanBankCardSendService {

    @Autowired
    private LoanBankCardSendDOMapper loanBankCardSendDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;


    @Override
    public ResultBean<Void> save(LoanBankCardSendDO loanBankCardSendDO) {
        Preconditions.checkNotNull(loanBankCardSendDO.getOrderId(), "订单号不能为空");

        LoanBankCardSendDO existDO = loanBankCardSendDOMapper.selectByPrimaryKey(loanBankCardSendDO.getOrderId());

        if (null == existDO) {
            // create
            loanBankCardSendDO.setGmtCreate(new Date());
            loanBankCardSendDO.setGmtModify(new Date());
            int count = loanBankCardSendDOMapper.insertSelective(loanBankCardSendDO);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            loanBankCardSendDO.setGmtModify(new Date());
            int count = loanBankCardSendDOMapper.updateByPrimaryKeySelective(loanBankCardSendDO);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }

    @Override
    public ResultBean<UniversalBankCardSendVO> detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        UniversalBankCardSendVO universalBankCardSendVO = loanQueryDOMapper.selectUniversalBankCardSend(orderId);

        return ResultBean.ofSuccess(universalBankCardSendVO);
    }
}
