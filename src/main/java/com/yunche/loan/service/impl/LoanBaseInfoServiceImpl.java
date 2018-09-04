package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.LoanBaseInfoVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanBaseInfoService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
@Service
@Transactional
public class LoanBaseInfoServiceImpl implements LoanBaseInfoService {

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private PartnerRelaEmployeeDOMapper partnerRelaEmployeeDOMapper;

    @Autowired
    private DepartmentDOMapper departmentDOMapper;

    @Autowired
    private BankCache bankCache;


    @Override
    public ResultBean<Long> create(LoanBaseInfoDO loanBaseInfoDO) {
//        Preconditions.checkNotNull(loanBaseInfoDO.getPartnerId(), "合伙人不能为空");
//        Preconditions.checkNotNull(loanBaseInfoDO.getSalesmanId(), "业务员不能为空");
//        Preconditions.checkNotNull(loanBaseInfoDO.getAreaId(), "区域不能为空");
//        Preconditions.checkNotNull(param.getCarType(), "车辆类型不能为空");
//        Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "预计贷款不能为空");

        // 业务员ID  -> 当前操作人
        if (null == loanBaseInfoDO.getSalesmanId()) {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            EmployeeDO employeeDO = new EmployeeDO();
            BeanUtils.copyProperties(principal, employeeDO);
            loanBaseInfoDO.setSalesmanId(employeeDO.getId());

            // 合伙人ID
            if (null == loanBaseInfoDO.getPartnerId()) {
                Long partnerId = partnerRelaEmployeeDOMapper.getPartnerIdByEmployeeId(employeeDO.getId());
                loanBaseInfoDO.setPartnerId(partnerId);
            }
        }

        loanBaseInfoDO.setGmtCreate(new Date());
        loanBaseInfoDO.setGmtModify(new Date());
        int count = loanBaseInfoDOMapper.insertSelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "插入贷款基本信息失败");
        return ResultBean.ofSuccess(loanBaseInfoDO.getId());
    }

    @Override
    public ResultBean<Void> update(LoanBaseInfoDO loanBaseInfoDO) {
        Preconditions.checkArgument(null != loanBaseInfoDO && null != loanBaseInfoDO.getId(), "贷款基本信息ID不能为空");

        loanBaseInfoDO.setGmtModify(new Date());
        int count = loanBaseInfoDOMapper.updateByPrimaryKeySelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null);
    }

    @Override
    public ResultBean<LoanBaseInfoVO> getLoanBaseInfoById(Long id) {
        Preconditions.checkNotNull(id, "贷款基本信息ID不能为空");

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(id);
        LoanBaseInfoVO loanBaseInfoVO = new LoanBaseInfoVO();
        BeanUtils.copyProperties(loanBaseInfoDO, loanBaseInfoVO);

        // 申请日期
        loanBaseInfoVO.setApplyDate(loanBaseInfoDO.getGmtCreate());

        // 实际贷款额
        Byte loanAmount = loanBaseInfoDO.getLoanAmount();
        if (null != loanAmount) {
            if (loanAmount == 1) {
                loanBaseInfoVO.setActualLoanAmount(String.valueOf("13万以下"));
            } else if (loanAmount == 2) {
                loanBaseInfoVO.setActualLoanAmount(String.valueOf("13~20万"));
            } else if (loanAmount == 3) {
                loanBaseInfoVO.setActualLoanAmount(String.valueOf("20万以上"));
            }
        }

        // 合伙人
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(), null);
        if (null != partnerDO) {
            BaseVO partner = new BaseVO();
            BeanUtils.copyProperties(partnerDO, partner);
            loanBaseInfoVO.setPartner(partner);
            DepartmentDO departmentDO = departmentDOMapper.selectByPrimaryKey(partnerDO.getDepartmentId(), null);
            loanBaseInfoVO.setDepartmentName(departmentDO.getName());
        }
        // 业务员
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanBaseInfoDO.getSalesmanId(), null);
        if (null != employeeDO) {
            BaseVO salesman = new BaseVO();
            BeanUtils.copyProperties(employeeDO, salesman);
            loanBaseInfoVO.setSalesman(salesman);
        }

        // 区域
        List<Long> cascadeAreaId = Lists.newArrayList();
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), null);
        BaseVO area = new BaseVO();
        if (null != baseAreaDO) {
            area.setId(baseAreaDO.getAreaId());
            String areaName = baseAreaDO.getAreaName();
            cascadeAreaId.add(baseAreaDO.getAreaId());

            if (LEVEL_CITY.equals(baseAreaDO.getLevel())) {
                BaseAreaDO parentAreaDO = baseAreaDOMapper.selectByPrimaryKey(baseAreaDO.getParentAreaId(), null);
                if (null != parentAreaDO) {
                    areaName = parentAreaDO.getAreaName() + areaName;

                    cascadeAreaId.add(parentAreaDO.getAreaId());
                    Collections.reverse(cascadeAreaId);
                }
            }
            area.setName(areaName);
        }
        loanBaseInfoVO.setArea(area);
        loanBaseInfoVO.setCascadeAreaId(cascadeAreaId);

        return ResultBean.ofSuccess(loanBaseInfoVO);
    }

    @Override
    public LoanBaseInfoDO getLoanBaseInfoByOrderId(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "订单不存在");

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        Preconditions.checkNotNull(loanBaseInfoDO, "订单基本信息不存在");

        return loanBaseInfoDO;
    }

    /**
     * 获取订单的贷款银行
     *
     * @param orderId
     * @return
     */
    @Override
    public Long getBankId(Long orderId) {

        LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoByOrderId(orderId);

        Long bankId = bankCache.getIdByName(loanBaseInfoDO.getBank());

        return bankId;
    }
}
