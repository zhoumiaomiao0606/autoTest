package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CustBaseInfoDOMapper;
import com.yunche.loan.dao.mapper.CustRelaPersonInfoDOMapper;
import com.yunche.loan.domain.dataObj.CustBaseInfoDO;
import com.yunche.loan.domain.dataObj.CustRelaPersonInfoDO;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.service.CreditService;
import org.activiti.engine.delegate.DelegateExecution;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
@Service
public class CreditServiceImpl implements CreditService {
    @Autowired
    private CustBaseInfoDOMapper custBaseInfoDOMapper;
    @Autowired
    private CustRelaPersonInfoDOMapper custRelaPersonInfoDOMapper;

    @Override
    public ResultBean<Void> create(DelegateExecution execution) {
        CustBaseInfoVO custBaseInfoVO = (CustBaseInfoVO) execution.getVariable("custBaseInfoVO");
        if (custBaseInfoVO != null) {
            // 主贷人
            CustBaseInfoDO custBaseInfoDO = new CustBaseInfoDO();
            BeanUtils.copyProperties(custBaseInfoVO, custBaseInfoDO);
            custBaseInfoDOMapper.insert(custBaseInfoDO);

            // 共贷人
            CustRelaPersonInfoDO sharePerson = custBaseInfoVO.getShareLoanPerson();
            if (sharePerson != null) {
                sharePerson.setRelaCustId(custBaseInfoDO.getCustId());
                custRelaPersonInfoDOMapper.insert(sharePerson);
            }
            // 担保人
            CustRelaPersonInfoDO guarantPerson = custBaseInfoVO.getGuarantPerson();
            if (guarantPerson != null) {
                guarantPerson.setRelaCustId(custBaseInfoDO.getCustId());
                custRelaPersonInfoDOMapper.insert(guarantPerson);
            }
            // 反担保人
            CustRelaPersonInfoDO backGuarantPerson = custBaseInfoVO.getBackGuarantorPerson();
            if (backGuarantPerson != null) {
                backGuarantPerson.setRelaCustId(custBaseInfoDO.getCustId());
                custRelaPersonInfoDOMapper.insert(backGuarantPerson);
            }
        }
        return ResultBean.ofSuccess(null, "存储成功");
    }
}
