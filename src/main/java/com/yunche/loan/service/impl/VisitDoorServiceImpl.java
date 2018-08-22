package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.CarLoanHttpUtil;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.query.GpsInfoQuery;
import com.yunche.loan.domain.query.LawWorkQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.AuxiliaryService;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VisitDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VisitDoorServiceImpl implements VisitDoorService {
    @Autowired
    private VisitDoorDOMapper visitDoorDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LitigationDOMapper litigationDOMapper;

    @Autowired
    private CollectionRecordDOMapper collectionRecordDOMapper;

    @Autowired
    private CollectionNewInfoDOMapper collectionNewInfoDOMapper;

    @Autowired
    private LitigationStateDOMapper litigationStateDOMapper;

    @Autowired
    private AuxiliaryService auxiliaryService;

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Override
    public VisitDoorVO detail(Long orderId,Long id,Long bankRepayImpRecordId) {
        List<LoanApplyCompensationDO> list = loanApplyCompensationDOMapper.selectByOrderId(orderId);

        CollectionNewInfoDOKey collectionNewInfoDOKey = new CollectionNewInfoDOKey();
        collectionNewInfoDOKey.setId(orderId);
        collectionNewInfoDOKey.setBankRepayImpRecordId(bankRepayImpRecordId);
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(collectionNewInfoDOKey);
        if(collectionNewInfoDO ==  null){
            collectionNewInfoDO = new CollectionNewInfoDO();
        }
        LawWorkQuery lawWorkQuery = litigationDOMapper.selectLawWorkInfo(orderId);

        CollectionRecordVO collectionRecordVO = collectionRecordDOMapper.selectNewest(orderId);
        int num =collectionRecordDOMapper.selectNewestTotal(orderId).size();

        VisitDoorVO visitDoorVO = new VisitDoorVO();
        visitDoorVO.setCollectionRecordVO(collectionRecordVO);
        visitDoorVO.setCollectionNum(num);
        visitDoorVO.setResult(lawWorkQuery);
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        if(id != null){
           // VisitDoorDO v = visitDoorDOMapper.selectByOrderIdAndRecordId(orderId,bankRepayImpRecordId);
            VisitDoorDO v = visitDoorDOMapper.selectByPrimaryKey(id);
            visitDoorVO.setVisitDoorDO(v == null ? new VisitDoorDO():v);
        }else{
            visitDoorVO.setVisitDoorDO(new VisitDoorDO());
        }
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        visitDoorVO.setCustomers(customers);
        visitDoorVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        visitDoorVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));
        visitDoorVO.setLitigationStateDO(litigationStateDOMapper.selectByIdAndType(orderId,"1",bankRepayImpRecordId));
        visitDoorVO.setLoanApplyCompensation(list);
        return visitDoorVO;
    }

    @Override
    public VisitDoorVO cusInfoDetatil(Long orderId, Long id,Long bankRepayImpRecordId) {
        CollectionNewInfoDOKey collectionNewInfoDOKey = new CollectionNewInfoDOKey();
        collectionNewInfoDOKey.setId(orderId);
        collectionNewInfoDOKey.setBankRepayImpRecordId(bankRepayImpRecordId);
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(collectionNewInfoDOKey);
        if(collectionNewInfoDO ==  null){
            collectionNewInfoDO = new CollectionNewInfoDO();
        }
        List<LoanApplyCompensationDO> list = loanApplyCompensationDOMapper.selectByOrderId(orderId);

        LawWorkQuery lawWorkQuery = litigationDOMapper.selectAppVisitInfo(orderId);

        CollectionRecordVO collectionRecordVO = collectionRecordDOMapper.selectNewest(orderId);
        int num =collectionRecordDOMapper.selectNewestTotal(orderId).size();

        VisitDoorVO visitDoorVO = new VisitDoorVO();
        visitDoorVO.setCollectionRecordVO(collectionRecordVO);
        visitDoorVO.setCollectionNum(num);
        visitDoorVO.setResult(lawWorkQuery);
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        visitDoorVO.setLitigationStateDO(litigationStateDOMapper.selectByIdAndType(orderId,"1",bankRepayImpRecordId));
        visitDoorVO.setLoanApplyCompensation(list == null ? new ArrayList<LoanApplyCompensationDO>() : list);
        return visitDoorVO;
    }

    @Override
    public VisitDoorVO visitDoorDetatil(Long orderId, Long id,Long bankRepayImpRecordId) {
        CollectionNewInfoDOKey collectionNewInfoDOKey = new CollectionNewInfoDOKey();
        collectionNewInfoDOKey.setId(orderId);
        collectionNewInfoDOKey.setBankRepayImpRecordId(bankRepayImpRecordId);
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(collectionNewInfoDOKey);
        if(collectionNewInfoDO ==  null){
            collectionNewInfoDO = new CollectionNewInfoDO();
        }
        VisitDoorVO visitDoorVO =new VisitDoorVO();
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        if(id != null){
            VisitDoorDO v = visitDoorDOMapper.selectByPrimaryKey(id);
            visitDoorVO.setVisitDoorDO(v == null ? new VisitDoorDO():v);
        }else{
            visitDoorVO.setVisitDoorDO(new VisitDoorDO());
        }
        return visitDoorVO;
    }

    public String getGspAddress(Long orderId){
        String returnInfo = "";
        List<GpsInfoQuery> list = visitDoorDOMapper.selectGpsInfo(orderId);
        if(list.size() > 0){
            GpsInfoQuery gpsInfoQuery = list.get(0);
            String gpsCom = gpsInfoQuery.getGps_company();
            if("JIMI".equals(gpsCom)){
                returnInfo = auxiliaryService.getGpsAddress(gpsInfoQuery.getGps_number());
            }else if("CATLOAN".equals(gpsCom)){
                try {
                    List<Map<String,Object>> list1 = CarLoanHttpUtil.getGpsInfo(gpsInfoQuery.getGps_number());
                    if(list1.size()>0){
                        Map<String,Object> map = list1.get(0);
                        returnInfo += "经度:"+map.get("lng")+",纬度:"+map.get("lat");
                    }
                } catch (Exception e) {
                    return "";
                }
            }
        }
        return returnInfo;
    }

    @Override
    public VisitDoorUpdateVO update(VisitDoorDO visitDoorDO) {
        VisitDoorUpdateVO visitDoorUpdateVO = new VisitDoorUpdateVO();
         if(visitDoorDO.getId() !=null&&!"".equals(visitDoorDO.getId())){
           visitDoorDOMapper.updateByPrimaryKeySelective(visitDoorDO);
        }else{
            visitDoorDOMapper.insertSelective(visitDoorDO);
        }
        visitDoorUpdateVO.setId(visitDoorDO.getId());
         return visitDoorUpdateVO;
    }

    @Override
    public void visitDoorRevoke(LitigationStateDO litigationStateDO) {
        litigationStateDO.setCollectionType("1");
        litigationStateDOMapper.insertSelective(litigationStateDO);
    }

    @Override
    public List<UniversalTelephoneCollectionEmployee> visitDoorEmployees() {
        return loanQueryDOMapper.selectVisitDoorEmployee();
    }
}
