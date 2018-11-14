package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.SecondHandCarEvaluateDO;
import com.yunche.loan.domain.param.EvaluateListParam;
import com.yunche.loan.domain.param.QueryVINParam;
import com.yunche.loan.domain.vo.SecondHandCarEvaluateList;

import java.util.List;

public interface SecondHandCarEvaluateDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SecondHandCarEvaluateDO record);

    int insertSelective(SecondHandCarEvaluateDO record);

    SecondHandCarEvaluateDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SecondHandCarEvaluateDO record);

    int updateByPrimaryKey(SecondHandCarEvaluateDO record);

    List<SecondHandCarEvaluateDO> queryVIN(QueryVINParam vin);

    List<SecondHandCarEvaluateList> evaluateList(EvaluateListParam param);
}