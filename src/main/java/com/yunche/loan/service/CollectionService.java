package com.yunche.loan.service;

import com.yunche.loan.domain.param.CollectionRecordUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCollectionRecordDetail;
import com.yunche.loan.domain.vo.UniversalTelephoneCollectionEmployee;

import java.util.List;

public interface CollectionService {

    RecombinationVO detail(Long orderId);

    UniversalCollectionRecordDetail recordDetail(Long collectionId);

    void recordUpdate(CollectionRecordUpdateParam param);

    List<UniversalTelephoneCollectionEmployee> selectTelephoneCollectionEmployee();
}
