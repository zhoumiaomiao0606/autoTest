package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
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

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

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

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private LitigationStateDOMapper litigationStateDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    @Override
    public RecombinationVO detail(Long orderId,Long bankRepayImpRecordId) {
        List<LoanApplyCompensationDO> list = loanApplyCompensationDOMapper.selectByOrderId(orderId);
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        CollectionNewInfoDOKey collectionNewInfoDOKey = new CollectionNewInfoDOKey();
        collectionNewInfoDOKey.setId(orderId);
        collectionNewInfoDOKey.setBankRepayImpRecordId(bankRepayImpRecordId);
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(collectionNewInfoDOKey);
        if(collectionNewInfoDO == null){
            collectionNewInfoDO = new CollectionNewInfoDO();
        }

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        universalInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);


        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setRelations(loanQueryDOMapper.selectUniversalRelationCustomer(orderId));
        recombinationVO.setOverdue(loanQueryDOMapper.selectUniversalOverdueInfo(orderId));
        recombinationVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));
        recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        recombinationVO.setCollections(loanQueryDOMapper.selectUniversalCollectionRecord(orderId));
        recombinationVO.setRepayments(loanQueryDOMapper.selectUniversalLoanRepaymentPlan(orderId));
        recombinationVO.setCustomers(customers);
        recombinationVO.setLoanApplyCompensation(list);
        recombinationVO.setCollectionNewInfoDO(collectionNewInfoDO);
        return recombinationVO;
    }

    @Override
    public VisitDoorVO isCollectionDetail(Long orderId,Long bankRepayImpRecordId) {

        LawWorkQuery lawWorkQuery = litigationDOMapper.selectLawWorkInfo(orderId);
        CollectionRecordVO collectionRecordVO = collectionRecordDOMapper.selectNewest(orderId);
        List<LoanApplyCompensationDO> list = loanApplyCompensationDOMapper.selectByOrderId(orderId);
        int num =collectionRecordDOMapper.selectNewestTotal(orderId).size();
        CollectionNewInfoDOKey collectionNewInfoDOKey = new CollectionNewInfoDOKey();
        collectionNewInfoDOKey.setId(orderId);
        collectionNewInfoDOKey.setBankRepayImpRecordId(bankRepayImpRecordId);
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(collectionNewInfoDOKey);
        if(collectionNewInfoDO ==  null){
            collectionNewInfoDO = new CollectionNewInfoDO();
        }
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        universalCarInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);


        VisitDoorVO visitDoorVO = new VisitDoorVO();
        visitDoorVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));
        visitDoorVO.setCar(universalCarInfoVO);
        visitDoorVO.setCollections(loanQueryDOMapper.selectUniversalCollectionRecord(orderId));
        visitDoorVO.setRepayments(loanQueryDOMapper.selectUniversalLoanRepaymentPlan(orderId));
        visitDoorVO.setCustomers(customers);
        visitDoorVO.setResult(lawWorkQuery);
        visitDoorVO.setCollectionRecordVO(collectionRecordVO == null?new CollectionRecordVO():collectionRecordVO);
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        visitDoorVO.setCollectionNum(num);
        visitDoorVO.setLoanApplyCompensation(list);

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
        CollectionNewInfoDOKey collectionNewInfoDOKey = new CollectionNewInfoDOKey();
        collectionNewInfoDOKey.setId(recordCollectionParam.getId());
        collectionNewInfoDOKey.setBankRepayImpRecordId(recordCollectionParam.getBankRepayImpRecordId());

        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(collectionNewInfoDOKey);
        CollectionNewInfoDO collectionNewInfoDO1 = new CollectionNewInfoDO();
        if(collectionNewInfoDO != null){
            BeanUtils.copyProperties(recordCollectionParam,collectionNewInfoDO1);
            collectionNewInfoDO1.setDispatchedDate(new Date());
            collectionNewInfoDO1.setDispatchedStaff(SessionUtils.getLoginUser().getName());
            collectionNewInfoDO1.setBankRepayImpRecordId(recordCollectionParam.getBankRepayImpRecordId());
            collectionNewInfoDOMapper.updateByPrimaryKeySelective(collectionNewInfoDO1);
        }else{
            BeanUtils.copyProperties(recordCollectionParam,collectionNewInfoDO1);
            collectionNewInfoDO1.setDispatchedDate(new Date());
            collectionNewInfoDO1.setDispatchedStaff(SessionUtils.getLoginUser().getName());
            collectionNewInfoDO1.setBankRepayImpRecordId(recordCollectionParam.getBankRepayImpRecordId());
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
