package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.param.CreditRecordParam;
import com.yunche.loan.domain.vo.LoanCreditInfoVO;
import com.yunche.loan.mapper.LoanCreditInfoDOMapper;
import com.yunche.loan.service.LoanCreditInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Service
@Transactional
public class LoanCreditInfoServiceImpl implements LoanCreditInfoService {


    @Autowired
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;


    @Override
    public ResultBean<Long> create(CreditRecordParam creditRecordParam) {
        Preconditions.checkNotNull(creditRecordParam.getCustomerId(), "客户ID不能为空");
        Preconditions.checkNotNull(creditRecordParam.getType(), "征信类型不能为空");
        Preconditions.checkNotNull(creditRecordParam.getResult(), "征信结果不能为空");

        LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
        BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);
        loanCreditInfoDO.setStatus(VALID_STATUS);
        loanCreditInfoDO.setGmtCreate(new Date());
        loanCreditInfoDO.setGmtModify(new Date());
        int count = loanCreditInfoDOMapper.insertSelective(loanCreditInfoDO);
        Preconditions.checkArgument(count > 0, "征信结果录入失败");

        return ResultBean.ofSuccess(loanCreditInfoDO.getId(), "征信结果录入成功");
    }

    @Override
    public ResultBean<Long> update(CreditRecordParam creditRecordParam) {
        Preconditions.checkNotNull(creditRecordParam.getId(), "征信信息ID不能为空");

        LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
        BeanUtils.copyProperties(creditRecordParam, loanCreditInfoDO);
        loanCreditInfoDO.setGmtModify(new Date());
        int count = loanCreditInfoDOMapper.updateByPrimaryKeySelective(loanCreditInfoDO);
        Preconditions.checkArgument(count > 0, "征信结果修改失败");

        return ResultBean.ofSuccess(loanCreditInfoDO.getId(), "征信结果修改成功");
    }

    @Override
    public ResultBean<LoanCreditInfoVO> getByCustomerId(Long customerId, Byte type) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");
        Preconditions.checkNotNull(type, "征信类型不能为空");

        LoanCreditInfoDO loanCreditInfoDO = loanCreditInfoDOMapper.getByCustomerIdAndType(customerId, type);
        LoanCreditInfoVO loanCreditInfoVO = new LoanCreditInfoVO();
        if (null != loanCreditInfoDO) {
            BeanUtils.copyProperties(loanCreditInfoDO, loanCreditInfoVO);
        }
        return ResultBean.ofSuccess(loanCreditInfoVO);
    }
}
