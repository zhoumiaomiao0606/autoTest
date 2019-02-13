package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.TaskListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TotalQueryListDOMapper {

    List<TaskListVO> selectTotalCusInfo(TaskListQuery taskListQuery);

    List<TaskListVO> selectBankCreditPend(TaskListQuery taskListQuery);

    List<Long> selectSuccessBankOrder(@Param("transCode") String transCode);

    List<Long> selectProcessBankOrder(@Param("transCode") String transCode);

    List<Long> selectApplyInstalmentException(@Param("transCode") String transCode);

    List<Long> selectApplyInstalmentProcess(@Param("transCode") String transCode);

    List<Long> selectApplyInstalmentBack(@Param("transCode") String transCode);

    //征信申请
    List<TaskListVO> selectApplyCredit(TaskListQuery taskListQuery);

    List<TaskListVO> selectCarGps(TaskListQuery taskListQuery);

    List<TaskListVO> selectVisitDoor(TaskListQuery taskListQuery);

    List<TaskListVO> selectCarInsurance(TaskListQuery taskListQuery);

    List<TaskListVO> selectSupplementInfo(TaskListQuery taskListQuery);

    List<TaskListVO> selectBusinessPay(TaskListQuery taskListQuery);

    List<TaskListVO> selectLoanReview(TaskListQuery taskListQuery);

    List<TaskListVO> selectRemitReview(TaskListQuery taskListQuery);

    List<TaskListVO> selectApplyInstalment(TaskListQuery taskListQuery);

    // 视频面签登记
    List<TaskListVO> selectLoanInfoRecordList(TaskListQuery taskListQuery);

    //提车资料
    List<TaskListVO> selectVehicleInformationList(TaskListQuery taskListQuery);

    //金融方案修改
    List<TaskListVO> selectFinancialSchemeModifyApplyList(TaskListQuery taskListQuery);

    //业务审批单
    List<TaskListVO> selectBusinessReviewList(TaskListQuery taskListQuery);

    //贷款申请
    List<TaskListVO> queryLoanApplyList(TaskListQuery taskListQuery);

    //合同套打
    List<TaskListVO> queryMaterialPrintList(TaskListQuery taskListQuery);

    //合同归档
    List<TaskListVO> queryMaterialManageList(TaskListQuery taskListQuery);

    //抵押记录
    List<TaskListVO> queryApplyLicensePlateDepositList(TaskListQuery taskListQuery);

    //业务付款申请
    List<TaskListVO> queryBusinessPayList(TaskListQuery taskListQuery);

    //业务审批单
    List<TaskListVO> queryBusinessReviewList(TaskListQuery taskListQuery);

    //放款审批单
    List<TaskListVO> queryLoanReviewList(TaskListQuery taskListQuery);

    //打款确认单
    List<TaskListVO> queryRemitReviewList(TaskListQuery taskListQuery);

    //申请开卡
    List<TaskListVO> selectBankOpenCardList(TaskListQuery taskListQuery);

    //资料审核
    //List<TaskListVO>   queryMaterialReviewList(TaskListQuery taskListQuery);

    // 电审
    List<TaskListVO> queryTelephoneVerifyList(TaskListQuery taskListQuery);


}
