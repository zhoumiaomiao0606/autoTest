package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.BankUrgeRecordDO;
import com.yunche.loan.domain.entity.CollectionNewInfoDO;
import com.yunche.loan.domain.entity.CollectionRecordDO;
import com.yunche.loan.domain.param.CollectionRecordUpdateParam;
import com.yunche.loan.domain.param.ManualDistributionParam;
import com.yunche.loan.domain.param.RecordCollectionParam;
import com.yunche.loan.domain.query.LawWorkQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.CollectionService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private CollectionRecordDOMapper collectionRecordDOMapper;

    @Resource
    private BankUrgeRecordDOMapper bankUrgeRecordDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private CollectionNewInfoDOMapper collectionNewInfoDOMapper;

    @Autowired
    private LitigationDOMapper litigationDOMapper;


    @Override
    public RecombinationVO detail(Long orderId) {
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
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
    public VisitDoorVO isCollectionDetail(Long orderId) {

        LawWorkQuery lawWorkQuery = litigationDOMapper.selectLawWorkInfo(orderId);
        CollectionRecordDO collectionRecordDO = collectionRecordDOMapper.selectNewest(orderId);
        int num =collectionRecordDOMapper.selectNewestTotal(orderId).size();
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(orderId);
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        VisitDoorVO visitDoorVO = new VisitDoorVO();
        visitDoorVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));
        visitDoorVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        visitDoorVO.setCollections(loanQueryDOMapper.selectUniversalCollectionRecord(orderId));
        visitDoorVO.setRepayments(loanQueryDOMapper.selectUniversalLoanRepaymentPlan(orderId));
        visitDoorVO.setCustomers(customers);
        visitDoorVO.setResult(lawWorkQuery);
        visitDoorVO.setCollectionRecordDO(collectionRecordDO);
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        return visitDoorVO;
    }

    @Override
    public UniversalCollectionRecordDetail recordDetail(Long collectionId) {
        return loanQueryDOMapper.selectUniversalCollectionRecordDetail(collectionId);
    }

    @Override
    public void recordUpdate(CollectionRecordUpdateParam param) {
        if (StringUtils.isBlank(param.getId())) {
            //新增
            CollectionRecordDO V = BeanPlasticityUtills.copy(CollectionRecordDO.class, param);
            collectionRecordDOMapper.insertSelective(V);
        } else {
            CollectionRecordDO V = BeanPlasticityUtills.copy(CollectionRecordDO.class, param);
            V.setId(Long.parseLong(param.getId()));
            collectionRecordDOMapper.updateByPrimaryKeySelective(V);
        }
    }

    @Override
    public void recordCollection(RecordCollectionParam recordCollectionParam) {
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(recordCollectionParam.getId());
        CollectionNewInfoDO collectionNewInfoDO1 = new CollectionNewInfoDO();
        if(collectionNewInfoDO !=null){
            BeanUtils.copyProperties(recordCollectionParam,collectionNewInfoDO1);
            collectionNewInfoDO1.setDispatchedDate(new Date());
            collectionNewInfoDO1.setDispatchedStaff(SessionUtils.getLoginUser().getName());
            collectionNewInfoDOMapper.updateByPrimaryKeySelective(collectionNewInfoDO1);
        }else{
            BeanUtils.copyProperties(recordCollectionParam,collectionNewInfoDO1);
            collectionNewInfoDO1.setDispatchedDate(new Date());
            collectionNewInfoDO1.setDispatchedStaff(SessionUtils.getLoginUser().getName());
            collectionNewInfoDOMapper.insertSelective(collectionNewInfoDO1);
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
        if (CollectionUtils.isEmpty(universalTelephoneCollectionEmployees)) {
            return;

        }

        //未分配的单子
        List<UniversalUndistributedCollection> UniversalUndistributedCollections = loanQueryDOMapper.selectUniversalUndistributedCollection();
        ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
        int i = 0;
        while (queue.size() < UniversalUndistributedCollections.size()) {
            if (i == universalTelephoneCollectionEmployees.size()) {
                i = 0;
            }
            queue.add(universalTelephoneCollectionEmployees.get(i).getId());
            i++;
        }

        for (UniversalUndistributedCollection V : UniversalUndistributedCollections) {
            BankUrgeRecordDO bankUrgeRecordDO = new BankUrgeRecordDO();
            bankUrgeRecordDO.setOrderId(Long.valueOf(V.getOrder_id()));
            bankUrgeRecordDO.setSendeeDate(new Date());
            bankUrgeRecordDO.setSendee(Long.valueOf(queue.poll().toString()));
            bankUrgeRecordDOMapper.updateByPrimaryKeySelective(bankUrgeRecordDO);
        }
    }

    @Override
    public void manualDistribution(List<ManualDistributionParam> params) {
        //分配开始
        for (ManualDistributionParam param : params) {
            boolean flag = false;
            //催收人员列表
            List<UniversalTelephoneCollectionEmployee> universalTelephoneCollectionEmployees = loanQueryDOMapper.selectUniversalTelephoneCollectionEmployee();
            for (UniversalTelephoneCollectionEmployee V : universalTelephoneCollectionEmployees) {
                if (V.getId().equals(param.getSendee())) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                BankUrgeRecordDO bankUrgeRecordDO = new BankUrgeRecordDO();
                bankUrgeRecordDO.setOrderId(Long.valueOf(param.getOrder_id()));
                bankUrgeRecordDO.setSendeeDate(new Date());
                bankUrgeRecordDO.setSendee(Long.valueOf(param.getSendee()));
                bankUrgeRecordDOMapper.updateByPrimaryKeySelective(bankUrgeRecordDO);
            }
        }
    }

    @Override
    public boolean checkCollectionUserRole() {
        return loanQueryDOMapper.checkCollectionUserRole(SessionUtils.getLoginUser().getId());
    }


}
