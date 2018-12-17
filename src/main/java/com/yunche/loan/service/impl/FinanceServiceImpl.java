package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunche.loan.config.common.FinanceConfig;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.PaymentParam;
import com.yunche.loan.domain.param.RemitDetailsParam;
import com.yunche.loan.domain.param.RemitSatusParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.FinanceService;
import com.yunche.loan.service.LoanProcessLogService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunche.loan.config.constant.BaseConst.*;
import static com.yunche.loan.config.constant.LoanProcessEnum.*;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;

@Service
public class FinanceServiceImpl implements FinanceService
{
    private static final Logger LOG = LoggerFactory.getLogger(FinanceServiceImpl.class);
    @Resource
    private BusinessReviewManager businessReviewManager;

    @Autowired
    private FinanceConfig financeConfig;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private RemitDetailsDOMapper remitDetailsDOMapper;

    private LoanProcessService loanProcessService;


    @Override
    public RecombinationVO detail(Long orderId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        List<UniversalCreditInfoVO> credits = loanQueryDOMapper.selectUniversalCreditInfo(orderId);
        for (UniversalCreditInfoVO universalCreditInfoVO : credits) {
            if (!StringUtils.isBlank(universalCreditInfoVO.getCustomer_id())) {
                universalCreditInfoVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderIdByCustomerId(orderId, Long.valueOf(universalCreditInfoVO.getCustomer_id())));
            }
        }

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null)
        {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel())))
            {
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null)
            {
                if (baseAreaDO.getParentAreaName() != null)
                {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else
                    {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        universalInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setRemit(loanQueryDOMapper.selectUniversalRemitDetails(orderId));
        recombinationVO.setCost(loanQueryDOMapper.selectUniversalCostDetails(orderId));
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo(LOAN_REVIEW.getCode(), orderId));
        recombinationVO.setChannel_msg(loanQueryDOMapper.selectUniversalApprovalInfo(BUSINESS_REVIEW.getCode(), orderId));
        recombinationVO.setTelephone_msg(loanQueryDOMapper.selectUniversalApprovalInfo(TELEPHONE_VERIFY.getCode(), orderId));
        recombinationVO.setLoan(loanQueryDOMapper.selectUniversalLoanInfo(orderId));
        recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(orderId));
        recombinationVO.setCredits(credits);
        recombinationVO.setCustomers(customers);
        return recombinationVO;
    }

    @Override
    public ResultBean update(RemitDetailsParam remitDetailsParam)
    {
        Preconditions.checkNotNull(remitDetailsParam.getOrderId(),"订单id不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(remitDetailsParam.getOrderId());

        Preconditions.checkNotNull(loanOrderDO,"该单号订单不存在");

        RemitDetailsDO remitDetailsDO = remitDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getRemitDetailsId());

        remitDetailsDO.setRemit_account(remitDetailsParam.getRemit_account());
        remitDetailsDO.setRemit_bank(remitDetailsParam.getRemit_bank());
        remitDetailsDO.setRemit_account_number(remitDetailsParam.getRemit_account_number());

        int i = remitDetailsDOMapper.updateByPrimaryKeySelective(remitDetailsDO);

        if (i == 1)
        {
            return ResultBean.ofSuccess("成功");
        }else
            {
                return ResultBean.ofError("错误");
            }

    }

    @Override
    public ResultBean payment(Long orderId)
    {
        Preconditions.checkNotNull(orderId,"订单id不能为空");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);

        Preconditions.checkNotNull(loanOrderDO,"该单号订单不存在");

        RemitDetailsDO remitDetailsDO = remitDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getRemitDetailsId());

        //先校验是否已经打款
        //UniversalRemitDetails universalRemitDetails = loanQueryDOMapper.selectUniversalRemitDetails(orderId);
        if (remitDetailsDO==null)
        {
            throw  new BizException("打款信息有误！！！");
        }
        if (remitDetailsDO.getRemit_status().equals(REMIT_STATUS_ONE))
        {
            throw  new BizException("该订单已处于打款中！！！");
        }
        if (remitDetailsDO.getRemit_status().equals(REMIT_STATUS_TWO))
        {
            throw  new BizException("该订单已打款成功！！！");
        }
        if (remitDetailsDO.getRemit_status().equals(REMIT_STATUS_THREE))
        {
            throw  new BizException("该订单已打款失败！！！");
        }


        PaymentParam paymentParam = new PaymentParam();

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);

        paymentParam.setOrder_id(orderId);
        paymentParam.setAmount(remitDetailsDO.getRemit_amount());
        paymentParam.setAccount_name(remitDetailsDO.getBeneficiary_bank());
        paymentParam.setAccount_number(remitDetailsDO.getBeneficiary_account_number());

        Map<String,String> map = new HashMap<>();
        map.put("debit_name",universalInfoVO.getCustomer_name());
        map.put("debit_cert_no",universalInfoVO.getCustomer_id_card());
        map.put("debit_mobile_no",universalInfoVO.getCustomer_mobile());

        List<Map<String,String>> list = new ArrayList<>();
        list.add(map);

        paymentParam.setDebitInfo(list);

        //paymentParam.setDebit_name(universalInfoVO.getCustomer_name());
        //paymentParam.setDebit_cert_no(universalInfoVO.getCustomer_id_card());
        //paymentParam.setDebit_mobile_no(universalInfoVO.getCustomer_mobile());

        //设置回调接口
        paymentParam.setCall_back_url(financeConfig.getCallBackUrl());

        LOG.info("支付参数："+paymentParam.toString());

        String financeResult = businessReviewManager.financeUnisal3(paymentParam,"/payment");

        CommonFinanceResult Result = new CommonFinanceResult();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Type type =new TypeToken<CommonFinanceResult>(){}  .getType();
            Gson gson = new Gson();
            Result = gson.fromJson(financeResult, type);
        }

        if (!Result.getResultCode().trim().equals("200"))
        {
            return ResultBean.ofError("打款失败:"+Result.getMessage());
        }

        //更新打款单打款状态---待讨论


        remitDetailsDO.setRemit_status(REMIT_STATUS_ONE);
        int i = remitDetailsDOMapper.updateByPrimaryKeySelective(remitDetailsDO);

        if (i == 1)
        {
            return ResultBean.ofSuccess("打款中！");
        }else
        {
            return ResultBean.ofError("打款信息错误！");
        }


    }

    @Override
    public ResultBean remitInfo(RemitSatusParam remitSatusParam)
    {
        Preconditions.checkNotNull(remitSatusParam.getOrderId(),"订单id不能为空");
        Preconditions.checkNotNull(remitSatusParam.getRemitSatus(),"打款状态不能为空");


        if (remitSatusParam.getRemitSatus().equals(REMIT_STATUS_TWO))
        {
            //提交订单
            ApprovalParam approvalParam = new ApprovalParam();

            approvalParam.setOrderId(remitSatusParam.getOrderId());
            approvalParam.setTaskDefinitionKey(REMIT_REVIEW.getCode());
            approvalParam.setAction(ACTION_PASS);

            ResultBean<Void> approval = loanProcessService.approval(approvalParam);
            Preconditions.checkArgument(approval.getSuccess(), approval.getMsg());
        }

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(remitSatusParam.getOrderId());

        Preconditions.checkNotNull(loanOrderDO,"该单号订单不存在");

        RemitDetailsDO remitDetailsDO = remitDetailsDOMapper.selectByPrimaryKey(loanOrderDO.getRemitDetailsId());

        remitDetailsDO.setRemit_status(remitSatusParam.getRemitSatus());

        int i = remitDetailsDOMapper.updateByPrimaryKeySelective(remitDetailsDO);

        if (i == 1)
        {
            return ResultBean.ofSuccess("更新打款状态成功！");
        }else
        {
            return ResultBean.ofError("更新打款状态失败！");
        }
    }
}
