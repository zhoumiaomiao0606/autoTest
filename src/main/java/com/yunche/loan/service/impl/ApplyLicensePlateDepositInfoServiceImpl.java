package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.ApplyLicensePlateDepositInfoDO;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.ApplyLicensePlateDepositInfoUpdateParam;
import com.yunche.loan.domain.param.VehicleInformationUpdateParam;
import com.yunche.loan.domain.vo.ApplyLicensePlateDepositInfoVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.ApplyLicensePlateDepositInfoService;
import com.yunche.loan.service.VehicleInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

@Service
@Transactional
public class ApplyLicensePlateDepositInfoServiceImpl implements ApplyLicensePlateDepositInfoService  {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private ApplyLicensePlateDepositInfoDOMapper applyLicensePlateDepositInfoDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private VehicleInformationService vehicleInformationService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private PartnerRelaAreaDOMapper partnerRelaAreaDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {
        ApplyLicensePlateDepositInfoVO applyLicensePlateDepositInfoVO = loanQueryDOMapper.selectApplyLicensePlateDepositInfo(orderId);

        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);

        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }



        if(applyLicensePlateDepositInfoVO.getApply_license_plate_area()!=null){
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(Long.valueOf(applyLicensePlateDepositInfoVO.getApply_license_plate_area()), VALID_STATUS);
            applyLicensePlateDepositInfoVO.setHasApplyLicensePlateArea(baseAreaDO);
            String tmpApplyLicensePlateArea=null;
            if(baseAreaDO.getParentAreaName()!=null){
                tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName()+baseAreaDO.getAreaName();
            }else{
                tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
            }
            applyLicensePlateDepositInfoVO.setApply_license_plate_area(tmpApplyLicensePlateArea);
        }

        RecombinationVO<ApplyLicensePlateDepositInfoVO> recombinationVO = new RecombinationVO<ApplyLicensePlateDepositInfoVO>();
        recombinationVO.setInfo(applyLicensePlateDepositInfoVO);
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    public void update(ApplyLicensePlateDepositInfoUpdateParam param){
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }

        Long foundationId  = loanOrderDO.getApplyLicensePlateDepositInfoId();//关联ID
        if(foundationId == null){
            //新增提交
            ApplyLicensePlateDepositInfoDO V =  BeanPlasticityUtills.copy(ApplyLicensePlateDepositInfoDO.class,param);
            applyLicensePlateDepositInfoDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setApplyLicensePlateDepositInfoId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(applyLicensePlateDepositInfoDOMapper.selectByPrimaryKey(foundationId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                ApplyLicensePlateDepositInfoDO V= BeanPlasticityUtills.copy(ApplyLicensePlateDepositInfoDO.class,param);
                V.setId(foundationId);
                applyLicensePlateDepositInfoDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                ApplyLicensePlateDepositInfoDO V= BeanPlasticityUtills.copy(ApplyLicensePlateDepositInfoDO.class,param);
                V.setId(foundationId);
                applyLicensePlateDepositInfoDOMapper.updateByPrimaryKeySelective(V);

            }
        }

        vehicleInformationService.update(BeanPlasticityUtills.copy(VehicleInformationUpdateParam.class,param));
    }
}
