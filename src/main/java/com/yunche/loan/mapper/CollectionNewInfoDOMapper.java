package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CollectionNewInfoDO;
import com.yunche.loan.domain.entity.CollectionNewInfoDOKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectionNewInfoDOMapper
{
    int deleteByPrimaryKey(CollectionNewInfoDOKey key);

    int insert(CollectionNewInfoDO record);

    int insertSelective(CollectionNewInfoDO record);

    CollectionNewInfoDO selectByPrimaryKey(CollectionNewInfoDOKey key);

    int updateByPrimaryKeySelective(CollectionNewInfoDO record);

    int updateByPrimaryKey(CollectionNewInfoDO record);

    //根据orderId查询出所有版本下的申请拖车记录
    List<CollectionNewInfoDO> selectByOrderId(Long orderId);

    void isvisitback(@Param("orderId")Long orderId,@Param("bankRepayImpRecordId") Long bankRepayImpRecordId);

    void islawback(@Param("orderId")Long orderId,@Param("bankRepayImpRecordId") Long bankRepayImpRecordId);
}