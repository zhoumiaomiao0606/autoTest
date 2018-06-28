package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.UniversalBankInterfaceSerialVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.BankSolutionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

import static com.yunche.loan.config.constant.BankInterfaceSerialStatusEnum.PROCESS;
import static com.yunche.loan.config.constant.BankInterfaceSerialStatusEnum.SUCCESS;

@Service
@Transactional
public class BankSolutionServiceImpl implements BankSolutionService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private ICBCFeignClient icbcFeignClient;

    @Override
    public void creditAutomaticCommit(@Validated @NotNull Long bankId, @Validated @NotNull List<LoanCustomerDO> customers) {
        checkCustomerHavingCreditON14Day(customers);
        int value = bankId.intValue();
        switch (value) {
            case 4:
                //判断当前客户贷款银行是否为台州工行，如为台州工行：
                tzICBCBankCreditProcess(customers);
                break;
            default:
                return;
        }
    }

    @Override
    public void creditArtificialCompensation(@Validated @NotNull Long bankId,@Validated @NotNull Long customerId) {

    }


    private void tzICBCBankCreditProcess(List<LoanCustomerDO> customers){
            //①判断客户是否已提交了征信记录，且银行征信结果非退回，若满足，则不会推送该客户，否则继续②
            for(LoanCustomerDO loanCustomerDO:customers){
                UniversalBankInterfaceSerialVO result = loanQueryDOMapper.selectUniversalLatestBankInterfaceSerial(loanCustomerDO.getId());
                if(result!=null){
                    //之前提交过
                    //非处理中 并且 非查询成功的可以进行推送
                    if(!result.getStatus().equals(SUCCESS.getStatus().toString()) && !result.getStatus().equals(PROCESS.getStatus().toString())){

                    }
                }


            }


    }

    private void checkCustomerHavingCreditON14Day(List<LoanCustomerDO> customers){
        for(LoanCustomerDO loanCustomerDO:customers){
            Preconditions.checkArgument(StringUtils.isNotBlank(loanCustomerDO.getIdCard()), loanCustomerDO.getName()+"身份证号不能为空");
            if(loanQueryDOMapper.checkCustomerHavingCreditON14Day(loanCustomerDO.getIdCard())){
                throw new BizException(loanCustomerDO.getName()+"在14天内重复查询征信");
            }
        }
    }

    /**
     * 银行开卡
     * @param bankOpenCardParam
     */
    public ResultBean creditcardapply(BankOpenCardParam bankOpenCardParam){

        ResultBean creditcardapply = icbcFeignClient.creditcardapply(bankOpenCardParam);
        return ResultBean.ofSuccess(creditcardapply);
    }


}
