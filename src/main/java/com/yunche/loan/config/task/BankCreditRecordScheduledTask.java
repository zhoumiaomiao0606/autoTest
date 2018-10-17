package com.yunche.loan.config.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.constant.LoanCustomerConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.service.LoanCreditInfoHisService;
import com.yunche.loan.service.BankInterfaceSerialService;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanCustomerConst.CREDIT_TYPE_BANK;
import static com.yunche.loan.config.constant.LoanCustomerConst.ENABLE_TYPE_BANK;
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

    @Autowired
    private BankInterfaceSerialService bankInterfaceSerialService;


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

        if (CollectionUtils.isEmpty(bankInterfaceSerialDOS)) {
            return;
        }

        // sortByOrderId
        HashMap<Long, List<BankInterfaceSerialDO>> orderIdDOSMap = sortByOrderId(bankInterfaceSerialDOS);

        // 通用打回param
        ApprovalParam approval = getApprovalParam();

        // 按customer打回
        orderIdDOSMap.forEach((orderId, dos) -> {

            approval.setOrderId(orderId);
            // info拼接
            final String[] info = {null};
            //
            Set<Long> enableCustomerIdSet = Sets.newHashSet();


            dos.stream()
                    .filter(Objects::nonNull)
                    .forEach(bankInterfaceSerialDO -> {

                        // 更新customer 可编辑状态 & 银行征信打回标记
                        updateCustomerEnableAndBankCreditReject(bankInterfaceSerialDO.getCustomerId());

                        // 银行征信拒绝的customer，打回以后，直接将结果设定为“征信拒贷”
                        updateLoanCreditInfo(bankInterfaceSerialDO.getCustomerId());

                        // 更新：auto_reject --> 1-是;
                        updateAutoReject(bankInterfaceSerialDO);

                        // 审核参数设置   当前customer-info
                        setApprovalParam(approval, bankInterfaceSerialDO);

                        // 单个客户银行征信查询历史    -- 打回时间/人/备注
                        loanCreditInfoHisService.saveCreditInfoHis_BankCreditReject_SingleCustomer(bankInterfaceSerialDO.getCustomerId(), approval.getInfo(), approval.isAutoTask());

                        // info拼接
                        if (StringUtils.isNotBlank(approval.getInfo())) {

                            if (StringUtils.isBlank(info[0])) {
                                info[0] = approval.getInfo();
                            } else {
                                info[0] += "   " + approval.getInfo();
                            }
                        }

                        enableCustomerIdSet.add(bankInterfaceSerialDO.getCustomerId());
                    });


            // enable_type
            String ids = enableCustomerIdSet.stream().map(String::valueOf).collect(Collectors.joining(","));
            loanCustomerService.enable(ids, ENABLE_TYPE_BANK);


            // 提交打回
            approval.setInfo(info[0]);
            autoReject(approval);

        });

    }


    /**
     * 根据orderId排序
     *
     * @param bankInterfaceSerialDOS
     * @return
     */
    private HashMap<Long, List<BankInterfaceSerialDO>> sortByOrderId(List<BankInterfaceSerialDO> bankInterfaceSerialDOS) {

        // orderId - DOS
        HashMap<Long, List<BankInterfaceSerialDO>> orderIdDOSMap = Maps.newHashMap();

        bankInterfaceSerialDOS.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    Long orderId = e.getOrderId();

                    if (orderIdDOSMap.containsKey(orderId)) {
                        orderIdDOSMap.get(orderId).add(e);
                    } else {
                        orderIdDOSMap.put(orderId, Lists.newArrayList(e));
                    }

                });

        return orderIdDOSMap;
    }

    /**
     * 通用打回param
     *
     * @return
     */
    public ApprovalParam getApprovalParam() {

        ApprovalParam approval = new ApprovalParam();
        approval.setTaskDefinitionKey(BANK_CREDIT_RECORD.getCode());
        approval.setAction(ACTION_REJECT_MANUAL);

        approval.setCheckPermission(false);
        approval.setNeedLog(true);
        approval.setNeedPush(true);
        approval.setAutoTask(true);

        return approval;
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
            loanCustomerDO.setEnableType(ENABLE_TYPE_BANK);
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
        loanCreditInfoDO.setResult(BaseConst.K_YORN_NO);
        loanCreditInfoDO.setType(CREDIT_TYPE_BANK);

        loanCreditInfoService.save(loanCreditInfoDO);
    }

    /**
     * 审核参数设置   当前customer-info
     *
     * @param approval
     * @param bankInterfaceSerialDO
     */
    private ApprovalParam setApprovalParam(ApprovalParam approval, BankInterfaceSerialDO bankInterfaceSerialDO) {

        // 清空旧info
        approval.setInfo(null);

        String rejectReason = bankInterfaceSerialDO.getRejectReason();

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

        return approval;
    }

    /**
     * 更新：auto_reject --> 1-是;
     *
     * @param bankInterfaceSerialDO
     */
    private void updateAutoReject(BankInterfaceSerialDO bankInterfaceSerialDO) {

        bankInterfaceSerialDO.setAutoReject(BaseConst.K_YORN_YES);

        bankInterfaceSerialService.update(bankInterfaceSerialDO);
    }

    /**
     * 提交打回
     *
     * @param approval
     */
    private void autoReject(ApprovalParam approval) {

        Long orderId = approval.getOrderId();

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
    }
}
