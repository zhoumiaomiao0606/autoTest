package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessActionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CustBaseInfoDOMapper;
import com.yunche.loan.dao.mapper.CustRelaPersonInfoDOMapper;
import com.yunche.loan.domain.dataObj.CustBaseInfoDO;
import com.yunche.loan.domain.dataObj.CustRelaPersonInfoDO;
import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.CustRelaPersonInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.CustService;
import com.yunche.loan.service.LoanOrderService;
import com.yunche.loan.service.ProcessNodeService;
import org.activiti.engine.TaskService;
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

    @Autowired
    private TaskService taskService;

    @Override
    public ResultBean<Long> create(DelegateExecution execution) {
        CustBaseInfoVO custBaseInfoVO = (CustBaseInfoVO) execution.getVariable("custBaseInfoVO");
        InstLoanOrderVO instLoanOrderVO = (InstLoanOrderVO) execution.getVariable("instLoanOrderVO");
        String processId = (String) execution.getVariable("processId");
        Long custId = null;
        if (custBaseInfoVO != null && custBaseInfoVO.getCustId() == null) {
            // 主贷人
            CustBaseInfoDO custBaseInfoDO = new CustBaseInfoDO();
            BeanUtils.copyProperties(custBaseInfoVO, custBaseInfoDO);
            custBaseInfoDOMapper.insert(custBaseInfoDO);
            custBaseInfoVO.setCustId(custBaseInfoDO.getCustId());

            List<CustRelaPersonInfoDO> custRelaPersonInfoDOList = custBaseInfoVO.getRelaPersonList();
            if (CollectionUtils.isNotEmpty(custRelaPersonInfoDOList)) {
                for (CustRelaPersonInfoDO custRelaPersonInfoDO : custRelaPersonInfoDOList) {
                    custRelaPersonInfoDO.setRelaCustId(custBaseInfoDO.getCustId());
                    custRelaPersonInfoDOMapper.insert(custRelaPersonInfoDO);
                }
            }

            // 更新订单信息
            instLoanOrderVO.setCustId(custBaseInfoDO.getCustId());
            loanOrderService.update(instLoanOrderVO);

            // 记录流程执行节点
            InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
            instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
            instProcessNodeDO.setProcessInstId(processId);
            instProcessNodeDO.setNodeCode(LoanProcessEnum.CREDIT_SAVE.getCode());
            instProcessNodeDO.setNodeName(LoanProcessEnum.CREDIT_SAVE.getName());
            instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.CREDIT_APPLY.getCode());
            instProcessNodeDO.setNextNodeCode(LoanProcessEnum.CREDIT_VERIFY.getCode());
            instProcessNodeDO.setStatus(ProcessActionEnum.PASS.name());
            processNodeService.insert(instProcessNodeDO);

            return ResultBean.ofSuccess(custBaseInfoVO.getCustId(), "创建主贷人成功");
        } else if (custBaseInfoVO != null && custBaseInfoVO.getCustId() != null) {
            // 客户已存在
            updateMainCust(custBaseInfoVO);

            // 更新订单信息
            instLoanOrderVO.setCustId(custBaseInfoVO.getCustId());
            loanOrderService.update(instLoanOrderVO);

            // 记录流程执行节点
            InstProcessNodeDO instProcessNodeDO = new InstProcessNodeDO();
            instProcessNodeDO.setOrderId(instLoanOrderVO.getOrderId());
            instProcessNodeDO.setProcessInstId(processId);
            instProcessNodeDO.setNodeCode(LoanProcessEnum.CREDIT_SAVE.getCode());
            instProcessNodeDO.setNodeName(LoanProcessEnum.CREDIT_SAVE.getName());
            instProcessNodeDO.setPreviousNodeCode(LoanProcessEnum.CREDIT_APPLY.getCode());
            instProcessNodeDO.setNextNodeCode(LoanProcessEnum.CREDIT_VERIFY.getCode());
            instProcessNodeDO.setStatus(ProcessActionEnum.PASS.name());
            processNodeService.insert(instProcessNodeDO);

            return ResultBean.ofSuccess(custBaseInfoVO.getCustId(), "更新主贷人成功");
        }
        return ResultBean.ofSuccess(custBaseInfoVO.getCustId(), "客户信息为空");
    }

    @Override
    public ResultBean<Long> createMainCust(CustBaseInfoVO custBaseInfoVO) {
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
        return ResultBean.ofSuccess(custBaseInfoDO.getCustId(), "创建主贷人成功");
    }

    @Override
    public ResultBean<Long> updateMainCust(CustBaseInfoVO custBaseInfoVO) {
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
        return ResultBean.ofSuccess(custBaseInfoDO.getCustId(), "修改主贷人成功");
    }

    @Override
    public ResultBean<Long> createRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO) {
        CustRelaPersonInfoDO custRelaPersonInfoDO = new CustRelaPersonInfoDO();
        BeanUtils.copyProperties(custRelaPersonInfoVO, custRelaPersonInfoDO);
        custRelaPersonInfoDOMapper.insert(custRelaPersonInfoDO);

        return ResultBean.ofSuccess(custRelaPersonInfoDO.getCustId(), "创建关联人成功");
    }

    @Override
    public ResultBean<Long> updateRelaCust(CustRelaPersonInfoVO custRelaPersonInfoVO) {
        CustRelaPersonInfoDO custRelaPersonInfoDO = new CustRelaPersonInfoDO();
        BeanUtils.copyProperties(custRelaPersonInfoVO, custRelaPersonInfoDO);
        custRelaPersonInfoDOMapper.updateByPrimaryKeySelective(custRelaPersonInfoDO);

        return ResultBean.ofSuccess(custRelaPersonInfoDO.getCustId(), "更新关联人成功");
    }

    @Override
    public ResultBean<Void> deleteRelaCust(Long custId) {
        Preconditions.checkArgument(custId != null, "custId");

        custRelaPersonInfoDOMapper.deleteByPrimaryKey(custId);

        return ResultBean.ofSuccess(null, "删除关联人成功");
    }

    @Override
    public ResultBean<Void> faceOff(Long mainCustId, Long relaCustId) {
        CustBaseInfoDO mainCustDO = custBaseInfoDOMapper.selectByPrimaryKey(mainCustId);
        CustRelaPersonInfoDO relaCustDO = custRelaPersonInfoDOMapper.selectByPrimaryKey(relaCustId);

        CustBaseInfoDO newMainCustDO = new CustBaseInfoDO();
        BeanUtils.copyProperties(relaCustDO, newMainCustDO);
        newMainCustDO.setCustId(mainCustDO.getCustId());
        custBaseInfoDOMapper.updateByPrimaryKeySelective(newMainCustDO);

        CustRelaPersonInfoDO newRelaCustDO = new CustRelaPersonInfoDO();
        BeanUtils.copyProperties(mainCustDO, newRelaCustDO);
        newRelaCustDO.setCustId(relaCustDO.getCustId());
        custRelaPersonInfoDOMapper.updateByPrimaryKeySelective(newRelaCustDO);

        return ResultBean.ofSuccess(null, "主贷人和共贷人切换成功");
    }

}
