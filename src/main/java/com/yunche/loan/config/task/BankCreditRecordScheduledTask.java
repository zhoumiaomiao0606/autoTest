package com.yunche.loan.config.task;

import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.service.LoanProcessService;
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


    /**
     * 自动打回任务：  [银行征信查询] -> [征信申请]
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    @DistributedLock(60)
    public void doAutoRejectTask() {

        // 扫描：[银行征信] - 推送失败的 所有订单ID
        List<Long> orderIdList = listOrderIdOfBankCreditRecordPushFailed();

        // 自动打回
        doAutoReject(orderIdList);
    }

    /**
     * [银行征信] - 推送失败的   所有订单ID
     *
     * @return
     */
    private List<Long> listOrderIdOfBankCreditRecordPushFailed() {

        List<Long> orderIdList = bankInterfaceSerialDOMapper.listOrderIdOfBankCreditRecordPushFailed();

        return orderIdList;
    }

    /**
     * 自动打回： [银行征信查询] -> [征信申请]
     *
     * @param orderIdList
     */
    private void doAutoReject(List<Long> orderIdList) {

        if (!CollectionUtils.isEmpty(orderIdList)) {

            ApprovalParam approval = new ApprovalParam();
            approval.setTaskDefinitionKey(BANK_CREDIT_RECORD.getCode());
            approval.setAction(ACTION_REJECT_MANUAL);

            approval.setCheckPermission(false);
            approval.setNeedLog(true);
            approval.setNeedPush(true);

            orderIdList.parallelStream()
                    .forEach(orderId -> {

                        approval.setOrderId(orderId);

                        try {

                            ResultBean<Void> approvalResult = loanProcessService.approval(approval);

                            if (approvalResult.getSuccess()) {

                                logger.info("自动打回成功  >>>  orderId : {}", orderId);

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
