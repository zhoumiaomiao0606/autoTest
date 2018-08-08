package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.EmployeeRelaBizAreaDO;
import com.yunche.loan.domain.entity.EmployeeRelaBizAreaDOKey;
import com.yunche.loan.domain.vo.EmployeeRelaBizAreaVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeRelaBizAreaDOMapper {
    List<EmployeeRelaBizAreaVO> selectByEmployeeId(Long employeeId);

    void deleteByEmployeeId(Long employeeId);

    int deleteByPrimaryKey(EmployeeRelaBizAreaDOKey key);

    int insertSelective(EmployeeRelaBizAreaDO record);

    EmployeeRelaBizAreaDO selectByPrimaryKey(EmployeeRelaBizAreaDOKey key);

    int updateByPrimaryKeySelective(EmployeeRelaBizAreaDO record);
}