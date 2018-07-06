package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalInfoVO;
import com.yunche.loan.mapper.LoanDataFlowDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanDataFlowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
@Service
public class LoanDataFlowServiceImpl implements LoanDataFlowService {

    @Autowired
    private LoanDataFlowDOMapper loanDataFlowDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;


    @Override
    public ResultBean<RecombinationVO> detail(Long orderId, String taskKey) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(taskKey), "taskKey不能为空");

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);


        RecombinationVO recombinationVO = new RecombinationVO();


        return ResultBean.ofSuccess(recombinationVO);
    }

    @Override
    @Transactional
    public ResultBean create(LoanDataFlowDO loanDataFlowDO) {
        Preconditions.checkArgument(null != loanDataFlowDO && null != loanDataFlowDO.getType(), "type不能为空");

        loanDataFlowDO.setGmtCreate(new Date());
        loanDataFlowDO.setGmtModify(new Date());
        int count = loanDataFlowDOMapper.insertSelective(loanDataFlowDO);
        Preconditions.checkArgument(count > 0, "插入失败");

        return ResultBean.ofSuccess(loanDataFlowDO.getId(), "创建成功");
    }

    @Override
    @Transactional
    public ResultBean update(LoanDataFlowDO loanDataFlowDO) {
        Preconditions.checkArgument(null != loanDataFlowDO && null != loanDataFlowDO.getId(), "id不能为空");

        loanDataFlowDO.setGmtModify(new Date());
        int count = loanDataFlowDOMapper.updateByPrimaryKeySelective(loanDataFlowDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean contract_c2b_detail(Long orderId) {
        return null;
    }

    @Override
    public ResultBean contract_c2b_create(LoanDataFlowDO loanDataFlowDO) {
        return null;
    }

    @Override
    public ResultBean contract_c2b_update(LoanDataFlowDO loanDataFlowDO) {
        return null;
    }
}
