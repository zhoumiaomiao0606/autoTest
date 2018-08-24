package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.TallyOrderResultService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-08-14 10:33
 * @description: 结清订单结果service实现类
 **/
@Service
@Transactional
public class TallyOrderResultServiceImpl implements TallyOrderResultService {


    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Autowired
    private InsuranceRiskDOMapper insuranceRiskDOMapper;

    @Autowired
    private CollectionNewInfoDOMapper collectionNewInfoDOMapper;

    @Autowired
    private VisitDoorDOMapper visitDoorDOMapper;

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private OrderHandleResultDOMapper orderHandleResultDOMapper;

    @Autowired
    private LegworkReimbursementDOMapper legworkReimbursementDOMapper;


    @Override
    public TallyOrderResultVO detail(Long orderId) {
        TallyOrderResultVO tallyOrderResultVO = new TallyOrderResultVO();

        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        //TODO
        //客户主要信息
        BaseCustomerInfoVO baseCustomerInfoVO = loanQueryDOMapper.selectBaseCustomerInfoInfo(orderId);
        //金融方案
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

        FinancialSchemeToTallyOrderVO financialSchemeToTallyOrderVO = new FinancialSchemeToTallyOrderVO();

        BeanUtils.copyProperties(financialSchemeVO, financialSchemeToTallyOrderVO);

        //业务审批单信息
        UniversalCostDetailsVO universalCostDetailsVO = loanQueryDOMapper.selectUniversalCostDetails(orderId);
        //抵押情况
        MortgageInfoVO mortgageInfoVO = loanQueryDOMapper.selectMortgageInfo(orderId);
        //最新保险信息
        List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.selectInsuranceInfoByOrderId(orderId);
        //出险记录
        List<InsuranceRiskDO> insuranceRiskDOS = insuranceRiskDOMapper.allRiskInfoByOrderId(orderId);
        //逾期代偿---repayment_record
        List<LoanApplyCompensationDO> loanApplyCompensationDOS = loanApplyCompensationDOMapper.selectByOrderId(orderId);


        //拖车概况---相关费用报销单里取--拖车时间从流程日志表里取
        List<TrailVehicleDetailVO> trailVehicleDetailVOS = new ArrayList<>();
        //根据orderId查询出所有版本下的申请拖车记录
        List<CollectionNewInfoDO> collectionNewInfoDOs = collectionNewInfoDOMapper.selectByOrderId(orderId);
        //根据orderId和版本号查询所有的拖车记录
        collectionNewInfoDOs.stream().forEach(collectionNewInfoDO ->
                {
                    List<VisitDoorDO> visitDoorDOs = visitDoorDOMapper.selectByOrderIdAndBankRepayImpRecordId(collectionNewInfoDO.getId(), collectionNewInfoDO.getBankRepayImpRecordId());
                    visitDoorDOs.stream().forEach(visitDoorDO ->
                    {
                        TrailVehicleDetailVO trailVehicleDetailVO = new TrailVehicleDetailVO();
                        //时间戳转字符串
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (visitDoorDO.getDispatchedDate() != null) {
                            trailVehicleDetailVO.setApplyTrailVehicleDate(simpleDateFormat.format(visitDoorDO.getDispatchedDate()));
                        }
                        if (visitDoorDO.getDispatchedDate() != null) {
                            trailVehicleDetailVO.setTrailVehicleDate(simpleDateFormat.format(visitDoorDO.getVisitDate()));
                        }
                        trailVehicleDetailVO.setTrailVehicleResult(visitDoorDO.getVisitResult());
                        LegworkReimbursementDO legworkReimbursementDO = legworkReimbursementDOMapper.selectByPrimaryKey(visitDoorDO.getId());
                        if (legworkReimbursementDO != null) {
                            trailVehicleDetailVO.setRelationFee(legworkReimbursementDO.getReimbursementAmount());
                        }

                        trailVehicleDetailVOS.add(trailVehicleDetailVO);

                    });
                }
        );


        //贷款业务详细信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        //业务操作日志

        tallyOrderResultVO.setBaseCustomerInfoVO(baseCustomerInfoVO);
        tallyOrderResultVO.setFinancialSchemeVO(financialSchemeVO);
        tallyOrderResultVO.setUniversalCostDetailsVO(universalCostDetailsVO);
        tallyOrderResultVO.setMortgageInfoVO(mortgageInfoVO);
        tallyOrderResultVO.setInsuranceRelevanceDOS(insuranceRelevanceDOS);
        //拖车记录
        tallyOrderResultVO.setTrailVehicleDetailVOs(trailVehicleDetailVOS);
        //逾期代偿
        tallyOrderResultVO.setLoanApplyCompensationDOS(loanApplyCompensationDOS);
        //出险
        tallyOrderResultVO.setInsuranceRiskDOS(insuranceRiskDOS);
        tallyOrderResultVO.setCustomers(customers);
        return tallyOrderResultVO;
    }

    @Override
    public ResultBean<Void> update(OrderHandleResultDO param) {
        Preconditions.checkNotNull(param.getOrderid(), "订单号不能为空");

        OrderHandleResultDO existDO = orderHandleResultDOMapper.selectByPrimaryKey(param.getOrderid());
        if (null == existDO) {
            int count = orderHandleResultDOMapper.insert(param);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            int count = orderHandleResultDOMapper.updateByPrimaryKey(param);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }

    /**
     * @Author: ZhongMingxiao
     * @Param:
     * @return:
     * @Date:
     * @Description: 模糊查询客户信息
     */
    @Override
    public List<CustomerOrderVO> CustomerOrder(String name) {
        return loanQueryDOMapper.selectCustomerOrder(name);
    }
}
