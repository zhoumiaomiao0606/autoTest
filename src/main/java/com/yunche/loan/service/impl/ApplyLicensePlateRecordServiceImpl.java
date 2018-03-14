package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.ApplyLicensePlateRecordDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.ApplyLicensePlateRecordUpdateParam;
import com.yunche.loan.mapper.ApplyLicensePlateRecordDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.ApplyLicensePlateRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Transactional
public class ApplyLicensePlateRecordServiceImpl implements ApplyLicensePlateRecordService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private ApplyLicensePlateRecordDOMapper applyLicensePlateRecordDOMapper;



    @Override
    public Map detail(String order_id) {
        return null;
    }

    @Override
    public void update(ApplyLicensePlateRecordUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }

        Long foundationId  = loanOrderDO.getApplyLicensePlateRecordId();//关联ID
        if(foundationId == null){
            //新增提交
            ApplyLicensePlateRecordDO V =  BeanPlasticityUtills.copy(ApplyLicensePlateRecordDO.class,param);
            applyLicensePlateRecordDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setApplyLicensePlateRecordId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(applyLicensePlateRecordDOMapper.selectByPrimaryKey(foundationId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                ApplyLicensePlateRecordDO V= BeanPlasticityUtills.copy(ApplyLicensePlateRecordDO.class,param);
                V.setId(foundationId);
                applyLicensePlateRecordDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                ApplyLicensePlateRecordDO V= BeanPlasticityUtills.copy(ApplyLicensePlateRecordDO.class,param);
                V.setId(foundationId);
                applyLicensePlateRecordDOMapper.updateByPrimaryKeySelective(V);

            }
        }
    }

}
