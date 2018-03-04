package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.dataObj.LoanHomeVisitDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LoanHomeVisitDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanHomeVisitDO record);

    int insertSelective(LoanHomeVisitDO record);

    LoanHomeVisitDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanHomeVisitDO record);

    int updateByPrimaryKey(LoanHomeVisitDO record);
}