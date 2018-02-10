package com.yunche.loan.service.impl;

import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessActionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CustBaseInfoDOMapper;
import com.yunche.loan.dao.mapper.CustRelaPersonInfoDOMapper;
import com.yunche.loan.domain.dataObj.CustBaseInfoDO;
import com.yunche.loan.domain.dataObj.CustRelaPersonInfoDO;
import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.CustService;
import com.yunche.loan.service.LoanOrderService;
import com.yunche.loan.service.ProcessNodeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
@Service
public class CustServiceImpl implements CustService {
    @Autowired
    private CustBaseInfoDOMapper custBaseInfoDOMapper;

    @Autowired
    private CustRelaPersonInfoDOMapper custRelaPersonInfoDOMapper;

    @Autowired
    private ProcessNodeService processNodeService;

    @Autowired
    private LoanOrderService loanOrderService;

    @Override
    public ResultBean<Void> create(DelegateExecution execution) {
        CustBaseInfoVO custBaseInfoVO = (CustBaseInfoVO) execution.getVariable("custBaseInfoVO");
        if (custBaseInfoVO != null && custBaseInfoVO.getCustId() == null) {
            // 主贷人
            CustBaseInfoDO custBaseInfoDO = new CustBaseInfoDO();
            BeanUtils.copyProperties(custBaseInfoVO, custBaseInfoDO);
            custBaseInfoDOMapper.insert(custBaseInfoDO);

            List<CustRelaPersonInfoDO> custRelaPersonInfoDOList = custBaseInfoVO.getRelaPersonList();
            if (CollectionUtils.isNotEmpty(custRelaPersonInfoDOList)) {
                for (CustRelaPersonInfoDO custRelaPersonInfoDO : custRelaPersonInfoDOList) {
                    custRelaPersonInfoDO.setRelaCustId(custBaseInfoDO.getCustId());
                    custRelaPersonInfoDOMapper.insert(custRelaPersonInfoDO);
                }
            }

            // 更新订单信息
            InstLoanOrderVO instLoanOrderVO = (InstLoanOrderVO) execution.getVariable("instLoanOrderVO");
            instLoanOrderVO.setCustId(custBaseInfoDO.getCustId());
            loanOrderService.update(instLoanOrderVO);

            // 记录流程执行节点
            InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
            instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
            String processId = (String) execution.getVariable("processId");
            instProcessNodeDO.setProcessInstId(processId);
            instProcessNodeDO.setNodeCode(LoanProcessEnum.CREDIT_SAVE.getCode());
            instProcessNodeDO.setNodeName(LoanProcessEnum.CREDIT_SAVE.getName());
            instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.CREDIT_APPLY.getCode());
            instProcessNodeDO.setNextNodeCode(LoanProcessEnum.CREDIT_VERIFY.getCode());
            instProcessNodeDO.setStatus(ProcessActionEnum.PASS.name());
            processNodeService.insert(instProcessNodeDO);
        } else if (custBaseInfoVO != null && custBaseInfoVO.getCustId() != null) {
            // 客户已存在
            update(custBaseInfoVO);

            // 更新订单信息
            InstLoanOrderVO instLoanOrderVO = (InstLoanOrderVO) execution.getVariable("instLoanOrderVO");
            instLoanOrderVO.setCustId(custBaseInfoVO.getCustId());
            loanOrderService.update(instLoanOrderVO);

            // 记录流程执行节点
            InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
            instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
            String processId = (String) execution.getVariable("processId");
            instProcessNodeDO.setProcessInstId(processId);
            instProcessNodeDO.setNodeCode(LoanProcessEnum.CREDIT_SAVE.getCode());
            instProcessNodeDO.setNodeName(LoanProcessEnum.CREDIT_SAVE.getName());
            instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.CREDIT_APPLY.getCode());
            instProcessNodeDO.setNextNodeCode(LoanProcessEnum.CREDIT_VERIFY.getCode());
            instProcessNodeDO.setStatus(ProcessActionEnum.PASS.name());
            processNodeService.insert(instProcessNodeDO);
        }
        return ResultBean.ofSuccess(null, "创建客户成功");
    }

    @Override
    public ResultBean<Void> update(CustBaseInfoVO custBaseInfoVO) {
        // 主贷人
        CustBaseInfoDO custBaseInfoDO = new CustBaseInfoDO();
        BeanUtils.copyProperties(custBaseInfoVO, custBaseInfoDO);
        custBaseInfoDOMapper.updateByPrimaryKeySelective(custBaseInfoDO);

        List<CustRelaPersonInfoDO> custRelaPersonInfoDOList = custBaseInfoVO.getRelaPersonList();
        if (CollectionUtils.isNotEmpty(custRelaPersonInfoDOList)) {
            for (CustRelaPersonInfoDO custRelaPersonInfoDO : custRelaPersonInfoDOList) {
                custRelaPersonInfoDO.setRelaCustId(custBaseInfoDO.getCustId());
                custRelaPersonInfoDOMapper.updateByPrimaryKeySelective(custRelaPersonInfoDO);
            }
        }

        return ResultBean.ofSuccess(null, "更新客户成功");
    }
}
