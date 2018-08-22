package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LitigationStateDO;
import com.yunche.loan.domain.entity.VisitDoorDO;
import com.yunche.loan.domain.vo.UniversalTelephoneCollectionEmployee;
import com.yunche.loan.domain.vo.VisitDoorUpdateVO;
import com.yunche.loan.domain.vo.VisitDoorVO;

import java.util.List;

public interface VisitDoorService {
    VisitDoorVO detail(Long orderId,Long id,Long bankRepayImpRecordId);

    VisitDoorVO cusInfoDetatil(Long orderId,Long id,Long collectionNewInfoDOKey);

    VisitDoorVO visitDoorDetatil(Long orderId,Long id,Long collectionNewInfoDOKey);

    VisitDoorUpdateVO update(VisitDoorDO visitDoorDO);

    void visitDoorRevoke(LitigationStateDO litigationStateDO);

    List<UniversalTelephoneCollectionEmployee> visitDoorEmployees();

    void insertNewInfo(VisitDoorDO visitDoorDO);
}
