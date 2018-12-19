package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.CashierAccountConfDO;
import com.yunche.loan.domain.param.QueryCashierAccountConfParam;
import com.yunche.loan.domain.vo.CashierAccountConfVO;
import com.yunche.loan.domain.vo.CashierEmployName;

import java.util.List;

public interface CashierAccountConfDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CashierAccountConfDO record);

    int insertSelective(CashierAccountConfDO record);

    CashierAccountConfDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CashierAccountConfDO record);

    int updateByPrimaryKey(CashierAccountConfDO record);

    List<CashierAccountConfVO> listAll(QueryCashierAccountConfParam queryCashierAccountConfParam);

    List<CashierEmployName> listAllEmployName();

    List<String> listAllCreateUserName();

    List<CashierAccountConfDO> listAllCashierAccountConfByEmployeeId(Long employeeId);

    void deleteByEmployeeId(Long employeeId);
}