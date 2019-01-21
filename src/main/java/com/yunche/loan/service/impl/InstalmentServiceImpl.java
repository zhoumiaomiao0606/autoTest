package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.TermFileEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.VehicleInformationDO;
import com.yunche.loan.domain.param.InstalmentUpdateParam;
import com.yunche.loan.domain.param.UniversalFileParam;
import com.yunche.loan.domain.vo.ApplyDiviGeneralInfoVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.InstalmentService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.yunche.loan.config.constant.LoanFileEnum.CAR_INVOICE;
import static com.yunche.loan.config.constant.LoanFileEnum.S9016;

@Service
@Transactional
public class InstalmentServiceImpl implements InstalmentService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private VehicleInformationDOMapper vehicleInformationDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {

        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (orderDO == null) {
            throw new BizException("此订单不存在");
        }

//        set.add(S9016.getType());
//        set.add(CAR_INVOICE.getType());
//        List list = loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, set);

        Set<Byte> types = new HashSet<>();
        for (TermFileEnum e : TermFileEnum.values()) {
            types.add(e.getKey());
        }


        RecombinationVO<ApplyDiviGeneralInfoVO> recombinationVO = new RecombinationVO<>();
        recombinationVO.setInfo(loanQueryDOMapper.selectApplyDiviGeneralInfo(orderId));
        recombinationVO.setMaterials(loanQueryDOMapper.selectUniversalCustomerFileByTypes(orderId, types));
        recombinationVO.setVideoFace(loanQueryService.selectVideoFaceLog(orderId));

        //

        return recombinationVO;
    }

    @Override
    public void update(InstalmentUpdateParam param) {
        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));

        if (orderDO == null) {
            throw new BizException("此订单不存在");
        }
        //更新基准评估价
        Long financialPlanId = orderDO.getLoanFinancialPlanId();
        if (financialPlanId == null) {
            throw new BizException("金融方案信息不存在");
        } else {
            LoanFinancialPlanDO financialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(financialPlanId);
            if (param.getAppraisal() != null) {
                financialPlanDO.setAppraisal(param.getAppraisal());
                int count = loanFinancialPlanDOMapper.updateByPrimaryKeySelective(financialPlanDO);
                Preconditions.checkArgument(count > 0, "更新产品基准评估价失败");
            }
        }

        Long customerId = orderDO.getLoanCustomerId();
        if (customerId == null) {
            throw new BizException("客户不存在");
        }

        if (customerId != null) {
            if (param.getFiles() != null) {
                if (!param.getFiles().isEmpty()) {
                    for (UniversalFileParam universalFileParam : param.getFiles()) {
                        List<LoanFileDO> uploadList = loanFileDOMapper.listByCustomerIdAndType(customerId, new Byte(universalFileParam.getType()), null);
                        for (LoanFileDO loanFileDO : uploadList) {
                            loanFileDOMapper.deleteByPrimaryKey(loanFileDO.getId());
                        }
                        LoanFileDO loanFileDO = new LoanFileDO();
                        loanFileDO.setCustomerId(customerId);
                        loanFileDO.setPath(JSON.toJSONString(universalFileParam.getUrls()));
                        loanFileDO.setType(new Byte(universalFileParam.getType()));
                        loanFileDO.setUploadType(new Byte("1"));
                        loanFileDO.setGmtCreate(new Date());
                        loanFileDO.setGmtModify(new Date());
                        loanFileDO.setStatus(new Byte("0"));
                        loanFileDOMapper.insertSelective(loanFileDO);
                    }
                }
            }
        }
        VehicleInformationDO vehicleInformationDO = new VehicleInformationDO();
        vehicleInformationDO.setId(orderDO.getVehicleInformationId());
        vehicleInformationDO.setAssess_use_year(param.getVehicle_assess_use_year());
        vehicleInformationDOMapper.updateByPrimaryKeySelective(vehicleInformationDO);
    }
}
