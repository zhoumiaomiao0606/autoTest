package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.param.LoanBaseInfoParam;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.LoanBaseInfoVO;
import com.yunche.loan.mapper.BaseAreaDOMapper;
import com.yunche.loan.mapper.EmployeeDOMapper;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.LoanBaseInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static com.yunche.loan.config.constant.AreaConst.LEVEL_CITY;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
@Service
@Transactional
public class LoanBaseInfoServiceImpl implements LoanBaseInfoService {

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private EmployeeDOMapper employeeDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    @Override
    public ResultBean<Long> create(LoanBaseInfoParam param) {
        Preconditions.checkNotNull(param.getPartnerId(), "合伙人不能为空");
        Preconditions.checkNotNull(param.getSalesmanId(), "业务员不能为空");
        Preconditions.checkNotNull(param.getAreaId(), "区域不能为空");
//        Preconditions.checkNotNull(param.getCarType(), "车辆类型不能为空");
        Preconditions.checkNotNull(param.getLoanAmount(), "预计贷款不能为空");

        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(param, loanBaseInfoDO);
        loanBaseInfoDO.setGmtCreate(new Date());
        loanBaseInfoDO.setGmtModify(new Date());
        int count = loanBaseInfoDOMapper.insertSelective(loanBaseInfoDO);
        Preconditions.checkArgument(count > 0, "插入贷款基本信息失败");
        return ResultBean.ofSuccess(loanBaseInfoDO.getId());
    }

    @Override
    public ResultBean<Void> update(LoanBaseInfoParam param) {
        Preconditions.checkArgument(null != param && null != param.getId(), "贷款基本信息ID不能为空");
        LoanBaseInfoDO loanBaseInfoDO = new LoanBaseInfoDO();
        BeanUtils.copyProperties(param, loanBaseInfoDO);
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

        // 合伙人
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(loanBaseInfoDO.getPartnerId(), null);
        BaseVO partner = new BaseVO();
        BeanUtils.copyProperties(partnerDO, partner);
        loanBaseInfoVO.setPartner(partner);

        // 业务员
        EmployeeDO employeeDO = employeeDOMapper.selectByPrimaryKey(loanBaseInfoDO.getSalesmanId(), null);
        BaseVO salesman = new BaseVO();
        BeanUtils.copyProperties(employeeDO, salesman);
        loanBaseInfoVO.setSalesman(salesman);

        // 区域
        BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), null);
        BaseVO area = new BaseVO();
        if (null != baseAreaDO) {
            area.setId(baseAreaDO.getAreaId());
            String areaName = baseAreaDO.getAreaName();

            if (LEVEL_CITY.equals(baseAreaDO.getLevel())) {
                BaseAreaDO parentAreaDO = baseAreaDOMapper.selectByPrimaryKey(baseAreaDO.getParentAreaId(), null);
                if (null != parentAreaDO) {
                    areaName = parentAreaDO.getAreaName() + areaName;
                }
            }
            area.setName(areaName);
        }
        loanBaseInfoVO.setArea(area);

        return ResultBean.ofSuccess(loanBaseInfoVO);
    }
}
