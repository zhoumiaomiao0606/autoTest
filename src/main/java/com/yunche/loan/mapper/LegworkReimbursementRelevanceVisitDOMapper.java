package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LegworkReimbursementRelevanceVisitDO;
import com.yunche.loan.domain.entity.LegworkReimbursementRelevanceVisitDOKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LegworkReimbursementRelevanceVisitDOMapper {
    int deleteByPrimaryKey(LegworkReimbursementRelevanceVisitDOKey key);

    void deleteByLegworkReimbursementId(Long legworkReimbursementId);

    int insertSelective(LegworkReimbursementRelevanceVisitDO record);

    LegworkReimbursementRelevanceVisitDO selectByPrimaryKey(LegworkReimbursementRelevanceVisitDOKey key);

    List<LegworkReimbursementRelevanceVisitDO> selectByLegworkReimbursementId(Long legworkReimbursementId);

    int updateByPrimaryKeySelective(LegworkReimbursementRelevanceVisitDO record);

    boolean checkHaving(Long visitDoorId);
}