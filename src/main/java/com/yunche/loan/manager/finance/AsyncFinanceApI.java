package com.yunche.loan.manager.finance;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.Subscribe;
import com.yunche.loan.config.util.EventBusCenter;
import com.yunche.loan.config.util.HttpUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.PostFinanceData;
import com.yunche.loan.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;

@Component
public class AsyncFinanceApI
{
    private static final Logger LOG = LoggerFactory.getLogger(AsyncFinanceApI.class);

    private static final String HOST = "http://";

    private static final String PATH = "/aaa/aaa";

    private static final String METHOD = "post";

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private RemitDetailsDOMapper remitDetailsDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanRefundApplyDOMapper loanRefundApplyDOMapper;

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;


    @Async
    public void postFinanceData(ApprovalParam approvalParam)
    {

        PostFinanceData postFinanceData =new PostFinanceData();

        //根据orderid查询合伙人id
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getByOrderId(approvalParam.getOrderId());
        Preconditions.checkNotNull(loanBaseInfoDO.getPartnerId(), "合伙人id不能为空");
        postFinanceData.setPartner(loanBaseInfoDO.getPartnerId());



        //判断是-垫款提交-退款提交-偿款提交----查询相关数据
        if (approvalParam.equals(REMIT_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))
        {
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(approvalParam.getOrderId());

            // 关联ID
            Long remitDetailsId = loanOrderDO.getRemitDetailsId();

            Preconditions.checkNotNull(remitDetailsId, "打款详单为空");

            RemitDetailsDO remitDetailsDO = remitDetailsDOMapper.selectByPrimaryKey(remitDetailsId);

            postFinanceData.setClientAdvance(remitDetailsDO.getRemit_amount());

        }

        if (approvalParam.equals(REFUND_APPLY.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))
        {
            LoanRefundApplyDO loanRefundApplyDO = loanRefundApplyDOMapper.lastByOrderId(approvalParam.getOrderId());
            Preconditions.checkNotNull(loanRefundApplyDO, "退款单为空");
            postFinanceData.setClientAdvance(loanRefundApplyDO.getRefund_amount());

        }

        if (approvalParam.equals(FINANCE_INSTEAD_PAY_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction()))
        {
            List<LoanApplyCompensationDO> loanApplyCompensationDOS = loanApplyCompensationDOMapper.selectByOrderId(approvalParam.getOrderId());
            BigDecimal totalCompensationAcount = new BigDecimal(0);

            for(LoanApplyCompensationDO loanApplyCompensationDO:loanApplyCompensationDOS)
            {
                if (loanApplyCompensationDO !=null)
                {
                    totalCompensationAcount = totalCompensationAcount.add(loanApplyCompensationDO.getCompensationAmount());
                }
            }

            postFinanceData.setClientAdvance(totalCompensationAcount);

        }

        //进行推送
        try {
            LOG.error("准备异步发送数据！！！"+postFinanceData.toString());
            HttpUtils.loginAuth();
            /*HttpUtils.doPost(HOST,PATH,METHOD,null,null,postFinanceData.toString());*/
        } catch (Exception e) {
            LOG.error("财务数据异步发送失败！！！",e);
        }


    }


    //一旦-垫款提交-退款提交-偿款提交-则执行
    @Subscribe
    public void listernApproval(ApprovalParam approvalParam)
    {
        if((approvalParam.getTaskDefinitionKey().equals(REMIT_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) || (approvalParam.getTaskDefinitionKey().equals(FINANCE_INSTEAD_PAY_REVIEW.getCode()) && ACTION_PASS.equals(approvalParam.getAction())) || (approvalParam.getTaskDefinitionKey().equals(REFUND_APPLY.getCode()) && ACTION_PASS.equals(approvalParam.getAction())))
        {
            postFinanceData(approvalParam);
        }

    }




    // 由spring 在初始化bean后执行
    @PostConstruct
    public void init(){
        register2EventBus();
    }

    // 将自己注册到eventBus中
    protected void register2EventBus(){
        EventBusCenter.eventBus.register(this);
    }
}
