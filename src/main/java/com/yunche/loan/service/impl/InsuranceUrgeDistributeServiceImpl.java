package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ManualInsuranceParam;
import com.yunche.loan.domain.query.InsuranceListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.InsuranceUrgeDistributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class InsuranceUrgeDistributeServiceImpl implements InsuranceUrgeDistributeService {

    @Autowired
    private BankRecordQueryDOMapper bankRecordQueryDOMapper;


    @Autowired
    private InsuranceDistributeRecordDOMapper insuranceDistributeRecordDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Autowired
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;








    @Override
    public List list(InsuranceListQuery insuranceListQuery) {

        List<InsuranceUrgeVO> urgeVOList = bankRecordQueryDOMapper.selectInsuranceUrgeTaskList(insuranceListQuery);

        return urgeVOList;
    }

    @Override
    @Transactional
    public ResultBean manualDistribution(ManualInsuranceParam manualInsuranceParam) {

        List<InsuranceDistributeRecordDO> list = Lists.newArrayList();
        //分配开始
        for(Long orderId:manualInsuranceParam.getOrderIdList()){
            InsuranceDistributeRecordDOKey insuranceDistributeRecordDOKey = new InsuranceDistributeRecordDOKey();
            insuranceDistributeRecordDOKey.setOrderId(orderId);//业务单号
            insuranceDistributeRecordDOKey.setEmployeeId(manualInsuranceParam.getSendeeId());//催保员工编号
            InsuranceDistributeRecordDO insuranceDistributeRecordDO = insuranceDistributeRecordDOMapper.selectByPrimaryKey(insuranceDistributeRecordDOKey);

            if(insuranceDistributeRecordDO==null){
                InsuranceDistributeRecordDO tmp = new InsuranceDistributeRecordDO();
                tmp.setOrderId(orderId);
                tmp.setDistributeDate(new Date());
                tmp.setEmployeeId(manualInsuranceParam.getSendeeId());
                tmp.setEmployeeName(manualInsuranceParam.getSendee());
                tmp.setGmtCreate(new Date());
                tmp.setInsuranceYear(manualInsuranceParam.getInsuranceYear());
                tmp.setStatus(new Byte("1"));//待处理
                list.add(tmp);
            }

        }

        int batch = insuranceDistributeRecordDOMapper.insertBatch(list);
        Preconditions.checkArgument(list.size()==batch,"催保分配异常");
        return ResultBean.ofSuccess(null,"催保分配完成");
    }

    /**
     * 催保员工
     * @return
     */
    @Override
    public List selectInsuranceDistributeEmployee() {

        List<UniversalTelephoneCollectionEmployee> universalTelephoneCollectionEmployees = loanQueryDOMapper.selectUniversalInsuranceUrgeEmployee();

        return universalTelephoneCollectionEmployees;
    }

    /**
     * 催保详情
     * @param orderId
     * @return
     */
    @Override
    public ResultBean detail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO<>();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);

        Preconditions.checkNotNull(loanOrderDO,"订单不存在");

//        UniversalCustomerDetailVO universalCustomerDetailVO = loanQueryDOMapper.selectUniversalCustomerDetail(orderId, loanOrderDO.getLoanCustomerId());

        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);

        List<InsuranceInfoDO> insuranceInfoDOS = insuranceInfoDOMapper.listByOrderId(orderId);

        List<UniversalInsuranceVO> insuranceDetail = Lists.newArrayList();
        insuranceInfoDOS.stream().forEach(e->{
            UniversalInsuranceVO universalInsuranceVO = new UniversalInsuranceVO();
            Byte year = e.getInsurance_year();
            List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.listByInsuranceInfoId(e.getId());
            universalInsuranceVO.setInsuranceYear(year);
            universalInsuranceVO.setInsuranceRele(insuranceRelevanceDOS);
            insuranceDetail.add(universalInsuranceVO);
        });

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setCar(universalCarInfoVO);
        recombinationVO.setInsuranceDetail(insuranceDetail);
        recombinationVO.setCustomers(customers);
        return ResultBean.ofSuccess(recombinationVO);
    }


}
