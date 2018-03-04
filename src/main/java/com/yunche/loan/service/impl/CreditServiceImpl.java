package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.LoanProcessOrderDOMapper;
import com.yunche.loan.dao.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.domain.dataObj.LoanProcessOrderDO;
import com.yunche.loan.domain.dataObj.LoanBaseInfoDO;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.domain.viewObj.LoanBaseInfoVO;
import com.yunche.loan.service.CreditService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/2/26
 */
@Service
@Transactional
public class CreditServiceImpl implements CreditService {

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanProcessOrderDOMapper loanProcessOrderDOMapper;

    @Autowired
    private RuntimeService runtimeService;


    @Override
    public ResultBean<Long> createLoanBaseInfo(String orderId, LoanBaseInfoDO loanBaseInfoDO) {
        Preconditions.checkArgument(StringUtils.isNotBlank(orderId), "业务单ID不能为空");
        Preconditions.checkArgument(null != loanBaseInfoDO && null != loanBaseInfoDO.getPartnerId(), "合伙人不能为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getSalesmanId(), "业务员不能为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getAreaId(), "业务区域不能为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getCarType(), "车辆类型不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(loanBaseInfoDO.getBank()), "贷款银行不能为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "预计贷款金额不能为空");

        // 贷款基本信息
        loanBaseInfoDO.setStatus(VALID_STATUS);
        loanBaseInfoDO.setGmtCreate(new Date());
        loanBaseInfoDO.setGmtModify(new Date());
        int count = loanBaseInfoDOMapper.insertSelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "保存贷款基本信息失败");

        // 开启流程单，并关联业务单
        startProcessAndRelaInstProcessOrder(orderId, loanBaseInfoDO.getId());

        return ResultBean.ofSuccess(loanBaseInfoDO.getId(), "保存贷款基本信息成功");
    }

    @Override
    public ResultBean<Void> updateLoanBaseInfo(LoanBaseInfoDO loanBaseInfoDO) {
        Preconditions.checkNotNull(loanBaseInfoDO.getId(), "贷款基本信息ID不能为空");

        loanBaseInfoDO.setGmtModify(new Date());
        int count = loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "编辑贷款基本信息失败");

        return ResultBean.ofSuccess(null, "编辑贷款基本信息成功");
    }

    @Override
    public ResultBean<LoanBaseInfoVO> getLoanBaseInfoById(Long id) {
        Preconditions.checkNotNull(id, "贷款基本信息ID不能为空");

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(id);
        LoanBaseInfoVO loanBaseInfoVO = new LoanBaseInfoVO();
        BeanUtils.copyProperties(loanBaseInfoDO, loanBaseInfoVO);

        return ResultBean.ofSuccess(loanBaseInfoVO);
    }

    @Override
    public ResultBean<List<InstLoanOrderVO>> query(OrderListQuery query) {

//        int totalNum = loanProcessOrderDOMapper.count(query);
//        if (totalNum > 0) {
//
//            List<InstLoanOrderDO> instLoanOrderDOS = loanProcessOrderDOMapper.query(query);
//            if (!CollectionUtils.isEmpty(instLoanOrderDOS)) {
//
//
//                List<InstLoanOrderVO> instLoanOrderVOList = instLoanOrderDOS.parallelStream()
//                        .filter(Objects::nonNull)
//                        .map(e -> {
//
//                            InstLoanOrderVO instLoanOrderVO = new InstLoanOrderVO();
//
//                            return instLoanOrderVO;
//                        })
//                        .collect(Collectors.toList());
//
//                return ResultBean.ofSuccess(instLoanOrderVOList, totalNum, query.getPageIndex(), query.getPageSize());
//            }
//        }

        return ResultBean.ofSuccess(Collections.EMPTY_LIST, 0, query.getPageIndex(), query.getPageSize());
    }

    /**
     * 开启流程单，并关联业务单
     *
     * @param orderId
     * @param loanBaseInfoId
     */
    private void startProcessAndRelaInstProcessOrder(String orderId, Long loanBaseInfoId) {
        // 开启activiti流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dev_loan_process");

        LoanProcessOrderDO loanProcessOrderDO = loanProcessOrderDOMapper.selectByPrimaryKey(orderId, null);
        // 开启本地业务单流程：业务单不存在，则新建
        if (null == loanProcessOrderDO) {
            loanProcessOrderDO = new LoanProcessOrderDO();
            loanProcessOrderDO.setProcessInstId(processInstance.getProcessInstanceId());
            loanProcessOrderDO.setId(orderId);
            loanProcessOrderDO.setLoanBaseInfoId(loanBaseInfoId);
            loanProcessOrderDO.setGmtCreate(new Date());
            loanProcessOrderDO.setGmtModify(new Date());
            int count = loanProcessOrderDOMapper.insertSelective(loanProcessOrderDO);
            Preconditions.checkArgument(count > 0, "新建业务单失败");
        } else {
            // 已存在，则直接关联
            loanProcessOrderDO.setProcessInstId(processInstance.getProcessInstanceId());
            loanProcessOrderDO.setLoanBaseInfoId(loanBaseInfoId);
            loanProcessOrderDO.setGmtModify(new Date());
            loanProcessOrderDOMapper.updateByPrimaryKeySelective(loanProcessOrderDO);
        }
    }
}
