package com.yunche.loan.service;

import com.yunche.loan.domain.param.CollectionRecordUpdateParam;
import com.yunche.loan.domain.param.ManualDistributionParam;
import com.yunche.loan.domain.param.RecordCollectionParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCollectionRecordDetail;
import com.yunche.loan.domain.vo.UniversalTelephoneCollectionEmployee;
import com.yunche.loan.domain.vo.VisitDoorVO;

import java.util.List;

public interface CollectionService {

    RecombinationVO detail(Long orderId,Long bankRepayImpRecordId);

    VisitDoorVO isCollectionDetail(Long orderId,Long bankRepayImpRecordId);

    UniversalCollectionRecordDetail recordDetail(Long collectionId);

    void recordUpdate(CollectionRecordUpdateParam param);

    List<UniversalTelephoneCollectionEmployee> selectTelephoneCollectionEmployee();

    void autoDistribution();

    void manualDistribution(List<ManualDistributionParam> params);

    boolean checkCollectionUserRole();

    void recordCollection(RecordCollectionParam recordCollectionParam);
}
