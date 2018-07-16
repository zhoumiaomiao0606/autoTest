package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanMaterialManageDO;
import com.yunche.loan.domain.vo.LoanMaterialManageVO;
import com.yunche.loan.mapper.LoanMaterialManageDOMapper;
import com.yunche.loan.service.LoanMaterialManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Service
public class LoanMaterialManageServiceImpl implements LoanMaterialManageService {

    @Autowired
    private LoanMaterialManageDOMapper loanMaterialManageDOMapper;


    @Override
    public ResultBean<Void> save(LoanMaterialManageDO loanMaterialManageDO) {
        Preconditions.checkNotNull(loanMaterialManageDO.getOrderId(), "订单号不能为空");

        LoanMaterialManageDO existDO = loanMaterialManageDOMapper.selectByPrimaryKey(loanMaterialManageDO.getOrderId());

        if (null == existDO) {
            // create
            loanMaterialManageDO.setGmtCreate(new Date());
            loanMaterialManageDO.setGmtModify(new Date());
            int count = loanMaterialManageDOMapper.insertSelective(loanMaterialManageDO);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            loanMaterialManageDO.setGmtModify(new Date());
            int count = loanMaterialManageDOMapper.updateByPrimaryKeySelective(loanMaterialManageDO);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }

    @Override
    public ResultBean<LoanMaterialManageVO> detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        LoanMaterialManageDO loanMaterialManageDO = loanMaterialManageDOMapper.selectByPrimaryKey(orderId);

        LoanMaterialManageVO loanMaterialManageVO = new LoanMaterialManageVO();
        if (null != loanMaterialManageDO) {
            BeanUtils.copyProperties(loanMaterialManageDO, loanMaterialManageVO);
            loanMaterialManageVO.setOrderId(String.valueOf(loanMaterialManageDO.getOrderId()));
        }

        return ResultBean.ofSuccess(loanMaterialManageVO);
    }
}
