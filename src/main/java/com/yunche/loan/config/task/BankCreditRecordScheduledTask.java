package com.yunche.loan.config.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.service.LoanCreditInfoHisService;
import com.yunche.loan.service.LoanCreditInfoService;
import com.yunche.loan.service.LoanCustomerService;
import com.yunche.loan.service.LoanProcessService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.yunche.loan.config.constant.LoanCustomerConst.CREDIT_TYPE_BANK;
import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_CREDIT_RECORD;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_REJECT_MANUAL;

/**
 * [银行征信]推送失败 - 自动打回 定时任务
 *
 * @author liuzhe
 * @date 2018/9/4
 */
@Component
public class BankCreditRecordScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(BankCreditRecordScheduledTask.class);


    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanCreditInfoService loanCreditInfoService;

    @Autowired
    private LoanCreditInfoHisService loanCreditInfoHisService;

    @Autowired
    private LoanProcessService loanProcessService;

    @Autowired
    private LoanCustomerService loanCustomerService;


    /**
     * 自动打回任务：  [银行征信查询] -> [征信申请]
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @DistributedLock(60)
    public void doAutoRejectTask() {

        // 扫描：[银行征信] - 推送失败的 所有订单ID
        List<BankInterfaceSerialDO> bankInterfaceSerialDOS = listOfBankCreditRecordPushFailed();

        // 自动打回
        doAutoReject(bankInterfaceSerialDOS);
    }

    /**
     * [银行征信] - 推送失败的   所有订单-推送失败详情DO
     *
     * @return
     */
    private List<BankInterfaceSerialDO> listOfBankCreditRecordPushFailed() {

        List<BankInterfaceSerialDO> bankInterfaceSerialDOS = bankInterfaceSerialDOMapper.listOfBankCreditRecordPushFailed();

        return bankInterfaceSerialDOS;
    }

    /**
     * 自动打回： [银行征信查询] -> [征信申请]
     *
     * @param bankInterfaceSerialDOS
     */
    private void doAutoReject(List<BankInterfaceSerialDO> bankInterfaceSerialDOS) {

        if (!CollectionUtils.isEmpty(bankInterfaceSerialDOS)) {

            ApprovalParam approval = new ApprovalParam();
            approval.setTaskDefinitionKey(BANK_CREDIT_RECORD.getCode());
            approval.setAction(ACTION_REJECT_MANUAL);

            approval.setCheckPermission(false);
            approval.setNeedLog(true);
            approval.setNeedPush(true);
            approval.setAutoTask(true);

            bankInterfaceSerialDOS.stream()
                    .forEach(bankInterfaceSerialDO -> {

                        // 更新 可编辑状态 & 银行征信打回标记
                        updateCustomerEnableAndBankCreditReject(bankInterfaceSerialDO.getCustomerId());

                        // 银行征信拒绝的客户，打回以后，直接将结果设定为“征信拒贷”
                        updateLoanCreditInfo(bankInterfaceSerialDO.getCustomerId());

                        // 审核参数设置
                        setApprovalParam(approval, bankInterfaceSerialDO);

                        // 记录单个客户征信查询历史记录--银行征信打回
                        loanCreditInfoHisService.saveCreditInfoHis_BankCreditReject(bankInterfaceSerialDO.getCustomerId(), approval.getInfo());

                        // 提交打回
                        autoReject(approval, bankInterfaceSerialDO);
                    });
        }

    }


    /**
     * 更新 可编辑状态 & 银行征信打回标记
     *
     * @param customerId
     */
    private void updateCustomerEnableAndBankCreditReject(Long customerId) {

        if (null != customerId) {

            LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
            loanCustomerDO.setId(customerId);
            loanCustomerDO.setEnable(BaseConst.K_YORN_YES);
            loanCustomerDO.setBankCreditReject(BaseConst.K_YORN_YES);

            ResultBean<Void> updateResult = loanCustomerService.update(loanCustomerDO);
            Preconditions.checkArgument(updateResult.getSuccess(), updateResult.getMsg());
        }
    }

    /**
     * 银行征信拒绝的客户，打回以后，直接将结果设定为“征信拒贷”
     *
     * @param customerId
     */
    private void updateLoanCreditInfo(Long customerId) {

        LoanCreditInfoDO loanCreditInfoDO = new LoanCreditInfoDO();
        loanCreditInfoDO.setCustomerId(customerId);
        // 0-不通过
        loanCreditInfoDO.setResult(new Byte("0"));
        loanCreditInfoDO.setType(CREDIT_TYPE_BANK);

        loanCreditInfoService.save(loanCreditInfoDO);
    }

    /**
     * 审核参数设置
     *
     * @param approval
     * @param bankInterfaceSerialDO
     */
    private void setApprovalParam(ApprovalParam approval, BankInterfaceSerialDO bankInterfaceSerialDO) {

        Long orderId = bankInterfaceSerialDO.getOrderId();
        String rejectReason = bankInterfaceSerialDO.getRejectReason();

        approval.setOrderId(orderId);

        LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(bankInterfaceSerialDO.getCustomerId(), null);

        // {"ICBC_API_RETMSG":"success","ICBC_API_TIMESTAMP":"2018-08-27 08:23:52","pub":{"retcode":"22094","retmsg":"该客户为灰名单客户"},"ICBC_API_RETCODE":0}
        String apiMsg = bankInterfaceSerialDO.getApiMsg();

        if (StringUtils.isNotBlank(rejectReason)) {

            approval.setInfo(customerDO.getName() + ":" + rejectReason);

        } else if (StringUtils.isNotBlank(apiMsg)) {

            JSONObject jsonObject = JSON.parseObject(apiMsg);
            JSONObject pub = jsonObject.getJSONObject("pub");

            if (!CollectionUtils.isEmpty(pub)) {
                String retmsg = pub.getString("retmsg");
                approval.setInfo(customerDO.getName() + ":" + retmsg);
            }
        }
    }

    /**
     * 提交打回
     *
     * @param approval
     * @param bankInterfaceSerialDO
     */
    private void autoReject(ApprovalParam approval, BankInterfaceSerialDO bankInterfaceSerialDO) {

        Long orderId = approval.getOrderId();

        try {

            ResultBean<Void> approvalResult = loanProcessService.approval(approval);

            if (approvalResult.getSuccess()) {

                logger.info("自动打回成功  >>>  orderId : {} , info : {} ", orderId, approval.getInfo());

                // 更新：auto_reject --> 1-是;
                bankInterfaceSerialDO.setAutoReject(BaseConst.K_YORN_YES);
                int count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(bankInterfaceSerialDO);
                Preconditions.checkArgument(count > 0, "更新auto_reject失败");

            } else {

                logger.error("自动打回失败  >>>  orderId : {} , errMsg : {} ", orderId, approvalResult.getMsg());
            }

        } catch (Exception e) {

            logger.error("自动打回失败  >>>  orderId : {} , errMsg : {} ", orderId, e.getMessage(), e);
        }
    }

}
