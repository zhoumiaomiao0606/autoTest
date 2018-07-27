package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.InsuranceDistributeRecordDO;
import com.yunche.loan.domain.entity.InsuranceDistributeRecordDOKey;
import com.yunche.loan.domain.param.ManualDistributionParam;
import com.yunche.loan.domain.vo.InsuranceUrgeVO;
import com.yunche.loan.domain.vo.UniversalTelephoneCollectionEmployee;
import com.yunche.loan.mapper.BankRecordQueryDOMapper;
import com.yunche.loan.mapper.InsuranceDistributeRecordDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.InsuranceUrgeDistributeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class InsuranceUrgeDistributeServiceImpl implements InsuranceUrgeDistributeService {

    @Autowired
    private BankRecordQueryDOMapper bankRecordQueryDOMapper;


    @Autowired
    private InsuranceDistributeRecordDOMapper insuranceDistributeRecordDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;
    @Override
    public List list(Integer pageIndex, Integer pageSize,Byte status) {

        List<InsuranceUrgeVO> urgeVOList = bankRecordQueryDOMapper.selectInsuranceUrgeTaskList(status);

        return urgeVOList;
    }

    @Override
    public ResultBean manualDistribution(List<ManualDistributionParam> manualDistributionParams) {

        List<InsuranceDistributeRecordDO> distributionList = Lists.newArrayList();
        //分配开始
        for(ManualDistributionParam param:manualDistributionParams){
            InsuranceDistributeRecordDOKey insuranceDistributeRecordDOKey = new InsuranceDistributeRecordDOKey();
            insuranceDistributeRecordDOKey.setOrderId(Long.valueOf(param.getOrder_id()));//业务单号
            insuranceDistributeRecordDOKey.setEmployeeId(param.getSendee_id());//催保员工编号
            InsuranceDistributeRecordDO insuranceDistributeRecordDO = insuranceDistributeRecordDOMapper.selectByPrimaryKey(insuranceDistributeRecordDOKey);

            if(insuranceDistributeRecordDO==null){
                InsuranceDistributeRecordDO tmp = new InsuranceDistributeRecordDO();
                tmp.setOrderId(Long.valueOf(param.getOrder_id()));
                tmp.setDistributeDate(new Date());
                tmp.setEmployeeId(param.getSendee_id());
                tmp.setEmployeeName(param.getSendee());
                tmp.setGmtCreate(new Date());
                distributionList.add(tmp);
            }
            int batch = insuranceDistributeRecordDOMapper.insertBatch(distributionList);
            Preconditions.checkArgument(distributionList.size()!=batch,"催保分配异常");
        }
        return ResultBean.ofSuccess(null,"催保分配完成");
    }

    @Override
    public List selectInsuranceDistributeEmployee() {

        List<UniversalTelephoneCollectionEmployee> universalTelephoneCollectionEmployees = loanQueryDOMapper.selectUniversalInsuranceUrgeEmployee();

        return universalTelephoneCollectionEmployees;
    }


}
