package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LitigationDO;
import com.yunche.loan.domain.query.LawWorkQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LitigationDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LitigationDO record);

    int insertSelective(LitigationDO record);

    LitigationDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LitigationDO record);

    int updateByPrimaryKey(LitigationDO record);

    List<LitigationDO> selectByOrderId(@Param("orderid")Long orderid,@Param("bankRepayImpRecordId")Long bankRepayImpRecordId);

    LawWorkQuery selectLawWorkInfo(@Param("orderid")Long orderid);

    LawWorkQuery selectAppVisitInfo(@Param("orderid")Long orderid);
}