package com.yunche.loan.mapper;

import com.yunche.loan.domain.param.FlowOperationMsgParam;
import com.yunche.loan.domain.param.LegworkReimbursementParam;
import com.yunche.loan.domain.param.SubimitVisitDoorParam;
import com.yunche.loan.domain.query.AppTaskListQuery;
import com.yunche.loan.domain.query.ScheduleTaskQuery;
import com.yunche.loan.domain.query.TaskListQuery;

import com.yunche.loan.domain.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.validation.annotation.Validated;


import java.util.List;

public interface TaskSchedulingDOMapper {

    List<FlowOperationMsgListVO> selectFlowOperationMsgList(FlowOperationMsgParam param);

    List<LegworkReimbursementVO> legworkReimbursementList(LegworkReimbursementParam param);

    List<Long> selectBankInterfaceSerialOrderidList(@Validated TaskListQuery taskListQuery);

    List<ScheduleTaskVO> selectScheduleTaskList(@Validated ScheduleTaskQuery param);

    List<TaskListVO> selectAppTaskList(@Validated AppTaskListQuery query);

    List<TaskListVO> selectTaskList(@Validated TaskListQuery query);

    boolean selectRejectTask(@Param("orderId") Long orderId);

    List<SubimitVisitDoorVO> subimitVisitDoorList(SubimitVisitDoorParam param);

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
