package com.yunche.loan.config.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.service.LoanProcessService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.yunche.loan.config.constant.LoanProcessEnum.BANK_CREDIT_RECORD;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_REJECT_MANUAL;

/**
 * @author liuzhe
 * @date 2018/9/4
 */
@Component
public class BankCreditRecordScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(BankCreditRecordScheduledTask.class);


    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Autowired
    private LoanProcessService loanProcessService;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;


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

            bankInterfaceSerialDOS.parallelStream()
                    .forEach(bankInterfaceSerialDO -> {

                        Long orderId = bankInterfaceSerialDO.getOrderId();

                        approval.setOrderId(orderId);

                        Byte status = bankInterfaceSerialDO.getStatus();

                        LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(bankInterfaceSerialDO.getCustomerId(), BaseConst.VALID_STATUS);
                        // {"ICBC_API_RETMSG":"success","ICBC_API_TIMESTAMP":"2018-08-27 08:23:52","pub":{"retcode":"22094","retmsg":"该客户为灰名单客户"},"ICBC_API_RETCODE":0}
                        String apiMsg = bankInterfaceSerialDO.getApiMsg();

                        if(StringUtils.isNotBlank(bankInterfaceSerialDO.getRejectReason())){
                            approval.setInfo(customerDO.getName()+":"+bankInterfaceSerialDO.getRejectReason());
                        }else if (StringUtils.isNotBlank(apiMsg)) {
                            JSONObject jsonObject = JSON.parseObject(apiMsg);
                            JSONObject pub = jsonObject.getJSONObject("pub");

                            if (!CollectionUtils.isEmpty(pub)) {
                                String retmsg = pub.getString("retmsg");
                                approval.setInfo(customerDO.getName()+":"+retmsg);
                            }
                        }


                        try {

                            ResultBean<Void> approvalResult = loanProcessService.approval(approval);

                            if (approvalResult.getSuccess()) {

                                logger.info("自动打回成功  >>>  orderId : {} , info : {} ", orderId, approval.getInfo());

                            } else {

                                logger.error("自动打回失败  >>>  orderId : {} , errMsg : {} ", orderId, approvalResult.getMsg());
                            }

                        } catch (Exception e) {

                            logger.error("自动打回失败  >>>  orderId : {} , errMsg : {} ", orderId, e.getMessage(), e);
                        }

                    });
        }

    }

}
