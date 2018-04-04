package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCarInfoDO;
import com.yunche.loan.domain.param.LoanCarInfoParam;
import com.yunche.loan.mapper.LoanCarInfoDOMapper;
import com.yunche.loan.service.LoanCarInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/3/8
 */
@Service
@Transactional
public class LoanCarInfoServiceImpl implements LoanCarInfoService {

    @Autowired
    private LoanCarInfoDOMapper loanCarInfoDOMapper;


    @Override
    public ResultBean<Long> create(LoanCarInfoDO loanCarInfoDO) {
        Preconditions.checkNotNull(loanCarInfoDO, "车辆信息不能为空");
        Preconditions.checkNotNull(loanCarInfoDO.getCarDetailId(), "车型不能为空");
        Preconditions.checkNotNull(loanCarInfoDO.getCarType(), "车辆属性不能为空");
        Preconditions.checkNotNull(loanCarInfoDO.getPartnerId(), "合伙人不能为空");
//        Preconditions.checkNotNull(loanCarInfoDO.getGpsNum(), "GPS个数不能为空");
//        Preconditions.checkNotNull(loanCarInfoDO.getCarKey(), "留备用钥匙不能为空");
/*        Preconditions.checkNotNull(loanCarInfoDO.getOpenBank(), "收款银行不能为空");
        Preconditions.checkNotNull(loanCarInfoDO.getAccountName(), "收款账户不能为空");
        Preconditions.checkNotNull(loanCarInfoDO.getBankAccount(), "收款账号不能为空");*/
        /*Preconditions.checkNotNull(loanCarInfoDO.getPayMonth(), "是否月结不能为空");*/

        // insert
        loanCarInfoDO.setGmtCreate(new Date());
        loanCarInfoDO.setGmtModify(new Date());
        loanCarInfoDO.setStatus(VALID_STATUS);

        int count = loanCarInfoDOMapper.insertSelective(loanCarInfoDO);
        Preconditions.checkArgument(count > 0, "创建贷款车辆信息失败");

        return ResultBean.ofSuccess(loanCarInfoDO.getId());
    }

    @Override
    public ResultBean<Void> update(LoanCarInfoDO loanCarInfoDO) {
        Preconditions.checkNotNull(loanCarInfoDO.getId(), "车辆信息ID不能为空");

        loanCarInfoDO.setGmtModify(new Date());
        int count = loanCarInfoDOMapper.updateByPrimaryKeySelective(loanCarInfoDO);
        Preconditions.checkArgument(count > 0, "编辑贷款车辆信息失败");

        return ResultBean.ofSuccess(null);
    }
}
