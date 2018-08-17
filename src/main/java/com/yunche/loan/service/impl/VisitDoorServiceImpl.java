package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.CarLoanHttpUtil;
import com.yunche.loan.domain.entity.CollectionNewInfoDO;
import com.yunche.loan.domain.entity.CollectionRecordDO;
import com.yunche.loan.domain.entity.LitigationStateDO;
import com.yunche.loan.domain.entity.VisitDoorDO;
import com.yunche.loan.domain.query.GpsInfoQuery;
import com.yunche.loan.domain.query.LawWorkQuery;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.domain.vo.VisitDoorVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.AuxiliaryService;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.VisitDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public VisitDoorVO detail(Long orderId,Long id) {



        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(orderId);
        LawWorkQuery lawWorkQuery = litigationDOMapper.selectLawWorkInfo(orderId);

        CollectionRecordDO collectionRecordDO = collectionRecordDOMapper.selectNewest(orderId);
        int num =collectionRecordDOMapper.selectNewestTotal(orderId).size();

        VisitDoorVO visitDoorVO = new VisitDoorVO();
        visitDoorVO.setCollectionRecordDO(collectionRecordDO);
        visitDoorVO.setCollectionNum(num);
        visitDoorVO.setResult(lawWorkQuery);
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        if(id != null){
            visitDoorVO.setVisitDoorDO(visitDoorDOMapper.selectByPrimaryKey(id));
        }
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        visitDoorVO.setCustomers(customers);
        visitDoorVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        visitDoorVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));
        visitDoorVO.setLitigationStateDO(litigationStateDOMapper.selectByIdAndType(orderId,"1"));

        return visitDoorVO;
    }

    @Override
    public VisitDoorVO cusInfoDetatil(Long orderId, Long id) {
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(orderId);
        LawWorkQuery lawWorkQuery = litigationDOMapper.selectLawWorkInfo(orderId);

        CollectionRecordDO collectionRecordDO = collectionRecordDOMapper.selectNewest(orderId);
        int num =collectionRecordDOMapper.selectNewestTotal(orderId).size();

        VisitDoorVO visitDoorVO = new VisitDoorVO();
        visitDoorVO.setCollectionRecordDO(collectionRecordDO);
        visitDoorVO.setCollectionNum(num);
        visitDoorVO.setResult(lawWorkQuery);
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        visitDoorVO.setLitigationStateDO(litigationStateDOMapper.selectByIdAndType(orderId,"1"));
        return visitDoorVO;
    }

    @Override
    public VisitDoorVO visitDoorDetatil(Long orderId, Long id) {
        CollectionNewInfoDO collectionNewInfoDO = collectionNewInfoDOMapper.selectByPrimaryKey(orderId);
        VisitDoorVO visitDoorVO =new VisitDoorVO();
        visitDoorVO.setCollectionNewInfoDO(collectionNewInfoDO);
        if(id != null){
            visitDoorVO.setVisitDoorDO(visitDoorDOMapper.selectByPrimaryKey(id));
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
    public void update(VisitDoorDO visitDoorDO) {
         if(visitDoorDO.getId() !=null){
           visitDoorDOMapper.updateByPrimaryKeySelective(visitDoorDO);
        }else{
            visitDoorDOMapper.insertSelective(visitDoorDO);
        }
    }

    @Override
    public void visitDoorRevoke(LitigationStateDO litigationStateDO) {
        litigationStateDO.setCollectionType("1");
        litigationStateDOMapper.insertSelective(litigationStateDO);
    }
}
