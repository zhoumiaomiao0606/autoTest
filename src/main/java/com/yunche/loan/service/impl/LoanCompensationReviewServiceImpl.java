package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.domain.vo.FinancialSchemeVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCompensationVO;
import com.yunche.loan.domain.vo.UniversalInfoVO;
import com.yunche.loan.mapper.LoanApplyCompensationDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanCompensationReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 财务代偿确认
 */
@Service
public class LoanCompensationReviewServiceImpl implements LoanCompensationReviewService {

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    /**
     * 代偿确认保存
     * @param param
     * @return
     */
    @Override
    @Transactional
    public Void save(UniversalCompensationParam param) {
        Preconditions.checkNotNull(param,"参数有误");
        Preconditions.checkNotNull(param.getId(),"代偿ID不能为空");
        int count = loanApplyCompensationDOMapper.updateByPrimaryKeySelective(param);
        Preconditions.checkArgument(count>0,"保存失败");

       return null;
    }

    /**
     * 代偿确认详情页
     * @param query
     * @return
     */
    @Override
    public ResultBean detail(UniversalCompensationQuery query) {
        Preconditions.checkNotNull(query,"参数有误");
        Preconditions.checkNotNull(query.getOrderId(),"业务单号不能为空");
        Preconditions.checkNotNull(query.getInsteadPayOrderId(),"代偿确认ID不能为空");


        LoanApplyCompensationDO compensationReview = loanApplyCompensationDOMapper.selectByPrimaryKey(query.getInsteadPayOrderId());
        UniversalCompensationVO compensationVO = new UniversalCompensationVO();
        BeanUtils.copyProperties(compensationReview,compensationVO);
        compensationVO.setOrderId(String.valueOf(compensationReview.getOrderId()));


        RecombinationVO<Object> recombinationVO = new RecombinationVO<>();

        //数据查询
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(query.getOrderId());
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(query.getOrderId());

        //数据填充
        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setApplyCompensation(compensationVO);

        return ResultBean.ofSuccess(recombinationVO);
    }
}
