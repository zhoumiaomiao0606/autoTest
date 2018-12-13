package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ContractOverDueParam;
import com.yunche.loan.domain.vo.ContractOverDueVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.ContractOverDueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContractOverDueServiceImpl implements ContractOverDueService
{
    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Override
    public ResultBean list(ContractOverDueParam param)
    {
        List<ContractOverDueVO> list = loanQueryDOMapper.contractOverDueList(param);
        return null;
    }
}
