package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.ApprovalInfoUtil;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.MaterialAuditDO;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.MaterialAuditDOMapper;
import com.yunche.loan.service.MaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class MaterialServiceImpl implements MaterialService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private MaterialAuditDOMapper materialAuditDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private ApprovalInfoUtil approvalInfoUtil;

    @Override
    public RecombinationVO detail(Long orderId) {
        MaterialVO materialVO = loanQueryDOMapper.selectMaterial(orderId);
        if(materialVO!=null){
            ApprovalInfoVO E = approvalInfoUtil.getApprovalInfoVO(orderId,LoanProcessEnum.TELEPHONE_VERIFY.getCode());
            if(E!=null){
                materialVO.setVerify_status(E.getAction()==null?"-1":String.valueOf(E.getAction()));
                materialVO.setVerify_report(E.getInfo());
            }
        }
        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        List<UniversalMaterialRecordVO>  materials = loanQueryDOMapper.selectUniversalMaterialRecord(orderId);

        RecombinationVO<MaterialVO> recombinationVO = new RecombinationVO<MaterialVO>();
        recombinationVO.setInfo(materialVO);
        recombinationVO.setCustomers(customers);
        recombinationVO.setMaterials(materials);
        return recombinationVO;
    }

    @Override
    public void update(MaterialUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }

        Long foundationId  = loanOrderDO.getMaterialAuditId();//关联ID
        if(foundationId == null){
            //新增提交
            MaterialAuditDO V =  BeanPlasticityUtills.copy(MaterialAuditDO.class,param);
            materialAuditDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setMaterialAuditId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(materialAuditDOMapper.selectByPrimaryKey(foundationId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                MaterialAuditDO V= BeanPlasticityUtills.copy(MaterialAuditDO.class,param);
                V.setId(foundationId);
                materialAuditDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                MaterialAuditDO V= BeanPlasticityUtills.copy(MaterialAuditDO.class,param);
                V.setId(foundationId);
                materialAuditDOMapper.updateByPrimaryKeySelective(V);
            }
        }
    }
}
