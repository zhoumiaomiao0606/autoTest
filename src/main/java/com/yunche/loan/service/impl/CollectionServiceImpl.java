package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.CollectionRecordDO;
import com.yunche.loan.domain.param.CollectionRecordUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.CollectionRecordDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.CollectionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private CollectionRecordDOMapper collectionRecordDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setRelations(loanQueryDOMapper.selectUniversalRelationCustomer(orderId));
        recombinationVO.setOverdue(loanQueryDOMapper.selectUniversalOverdueInfo(orderId));
        recombinationVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));
        recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        recombinationVO.setCollections(loanQueryDOMapper.selectUniversalCollectionRecord(orderId));
        recombinationVO.setRepayments(loanQueryDOMapper.selectUniversalLoanRepaymentPlan(orderId));
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    public UniversalCollectionRecordDetail recordDetail(Long collectionId) {
        return loanQueryDOMapper.selectUniversalCollectionRecordDetail(collectionId);
    }

    @Override
    public void recordUpdate(CollectionRecordUpdateParam param) {
        if(StringUtils.isBlank(param.getId())){
            //新增
            CollectionRecordDO V =  BeanPlasticityUtills.copy(CollectionRecordDO.class,param);
            collectionRecordDOMapper.insertSelective(V);
        }else{
            CollectionRecordDO V =  BeanPlasticityUtills.copy(CollectionRecordDO.class,param);
            V.setId(Long.parseLong(param.getId()));
            collectionRecordDOMapper.updateByPrimaryKeySelective(V);
        }
    }

    @Override
    public List<UniversalTelephoneCollectionEmployee> selectTelephoneCollectionEmployee() {
        return loanQueryDOMapper.selectUniversalTelephoneCollectionEmployee();
    }

    @Override
    public void autoDistribution() {
        //催收人员列表
        List<UniversalTelephoneCollectionEmployee> universalTelephoneCollectionEmployees = loanQueryDOMapper.selectUniversalTelephoneCollectionEmployee();
        //未分配的单子
        List<UniversalUndistributedCollection> UniversalUndistributedCollections = loanQueryDOMapper.selectUniversalUndistributedCollection();

        Set<String> set  = new HashSet<String>();
        for(UniversalUndistributedCollection V:UniversalUndistributedCollections){
            set.add(V.getBank());
        }





    }

    @Override
    public void manualDistribution(Long orderId, Long sendee) {

    }


}
