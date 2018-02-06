package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.InstLoanOrderDOMapper;
import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.LoanOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
public class LoanOrderServiceImpl implements LoanOrderService {
    @Autowired
    private InstLoanOrderDOMapper instLoanOrderDOMapper;

    @Override
    public ResultBean<InstLoanOrderDO> create(String processInstanceId) {
        InstLoanOrderDO instLoanOrderDO = new InstLoanOrderDO();
        instLoanOrderDO.setProcessInstId(processInstanceId);
        instLoanOrderDO.setStatus(0);
        instLoanOrderDOMapper.insert(instLoanOrderDO);

        return ResultBean.ofSuccess(instLoanOrderDO, "创建订单成功");
    }

    @Override
    public ResultBean<InstLoanOrderDO> update(InstLoanOrderVO instLoanOrderVO) {
        InstLoanOrderDO instLoanOrderDO = new InstLoanOrderDO();
        BeanUtils.copyProperties(instLoanOrderVO, instLoanOrderDO);
        instLoanOrderDO.setStatus(0);
        instLoanOrderDOMapper.updateByPrimaryKeySelective(instLoanOrderDO);

        return ResultBean.ofSuccess(instLoanOrderDO, "更新订单成功");
    }
}
