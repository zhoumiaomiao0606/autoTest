package com.yunche.loan.mapper;


import com.yunche.loan.domain.param.QueryListParam;
import com.yunche.loan.domain.vo.QueryListVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OptTaskschedulingDOMapper
{
    List<QueryListVO> queryList(QueryListParam param);
}
