package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.LoanDataFlowParam;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanDataFlowConst.DATA_FLOW_TASK_KEY_PREFIX;
import static com.yunche.loan.config.constant.LoanDataFlowConst.DATA_FLOW_TASK_KEY_REVIEW_SUFFIX;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.*;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_DONE;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_TODO;
import static com.yunche.loan.config.constant.LoanProcessConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.DATA_FLOW_MORTGAGE_P2C;
import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_SOCIAL_CREDIT_RECORD_FILTER;
import static com.yunche.loan.config.constant.LoanProcessEnum.CREDIT_APPLY;
import static com.yunche.loan.config.constant.ProcessApprovalConst.*;
import static com.yunche.loan.config.constant.ProcessTypeConst.*;
import static com.yunche.loan.config.thread.ThreadPool.executorService;
import static java.util.stream.Collectors.toList;

/**
 * @author liuzhe
 * @date 2018/8/21
 */
@Service
public class LoanProcessApprovalCommonServiceImpl implements LoanProcessApprovalCommonService {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessApprovalCommonServiceImpl.class);

    private static final Long AUTO_EMPLOYEE_ID = 878L;

    private static final String AUTO_EMPLOYEE_NAME = "自动任务";


    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanProcessLogDOMapper loanProcessLogDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanRejectLogDOMapper loanRejectLogDOMapper;

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private LoanProcessInsteadPayDOMapper loanProcessInsteadPayDOMapper;

    @Autowired
    private LoanProcessCollectionDOMapper loanProcessCollectionDOMapper;

    @Autowired
    private LoanProcessLegalDOMapper loanProcessLegalDOMapper;

    @Autowired
    private LoanProcessBridgeDOMapper loanProcessBridgeDOMapper;

    @Autowired
    private LoanDataFlowService loanDataFlowService;

    @Autowired
    private DictService dictService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDistributionService taskDistributionService;

    @Autowired
    private JpushService jpushService;

    @Autowired
    private CurrentNodeManagerDOMapper currentNodeManagerDOMapper;


    /**
     * 流程操作日志记录
     *
     * @param approval
     */
    @Override
    public void log(ApprovalParam approval) {

        // 是否需要日志记录
        if (!approval.isNeedLog()) {
            return;
        }

        LoanProcessLogDO loanProcessLogDO = new LoanProcessLogDO();
        BeanUtils.copyProperties(approval, loanProcessLogDO);

        EmployeeDO loginUser = null;

        // 自动任务
        if (approval.isAutoTask()) {
            loanProcessLogDO.setUserId(AUTO_EMPLOYEE_ID);
            loanProcessLogDO.setUserName(AUTO_EMPLOYEE_NAME);
        }
        // 正常人为操作
        else {

            try {
                loginUser = SessionUtils.getLoginUser();
            } catch (Exception e) {
                logger.info("自动任务|| 未登录");
            }

            if (null == loginUser) {
                loanProcessLogDO.setUserId(AUTO_EMPLOYEE_ID);
                loanProcessLogDO.setUserName(AUTO_EMPLOYEE_NAME);
            } else {
                loanProcessLogDO.setUserId(loginUser.getId());
                loanProcessLogDO.setUserName(loginUser.getName());
            }
        }

        loanProcessLogDO.setCreateTime(new Date());

        int count = loanProcessLogDOMapper.insertSelective(loanProcessLogDO);
        Preconditions.checkArgument(count > 0, "操作日志记录失败");
        updateCurrentNodeTimeAndUser(loanProcessLogDO);
    }
    public void updateCurrentNodeTimeAndUser(LoanProcessLogDO loanProcessLogDO){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if("1".equals(loanProcessLogDO.getAction()+"")){
            if("usertask_credit_apply".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskCreditApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskCreditApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskCreditApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskCreditApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_bank_credit_record".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskBankCreditRecordCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskBankCreditRecordGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskBankCreditRecordCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskBankCreditRecordGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_social_credit_record".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskSocialCreditRecordCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskSocialCreditRecordGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskSocialCreditRecordCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskSocialCreditRecordGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_loan_apply".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskLoanApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskLoanApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskLoanApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskLoanApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_visit_verify".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskVisitVerifyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskVisitVerifyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskVisitVerifyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskVisitVerifyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_telephone_verify".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskTelephoneVerifyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskTelephoneVerifyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskTelephoneVerifyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskTelephoneVerifyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_vehicle_information".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskVehicleInformationCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskVehicleInformationGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskVehicleInformationCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskVehicleInformationGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_material_review".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskMaterialReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskMaterialReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskMaterialReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskMaterialReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_material_print_review".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskMaterialPrintReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskMaterialPrintReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskMaterialPrintReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskMaterialPrintReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_install_gps".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskInstallGpsCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskInstallGpsGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskInstallGpsCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskInstallGpsGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_apply_license_plate_deposit_info".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskApplyLicensePlateDepositInfoCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskApplyLicensePlateDepositInfoGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskApplyLicensePlateDepositInfoCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskApplyLicensePlateDepositInfoGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_car_insurance".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskCarInsuranceCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskCarInsuranceGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskCarInsuranceCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskCarInsuranceGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_business_pay".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskBusinessPayCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskBusinessPayGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskBusinessPayCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskBusinessPayGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_business_review".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskBusinessReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskBusinessReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskBusinessReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskBusinessReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_loan_review".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskLoanReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskLoanReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskLoanReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskLoanReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_remit_review".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskRemitReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskRemitReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskRemitReviewCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskRemitReviewGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_material_manage".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskMaterialManageCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskMaterialManageGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskMaterialManageCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskMaterialManageGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_bank_lend_record".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskBankLendRecordCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskBankLendRecordGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskBankLendRecordCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskBankLendRecordGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_bank_card_send".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskBankCardSendCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskBankCardSendGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskBankCardSendCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskBankCardSendGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_refund_apply".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskRefundApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskRefundApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskRefundApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskRefundApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }else if("usertask_financial_scheme_modify_apply".equals(loanProcessLogDO.getTaskDefinitionKey())){
                CurrentNodeManagerDO currentNodeManagerDO = currentNodeManagerDOMapper.selectByPrimaryKey(loanProcessLogDO.getOrderId());
                if(currentNodeManagerDO == null){
                    CurrentNodeManagerDO insert = new CurrentNodeManagerDO();
                    insert.setOrderId(loanProcessLogDO.getOrderId());
                    insert.setUsertaskFinancialSchemeModifyApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    insert.setUsertaskFinancialSchemeModifyApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.insertSelective(insert);
                }else{
                    CurrentNodeManagerDO update = new CurrentNodeManagerDO();
                    update.setOrderId(loanProcessLogDO.getOrderId());
                    update.setUsertaskFinancialSchemeModifyApplyCreateTime(sdf.format(loanProcessLogDO.getCreateTime()));
                    update.setUsertaskFinancialSchemeModifyApplyGmtUserName(loanProcessLogDO.getUserName());
                    currentNodeManagerDOMapper.updateByPrimaryKeySelective(update);
                }
            }
        }
    }

    /**
     * 获取当前执行任务（activiti中）
     *
     * @param processInstId
     * @param taskDefinitionKey
     * @return
     */
    @Override
    public Task getTask(String processInstId, String taskDefinitionKey) {

        // 获取当前流程task
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstId)
                .taskDefinitionKey(taskDefinitionKey)
                .singleResult();

        Preconditions.checkNotNull(task, "当前任务不存在");

        return task;
    }

    /**
     * 获取当前待执行任务列表
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public List<Task> getCurrentTaskList(String processInstanceId) {

        List<Task> currentTaskList = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

        return currentTaskList;
    }

    /**
     * 获取当前待执行任务ID列表
     *
     * @param processInstanceId
     * @return
     */
    @Override
    public List<String> getCurrentTaskIdList(String processInstanceId) {

        List<Task> currentTaskList = getCurrentTaskList(processInstanceId);

        if (!CollectionUtils.isEmpty(currentTaskList)) {

            List<String> currentTaskIdList = currentTaskList.stream()
                    .filter(Objects::nonNull)
                    .map(Task::getId)
                    .collect(toList());

            return currentTaskIdList;
        }

        return null;
    }

    /**
     * [领取]完成  -相邻节点
     *
     * @param approval
     * @param startTaskIdList
     * @param processInstId
     */
    @Override
    public void finishTask(ApprovalParam approval, List<String> startTaskIdList, String processInstId) {

        Byte action = approval.getAction();

//        // 1、打回 -> 自动释放所有回滚的任务
//
//        // REJECT || ROLL_BACK
//        if (ACTION_REJECT_MANUAL.equals(action) || ACTION_REJECT_AUTO.equals(action)
//                || ACTION_ROLL_BACK.equals(action)) {
//
//            // 跨节点打回
//            if (!isBack_B2A) {
//
//                Preconditions.checkArgument(!CollectionUtils.isEmpty(reject2Tasks), "跨节点打回，跨节点任务列表：reject2Tasks不能为空");
//
//                // 以当前orderId为taskId                  跨区间节点为1->N时，暂不支持！！！
//                Long taskId = approval.getOrderId();
//                Long orderId = approval.getOrderId();
//
//                // open - reject2Tasks
//                taskDistributionService.rejectFinish(taskId, orderId, reject2Tasks);
//            }
//
//            // 相邻节点打回
//            else {
//
//                // 相邻节点列表
//                List<String> newTaskKeyList = getNewTaskKeyList(processInstId, startTaskIdList);
//
//                if (!CollectionUtils.isEmpty(newTaskKeyList)) {
//
//                    // open - reject2Tasks
//                    taskDistributionService.rejectFinish(approval.getTaskId(), approval.getOrderId(), newTaskKeyList);
//                }
//            }
//        }

        // 1、currentTask

        // 当前有taskId，且为：PASS    -->  处理当前任务
        if (null != approval.getTaskId() && ACTION_PASS.equals(action)) {

            // pass-当前task
            taskDistributionService.finish(approval.getTaskId(), approval.getOrderId(), approval.getTaskDefinitionKey());
        }


        // 2、newTasks

        // open -> 新产生的任务    (如果新任务是：过去已存在(即被打回过)，一律OPEN)
        List<String> newTaskKeyList = getNewTaskKeyList(processInstId, startTaskIdList);

        // open -> 被打回过的Tasks
        taskDistributionService.rejectFinish(approval.getTaskId(), approval.getOrderId(), newTaskKeyList);
    }

    /**
     * 异步推送
     *
     * @param loanOrderDO
     * @param approval
     */
    @Override
    public void asyncPush(LoanOrderDO loanOrderDO, ApprovalParam approval) {

        if (!approval.isNeedPush()) {
            return;
        }

        executorService.execute(() -> {

            LoanBaseInfoDO loanBaseInfoDO = getLoanBaseInfoDO(loanOrderDO.getLoanBaseInfoId());
            Long loanCustomerId = null;
            if (null != loanOrderDO) {
                loanCustomerId = loanOrderDO.getLoanCustomerId();
            }
            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanCustomerId, null);

            if (null != loanBaseInfoDO && !CREDIT_APPLY.getCode().equals(approval.getTaskDefinitionKey())) {

                String title = "你有一个新的消息";
                String prompt = "你提交的订单被管理员审核啦";
                String msg = "详细信息请联系管理员";

                String taskName = LoanProcessEnum.getNameByCode(approval.getTaskDefinitionKey());
                // 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补  / 4-新增任务  / 5-反审
                String result = "[异常]";
                switch (approval.getAction().intValue()) {
                    case 0:
                        result = "[已打回]";
                        break;
                    case 1:
                        result = "[已通过]";
                        break;
                    case 2:
                        result = "[已弃单]";
                        break;
                    case 3:
                        result = "[发起资料增补]";
                        break;
                    case 4:
                        result = "[新增任务]";
                        break;
                    case 5:
                        result = "[发起反审]";
                        break;
                    default:
                        result = "[异常]";
                }
                title = taskName + result;

                if (null != loanCustomerDO) {
                    prompt = "主贷人:[" + loanCustomerDO.getName() + "]-" + title;
                }
                msg = StringUtils.isBlank(approval.getInfo()) ? "无" : "null".equals(approval.getInfo()) ? "无" : approval.getInfo();

                FlowOperationMsgDO DO = new FlowOperationMsgDO();
                DO.setEmployeeId(loanBaseInfoDO.getSalesmanId());
                DO.setOrderId(loanOrderDO.getId());
                DO.setTitle(title);
                DO.setPrompt(prompt);
                DO.setMsg(msg);

                EmployeeDO loginUser = null;
                try {

                    loginUser = SessionUtils.getLoginUser();
                } catch (Exception ex) {
                    logger.info("自动任务 || 未登录");
                }

                if (null == loginUser) {

                    // 自动任务
                    DO.setSender(AUTO_EMPLOYEE_NAME);

                } else {

                    String loginUserName = loginUser.getName();
                    DO.setSender(loginUserName);
                }

                DO.setProcessKey(approval.getTaskDefinitionKey());
                DO.setSendDate(new Date());
                DO.setReadStatus(VALID_STATUS);
                DO.setType(approval.getAction());

                jpushService.push(DO);
            }

        });
    }

    /**
     * 打回记录
     *
     * @param newTaskList
     * @param orderId
     * @param rejectOriginTask
     * @param reason
     */
    @Override
    public void createRejectLog(List<Task> newTaskList, Long orderId, String rejectOriginTask, String reason) {

        if (!CollectionUtils.isEmpty(newTaskList)) {

            newTaskList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        String rejectToTask = getRejectToTask(e.getTaskDefinitionKey());

                        LoanRejectLogDO loanRejectLogDO = new LoanRejectLogDO();
                        loanRejectLogDO.setOrderId(orderId);
                        loanRejectLogDO.setRejectOriginTask(rejectOriginTask);
                        loanRejectLogDO.setRejectToTask(rejectToTask);
                        loanRejectLogDO.setReason(reason);
                        loanRejectLogDO.setGmtCreate(new Date());

                        int count = loanRejectLogDOMapper.insertSelective(loanRejectLogDO);
                        Preconditions.checkArgument(count > 0, "打回记录失败");
                    });
        }
    }

    @Override
    public LoanProcessDO_ getLoanProcess_(Long orderId, Long processId, String taskDefinitionKey) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");
        Preconditions.checkNotNull(taskDefinitionKey, "taskDefinitionKey不能为空");

        LoanProcessDO_ loanProcessDO_ = null;

        // 1 -> 1
        if (null == processId) {

            boolean isLoanProcessTask = !LOAN_PROCESS_INSTEAD_PAY_KEYS.contains(taskDefinitionKey)
                    && !LOAN_PROCESS_COLLECTION_KEYS.contains(taskDefinitionKey)
                    && !LOAN_PROCESS_LEGAL_KEYS.contains(taskDefinitionKey)
                    && !LOAN_PROCESS_BRIDGE_PAY_KEYS.contains(taskDefinitionKey);

            // 消费贷
            if (isLoanProcessTask) {

                loanProcessDO_ = loanProcessDOMapper.selectByPrimaryKey(orderId);

            } else {

                Preconditions.checkNotNull(processId, "processId不能为空");
            }
        }

        // 1 -> 多
        else {

            // 代偿流程
            if (LOAN_PROCESS_INSTEAD_PAY_KEYS.contains(taskDefinitionKey)) {

                loanProcessDO_ = loanProcessInsteadPayDOMapper.selectByPrimaryKey(processId);
            }
            // 上门催收流程
            else if (LOAN_PROCESS_COLLECTION_KEYS.contains(taskDefinitionKey)) {

                loanProcessDO_ = loanProcessCollectionDOMapper.selectByPrimaryKey(processId);
            }
            // 法务处理流程
            else if (LOAN_PROCESS_LEGAL_KEYS.contains(taskDefinitionKey)) {

                loanProcessDO_ = loanProcessLegalDOMapper.selectByPrimaryKey(processId);
            }
            // 过桥资金流程
            else if (LOAN_PROCESS_BRIDGE_PAY_KEYS.contains(taskDefinitionKey)) {

                loanProcessDO_ = loanProcessBridgeDOMapper.selectByPrimaryKey(processId);
            } else {

                throw new BizException("taskDefinitionKey有误");
            }
        }

        Preconditions.checkNotNull(loanProcessDO_, "流程记录丢失");

        return loanProcessDO_;
    }

    /**
     * 获取 订单流程节点 实时状态记录
     *
     * @param orderId
     * @return
     */
    @Override
    public LoanProcessDO getLoanProcess(Long orderId) {

        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "流程记录丢失");

        return loanProcessDO;
    }

    /**
     * 执行 - 更新本地已执行的任务状态
     *
     * @param loanProcessDO_
     * @param taskDefinitionKey
     * @param taskProcessStatus
     */
    @Override
    public void doUpdateCurrentTaskProcessStatus(LoanProcessDO_ loanProcessDO_, String taskDefinitionKey,
                                                 Byte taskProcessStatus) {
        // 方法名拼接   setXXX
        String methodBody = null;
        for (LoanProcessEnum e : LoanProcessEnum.values()) {

            if (e.getCode().equals(taskDefinitionKey)) {

                String[] keyArr = null;

                if (taskDefinitionKey.startsWith("servicetask")) {
                    keyArr = taskDefinitionKey.split("servicetask");
                } else if (taskDefinitionKey.startsWith("usertask")) {
                    keyArr = taskDefinitionKey.split("usertask");
                }

                // 下划线转驼峰
                methodBody = StringUtil.underline2Camel(keyArr[1]);
                break;
            }
        }

        // setXX
        String methodName = "set" + methodBody;

        // 反射执行
        try {

            // 获取反射对象
            Class<? extends LoanProcessDO_> loanProcessDOClass = loanProcessDO_.getClass();
            // 获取对应method
            Method method = loanProcessDOClass.getMethod(methodName, Byte.class);
            // 执行method
            Object result = method.invoke(loanProcessDO_, taskProcessStatus);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void checkOrderStatus(Long orderId, Long processId, Byte processType) {

        if (PROCESS_TYPE_LOAN_PROCESS.equals(processType)) {

            LoanProcessDO loanProcessDO = getLoanProcess(orderId);
            Preconditions.checkArgument(ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()),
                    "当前订单总状态" + getOrderStatusText(loanProcessDO));

        } else if (PROCESS_TYPE_LOAN_PROCESS_INSTEAD_PAY.equals(processType)) {

//            LoanProcessInsteadPayDO loanProcessDO = loanProcessInsteadPayDOMapper.selectByPrimaryKey(processId);
//            Preconditions.checkArgument(ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()),
//                    "当前订单子状态" + getOrderStatusText(loanProcessDO));

        } else if (PROCESS_TYPE_LOAN_PROCESS_COLLECTION.equals(processType)) {

            LoanProcessCollectionDO loanProcessDO = loanProcessCollectionDOMapper.selectByPrimaryKey(processId);
            Preconditions.checkArgument(ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()),
                    "当前订单子状态" + getOrderStatusText(loanProcessDO));

        } else if (PROCESS_TYPE_LOAN_PROCESS_LEGAL.equals(processType)) {

            LoanProcessLegalDO loanProcessDO = loanProcessLegalDOMapper.selectByPrimaryKey(processId);
            Preconditions.checkArgument(ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()),
                    "当前订单子状态" + getOrderStatusText(loanProcessDO));

        } else if (PROCESS_TYPE_LOAN_PROCESS_BRIDGE.equals(processType)) {

            LoanProcessBridgeDO loanProcessDO = loanProcessBridgeDOMapper.selectByPrimaryKey(processId);
            Preconditions.checkArgument(ORDER_STATUS_DOING.equals(loanProcessDO.getOrderStatus()),
                    "当前订单子状态" + getOrderStatusText(loanProcessDO));
        }
    }

    /**
     * 更新本地流程记录
     *
     * @param loanProcessDO
     */
    @Override
    public void updateLoanProcess(LoanProcessDO loanProcessDO) {

        loanProcessDO.setGmtModify(new Date());
        int count = loanProcessDOMapper.updateByPrimaryKeySelective(loanProcessDO);

        Preconditions.checkArgument(count > 0, "更新本地流程记录失败");
    }

    /**
     * 完成任务   ==>   在activiti中完成
     *
     * @param taskId
     * @param variables
     */
    @Override
    public void completeTask(String taskId, Map<String, Object> variables) {
        // 执行任务
        taskService.complete(taskId, variables);
    }

    /**
     * 更新本地已执行的任务状态
     *
     * @param loanProcessDO_
     * @param taskDefinitionKey
     * @param taskProcessStatus
     * @param approval
     */
    @Override
    public void updateCurrentTaskProcessStatus(LoanProcessDO_ loanProcessDO_, String taskDefinitionKey,
                                               Byte taskProcessStatus, ApprovalParam approval) {

        if (null == taskProcessStatus) {
            return;
        }

        if (taskDefinitionKey.startsWith("filter")) {
            return;
        }

        // 更新资料流转type
        doUpdateDataFlowType(loanProcessDO_, taskDefinitionKey, taskProcessStatus, approval);

        // 执行更新
        doUpdateCurrentTaskProcessStatus(loanProcessDO_, taskDefinitionKey, taskProcessStatus);
    }

    /**
     * 更新资料流转type
     *
     * @param loanProcessDO_
     * @param taskDefinitionKey
     * @param taskProcessStatus
     * @param approval
     */
    private void doUpdateDataFlowType(LoanProcessDO_ loanProcessDO_, String taskDefinitionKey, Byte taskProcessStatus, ApprovalParam approval) {

        // 如果是：[资料流转]节点
        if (taskDefinitionKey.startsWith(DATA_FLOW_TASK_KEY_PREFIX)) {

            // convert
            LoanProcessDO loanProcessDO = (LoanProcessDO) loanProcessDO_;

            String[] taskKeyArr = taskDefinitionKey.split(DATA_FLOW_TASK_KEY_REVIEW_SUFFIX);

            // 是否为：[确认接收]节点     _review
            boolean is_review_task_key = taskDefinitionKey.endsWith(DATA_FLOW_TASK_KEY_REVIEW_SUFFIX) && taskKeyArr.length == 1;

            // send   task-key
            String send_task_key = taskKeyArr[0];
            // review task-key
            String review_task_key = null;

            if (is_review_task_key) {
                review_task_key = taskDefinitionKey;
            } else {
                review_task_key = taskDefinitionKey + DATA_FLOW_TASK_KEY_REVIEW_SUFFIX;
            }

            // taskKey - type  映射
            Map<String, String> codeKMap = dictService.getCodeKMap("loanDataFlowType");

            // send-type
            Byte send_type = Byte.valueOf(codeKMap.get(send_task_key));
            // review-type
            Byte review_type = Byte.valueOf(codeKMap.get(review_task_key));

            Preconditions.checkNotNull(send_type, "资料流转-taskDefinitionKey异常");
            Preconditions.checkNotNull(review_type, "资料流转-taskDefinitionKey异常");


            // send   存在状态：1、2、3
            if (!is_review_task_key) {

                // 1
                if (TASK_PROCESS_DONE.equals(taskProcessStatus)) {

                    // update  type -> next_review_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), send_type, review_type);
                }

                // 2   新建记录
                else if (TASK_PROCESS_TODO.equals(taskProcessStatus)) {

                    // create  type -> send_taskKey--type

                    preRecordDataFlowOrderAndType(loanProcessDO.getOrderId(), send_type, approval);
                }

                // 3
                else if (TASK_PROCESS_REJECT.equals(taskProcessStatus)) {

                    // update   type -> send_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), review_type, send_type);
                }

            }

            // _review    存在状态：0、1、2
            else {

                // 0
                if (TASK_PROCESS_INIT.equals(taskProcessStatus)) {

                    // update   type -> send_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), review_type, send_type);
                }

                // 1
                else if (TASK_PROCESS_DONE.equals(taskProcessStatus)) {

                    // nothing    让下一个节点自己create

                }

                // 2
                else if (TASK_PROCESS_TODO.equals(taskProcessStatus)) {

                    // update  type -> next_review_taskKey--type

                    updateDataFlowType(loanProcessDO.getOrderId(), send_type, review_type);
                }

            }
        }
    }

    /**
     * 资料流转-SEND-TODO待办节点-预处理：插入一条待处理的节点记录 -> orderId、type
     *
     * @param orderId
     * @param sendType
     * @param approval
     */
    private void preRecordDataFlowOrderAndType(Long orderId, Byte sendType, ApprovalParam approval) {

        // 【005-抵押资料合伙人至公司】任务 -[新建]
        if (isDataFlowMortgageP2cNewTask(approval.getOriginalTaskDefinitionKey(), approval.getOriginalAction())) {
            // [提交之前]已经create过了  不能重复创建
            return;
        }

        LoanDataFlowParam loanDataFlowParam = new LoanDataFlowParam();

        loanDataFlowParam.setOrderId(orderId);
        loanDataFlowParam.setType(sendType);

        ResultBean result = loanDataFlowService.create(loanDataFlowParam);
        Preconditions.checkArgument(result.getSuccess(), result.getMsg());
    }

    /**
     * 【资料流转（抵押资料 - 合伙人->公司）】任务 -[新建]
     *
     * @param taskDefinitionKey
     * @param action
     * @return
     */
    private boolean isDataFlowMortgageP2cNewTask(String taskDefinitionKey, Byte action) {
        // 新建【资料流转（抵押资料 - 合伙人->公司）】单据
        boolean isDataFlowMortgageP2cNewTask = DATA_FLOW_MORTGAGE_P2C.getCode().equals(taskDefinitionKey)
                && ACTION_NEW_TASK.equals(action);
        return isDataFlowMortgageP2cNewTask;
    }

    /**
     * 更新资料流转type
     *
     * @param orderId
     * @param current_type
     * @param to_be_update_type
     */
    private void updateDataFlowType(Long orderId, Byte current_type, Byte to_be_update_type) {

        LoanDataFlowDO loanDataFlowDO = loanDataFlowService.getLastByOrderIdAndType(orderId, current_type);

        if (null != loanDataFlowDO) {

            loanDataFlowDO.setType(to_be_update_type);

            LoanDataFlowParam loanDataFlowParam = new LoanDataFlowParam();
            BeanUtils.copyProperties(loanDataFlowDO, loanDataFlowParam);

            ResultBean updateResultBean = loanDataFlowService.update(loanDataFlowParam);
            Preconditions.checkArgument(updateResultBean.getSuccess(), updateResultBean.getMsg());
        }
    }


    /**
     * 订单状态Text
     *
     * @param loanProcessDO_
     * @return
     */
    private String getOrderStatusText(LoanProcessDO_ loanProcessDO_) {

        try {

            // methodName
            String methodName = "getOrderStatus";

            // 获取反射对象
            Class<? extends LoanProcessDO_> loanProcessDOClass = loanProcessDO_.getClass();
            // 获取对应method
            Method method = loanProcessDOClass.getMethod(methodName);
            // 执行method
            Object result = method.invoke(loanProcessDO_);

            String orderStatusText = ORDER_STATUS_CANCEL.equals(result) ?
                    "[已弃单]" : (ORDER_STATUS_END.equals(result) ? "[已结单]" : "[状态异常]");

            return orderStatusText;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * 获取业务单
     *
     * @param orderId
     * @return
     */
    @Override
    public LoanOrderDO getLoanOrder(Long orderId) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "业务单不存在");
        Preconditions.checkNotNull(loanOrderDO.getProcessInstId(), "流程实例ID不存在");

        return loanOrderDO;
    }

    private String getRejectToTask(String taskDefinitionKey) {
        if (BANK_SOCIAL_CREDIT_RECORD_FILTER.getCode().equals(taskDefinitionKey)) {
            return CREDIT_APPLY.getCode();
        }
        return taskDefinitionKey;
    }

    private List<String> getNewTaskKeyList(String processInstanceId, List<String> startTaskIdList) {

        // 获取提交之后的待执行任务列表
        List<Task> endTaskList = getCurrentTaskList(processInstanceId);

        if (CollectionUtils.isEmpty(endTaskList)) {
            return null;
        }

        // 筛选出新产生的任务Key
        List<String> newTaskKeyList = Lists.newArrayList();

        endTaskList.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    if (!startTaskIdList.contains(e.getId())) {
                        // 不存在：新产生的任务
                        newTaskKeyList.add(e.getTaskDefinitionKey());
                    }
                });

        return newTaskKeyList;
    }

    /**
     * 获取 LoanBaseInfoDO
     *
     * @param loanBaseInfoId
     * @return
     */
    private LoanBaseInfoDO getLoanBaseInfoDO(Long loanBaseInfoId) {
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanBaseInfoId);
        Preconditions.checkNotNull(loanBaseInfoDO, "数据异常，贷款基本信息为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getLoanAmount(), "数据异常，贷款金额为空");
        Preconditions.checkNotNull(loanBaseInfoDO.getBank(), "数据异常，贷款银行为空");

        return loanBaseInfoDO;
    }

}
