package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VisitDoorDO;
import com.yunche.loan.domain.query.GpsInfoQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VisitDoorDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VisitDoorDO record);

    int insertSelective(VisitDoorDO record);

    VisitDoorDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VisitDoorDO record);

    int updateByPrimaryKey(VisitDoorDO record);

    List<GpsInfoQuery> selectGpsInfo(@Param("orderid")Long orderid);

    List<VisitDoorDO> selectByOrderId(Long orderId);

    List<VisitDoorDO> selectByOrderIdAndBankRepayImpRecordId(Long orderid, Long bankRepayImpRecordId);
}