package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.TaskListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TotalQueryListDOMapper {
    List<TaskListVO> selectTotalCusInfo(TaskListQuery taskListQuery);

    List<TaskListVO> selectBankCreditPend(TaskListQuery taskListQuery);

    List<TaskListVO> selectBankCreditExport(TaskListQuery taskListQuery);

    List<TaskListVO> selectBankCreditSuccess(TaskListQuery taskListQuery);

    List<TaskListVO> selectBankCreditAll(TaskListQuery taskListQuery);

    List<Long> selectSuccessBankOrder(@Param("transCode")String transCode);

    List<Long> selectProcessBankOrder(@Param("transCode")String transCode);

    //征信申请暂存/全部
    List<TaskListVO> selectApplyCreditExcept(TaskListQuery taskListQuery);
}
