package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.ApplyLicensePlateRecordDO;
import com.yunche.loan.domain.entity.CostDetailsDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.RemitDetailsDO;
import com.yunche.loan.domain.param.BusinessReviewCalculateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;
import com.yunche.loan.mapper.CostDetailsDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.RemitDetailsDOMapper;
import com.yunche.loan.service.BusinessReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

@Service
@Transactional
public class BusinessReviewServiceImpl implements BusinessReviewService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private CostDetailsDOMapper costDetailsDOMapper;

    @Resource
    private RemitDetailsDOMapper remitDetailsDOMapper;

    @Override
    public Map detail(Long orderId) {
        return null;
    }

    @Override
    public void update(BusinessReviewUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }
        Long costDetailsId  = loanOrderDO.getCostDetailsId();//关联ID


        if(costDetailsId == null){
            //新增提交
            CostDetailsDO V =  BeanPlasticityUtills.copy(CostDetailsDO.class,param);
            costDetailsDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setCostDetailsId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(costDetailsDOMapper.selectByPrimaryKey(costDetailsId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                CostDetailsDO V= BeanPlasticityUtills.copy(CostDetailsDO.class,param);
                V.setId(costDetailsId);
                costDetailsDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                CostDetailsDO V= BeanPlasticityUtills.copy(CostDetailsDO.class,param);
                V.setId(costDetailsId);
                costDetailsDOMapper.updateByPrimaryKeySelective(V);
            }
        }


        Long remitDetailsId  = loanOrderDO.getRemitDetailsId();//关联ID

        if(remitDetailsId == null){
            //新增提交
            RemitDetailsDO V =  BeanPlasticityUtills.copy(RemitDetailsDO.class,param);
            remitDetailsDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setApplyLicensePlateRecordId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(remitDetailsDOMapper.selectByPrimaryKey(remitDetailsId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                RemitDetailsDO V= BeanPlasticityUtills.copy(RemitDetailsDO.class,param);
                V.setId(remitDetailsId);
                remitDetailsDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                RemitDetailsDO V= BeanPlasticityUtills.copy(RemitDetailsDO.class,param);
                V.setId(remitDetailsId);
                remitDetailsDOMapper.updateByPrimaryKeySelective(V);

            }
        }
    }

    @Override
    public BigDecimal calculate(BusinessReviewCalculateParam param) {
        return null;
    }


}
