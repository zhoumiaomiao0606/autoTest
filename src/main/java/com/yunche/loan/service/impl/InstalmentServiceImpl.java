package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.InstalmentService;
import lombok.Setter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@Service
@Transactional
public class InstalmentServiceImpl implements InstalmentService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Override
    public ApplyDiviGeneralInfoVO detail(Long orderId) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,new Byte("0"));

        if(orderDO == null){
            throw new BizException("此订单不存在");
        }

        return loanQueryDOMapper.selectApplyDiviGeneralInfo(orderId);
    }
}
