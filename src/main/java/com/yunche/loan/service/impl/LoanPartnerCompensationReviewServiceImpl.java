package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanApplyCompensationDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanPartnerCompensationReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 合伙人代偿确认
 */
@Service
public class LoanPartnerCompensationReviewServiceImpl implements LoanPartnerCompensationReviewService {


    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    /**
     * 合伙人代偿确认 - 保存
     * @param param
     * @return
     */
    @Override
    @Transactional
    public ResultBean save(UniversalCompensationParam param) {
        Preconditions.checkNotNull(param,"参数有误");
        Preconditions.checkNotNull(param.getOrderId(),"业务单号不能为空");
        Preconditions.checkNotNull(param.getApplyCompensationDate(),"代偿申请日期不能为空");
        Preconditions.checkNotNull(param.getId(),"代偿ID不能为空");


        int count = loanApplyCompensationDOMapper.updateByPrimaryKeySelective(param);
        Preconditions.checkArgument(count>0,"合伙人代偿确认保存失败");

        return ResultBean.ofSuccess(null,"保存成功");
    }

    /**
     * 合伙人代偿确认 - 详情
     * @param query
     * @return
     */
    @Override
    public ResultBean detail(UniversalCompensationQuery query) {
        Preconditions.checkNotNull(query,"参数有误");
        Preconditions.checkNotNull(query.getOrderId(),"业务单号不能为空");
        Preconditions.checkNotNull(query.getInsteadPayOrderId(),"代偿申请日期不能为空");

        //数据查询
        LoanApplyCompensationDO applyCompensation = loanApplyCompensationDOMapper.selectByPrimaryKey(query.getInsteadPayOrderId());
        UniversalCompensationVO compensationVO = new UniversalCompensationVO();
        BeanUtils.copyProperties(applyCompensation,compensationVO);
        compensationVO.setOrderId(String.valueOf(applyCompensation.getOrderId()));


        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(query.getOrderId());//客户基本信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(query.getOrderId());
        Set<Byte> types = new HashSet<Byte>();
        types.add(LoanFileEnum.COMPENSATION_PAYMENT_VOUCHER.getType());
        List<UniversalMaterialRecordVO> materialRecord = loanQueryDOMapper.selectUniversalCustomerFileByTypes(query.getOrderId(), types);

        //返回数据
        RecombinationVO<Object> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setMaterials(materialRecord);
        recombinationVO.setApplyCompensation(compensationVO);
        return ResultBean.ofSuccess(recombinationVO);
    }
}
