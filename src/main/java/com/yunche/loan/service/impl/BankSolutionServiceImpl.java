package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.IConstant;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.ICBCFeignClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.param.BankReturnParam;
import com.yunche.loan.domain.vo.UniversalBankInterfaceSerialVO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.BankSolutionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

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
        //记录银行开发流水信息
        BankInterfaceSerialDO serialDO = new BankInterfaceSerialDO();
        //TODO 生成流水号
        serialDO.setSerialNo("12242423423423423423423");
        serialDO.setCustomerId(bankOpenCardParam.getCustomerId());
        serialDO.setTransCode(IDict.K_API.CREDITCARDAPPLY);
        serialDO.setStatus(IDict.K_JJZT.PROCESS);
        int count = bankInterfaceSerialDOMapper.insertSelective(serialDO);
        Preconditions.checkArgument(count>0,"插入银行开卡流水失败");

        //发送银行接口
        ResultBean creditcardapply = icbcFeignClient.creditcardapply(bankOpenCardParam);
        //应答数据
        BankReturnParam returnParam = (BankReturnParam)creditcardapply.getData();
        if(IConstant.SUCCESS.equals(returnParam.getReturnCode()) && IConstant.API_SUCCESS.equals(returnParam.getIcbcApiRetcode())){
            serialDO.setApiStatus(IDict.K_JJZT.REQ_SUCC);
            count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(serialDO);//更新状态
            Preconditions.checkArgument(count>0,"更新银行开卡流水失败");
            return ResultBean.ofSuccess(returnParam);
        }else{
            serialDO.setApiStatus(IDict.K_JJZT.REQ_FAIL);
            count = bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(serialDO);//更新状态
            Preconditions.checkArgument(count>0,"更新银行开卡流水失败");
            throw  new BizException("发送银行开卡流水失败");
        }

    }


}
