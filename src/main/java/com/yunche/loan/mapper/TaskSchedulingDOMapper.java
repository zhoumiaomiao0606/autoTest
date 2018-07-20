package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.ScheduleTaskQuery;
import com.yunche.loan.domain.query.TaskListQuery;

import com.yunche.loan.domain.vo.ScheduleTaskVO;
import com.yunche.loan.domain.vo.TaskListVO;
import org.springframework.validation.annotation.Validated;


import java.util.List;

public interface TaskSchedulingDOMapper {

    List<ScheduleTaskVO> selectScheduleTaskList(@Validated ScheduleTaskQuery param);

    List<TaskListVO> selectAppTaskList(@Validated AppTaskListQuery query);

    List<TaskListVO> selectTaskList(@Validated TaskListQuery query);

    /**
     * 资料流转 列表查询
     *
     * @param query
     * @return
     */
    List<TaskListVO> selectDataFlowTaskList(@Validated TaskListQuery query);

    Long selectTelephoneVerifyLevel(Long employeeId);

    Long selectCollectionLevel(Long employeeId);

    Long selectFinanceLevel(Long employeeId);

    Long selectMaxGroupLevel(Long employeeId);

    Long selectFinanceApplyLevel(Long employeeId);

    Long selectRefundApplyLevel(Long employeeId);

    Long selectMaterialSupplementLevel(Long employeeId);
}
