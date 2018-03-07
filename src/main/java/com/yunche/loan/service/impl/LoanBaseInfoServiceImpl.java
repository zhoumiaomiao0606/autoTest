package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.vo.LoanBaseInfoVO;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.service.LoanBaseInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
@Service
@Transactional
public class LoanBaseInfoServiceImpl implements LoanBaseInfoService {

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;


    @Override
    public ResultBean<Void> update(LoanBaseInfoVO loanBaseInfoVO) {
        Preconditions.checkArgument(null != loanBaseInfoVO && null != loanBaseInfoVO.getId(), "贷款基本信息ID不能为空");
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(loanBaseInfoVO, loanBaseInfoDO);
        loanBaseInfoDO.setGmtModify(new Date());

        int count = loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null);
    }
}
