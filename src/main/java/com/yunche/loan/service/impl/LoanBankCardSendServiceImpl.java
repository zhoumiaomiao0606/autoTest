package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.entity.LoanMaterialManageDO;
import com.yunche.loan.domain.vo.LoanBankCardSendVO;
import com.yunche.loan.domain.vo.LoanMaterialManageVO;
import com.yunche.loan.mapper.LoanBankCardSendDOMapper;
import com.yunche.loan.service.LoanBankCardSendService;
import org.springframework.beans.BeanUtils;
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
    public ResultBean<LoanBankCardSendVO> detail(Long orderId) {

        Preconditions.checkNotNull(orderId, "订单号不能为空");

        LoanBankCardSendDO loanBankCardSendDO = loanBankCardSendDOMapper.selectByPrimaryKey(orderId);

        LoanBankCardSendVO loanBankCardSendVO = new LoanBankCardSendVO();
        if (null != loanBankCardSendDO) {
            BeanUtils.copyProperties(loanBankCardSendDO, loanBankCardSendVO);
            loanBankCardSendVO.setOrderId(String.valueOf(loanBankCardSendDO.getOrderId()));
        }

        return ResultBean.ofSuccess(loanBankCardSendVO);
    }
}
