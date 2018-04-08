package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessLogDO;
import com.yunche.loan.domain.entity.MaterialAuditDO;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.MaterialAuditDOMapper;
import com.yunche.loan.service.LoanProcessLogService;
import com.yunche.loan.service.MaterialService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.yunche.loan.config.constant.LoanProcessEnum.TELEPHONE_VERIFY;

@Service
public class MaterialServiceImpl implements MaterialService {

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private MaterialAuditDOMapper materialAuditDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanProcessLogService loanProcessLogService;

    @Override
    public RecombinationVO detail(Long orderId) {
        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        for(UniversalCustomerVO universalCustomerVO:customers){
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setRelations(loanQueryDOMapper.selectUniversalRelationCustomer(orderId));
        recombinationVO.setLoan(loanQueryDOMapper.selectUniversalLoanInfo(orderId));
        recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        recombinationVO.setCredits(loanQueryDOMapper.selectUniversalCreditInfo(orderId));
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_telephone_verify",orderId));
        recombinationVO.setSupplement(loanQueryDOMapper.selectUniversalSupplementInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalMaterialRecord(orderId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    @Transactional
    public void update(MaterialUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()), new Byte("0"));
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }

        Long foundationId = loanOrderDO.getMaterialAuditId();//关联ID
        if (foundationId == null) {
            //新增提交
            MaterialAuditDO V = BeanPlasticityUtills.copy(MaterialAuditDO.class, param);
            materialAuditDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setMaterialAuditId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {
            if (materialAuditDOMapper.selectByPrimaryKey(foundationId) == null) {
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                MaterialAuditDO V = BeanPlasticityUtills.copy(MaterialAuditDO.class, param);
                V.setId(foundationId);
                materialAuditDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            } else {
                //代表存在
                //进行更新
                MaterialAuditDO V = BeanPlasticityUtills.copy(MaterialAuditDO.class, param);
                V.setId(foundationId);
                materialAuditDOMapper.updateByPrimaryKeySelective(V);
            }
        }
    }
}
