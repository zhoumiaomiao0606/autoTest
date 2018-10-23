package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.FinancialRebateDetailDO;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.CustomersLoanFinanceInfoByPartnerMapper;
import com.yunche.loan.mapper.FinancialRebateDetailDOMapper;
import com.yunche.loan.mapper.LoanApplyCompensationDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.CustomersLoanFinanceInfoByPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CustomersLoanFinanceInfoByPartnerServiceImpl implements CustomersLoanFinanceInfoByPartnerService {
    @Autowired
    private CustomersLoanFinanceInfoByPartnerMapper customersLoanFinanceInfoByPartnerMapper;

    @Autowired
    private LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private FinancialRebateDetailDOMapper financialRebateDetailDOMapper;

    public static enum CustomersLoanFinance {
        BADBALANCE(1, "不良余额"),
        OVERDUEBALANCE(2, "逾期余额"),
        INGUARANTEEBALANCE(3, "在保余额"),
        LOANBALANCE(4, "贷款余额"),
        COMPENSATION(5, "代偿");

        private int code;

        private String message;

        CustomersLoanFinance(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    @Override
    public ResultBean selectCustomersLoanFinanceInfoByPartner(CustomersLoanFinanceInfoByPartnerParam customersLoanFinanceInfoByPartnerParam) {
        Preconditions.checkNotNull(customersLoanFinanceInfoByPartnerParam.getPartnerId(), "合伙人id不能为空");
        Preconditions.checkNotNull(customersLoanFinanceInfoByPartnerParam.getCode(), "查询类型不能为空");

        if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.BADBALANCE.getCode()) {
            //分页
            PageHelper.startPage(customersLoanFinanceInfoByPartnerParam.getPageIndex(), customersLoanFinanceInfoByPartnerParam.getPageSize(), true);
            List<BadBalanceByPartnerVO> badBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectBadBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            // 取分页信息
            PageInfo<BadBalanceByPartnerVO> pageInfo = new PageInfo<>(badBalanceByPartnerVOS);
            return ResultBean.ofSuccess(pageInfo);

        } else if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.OVERDUEBALANCE.getCode()) {
            //分页
            PageHelper.startPage(customersLoanFinanceInfoByPartnerParam.getPageIndex(), customersLoanFinanceInfoByPartnerParam.getPageSize(), true);
            List<OverdueBalanceByPartnerVO> overdueBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectOverdueBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            // 取分页信息
            PageInfo<OverdueBalanceByPartnerVO> pageInfo = new PageInfo<>(overdueBalanceByPartnerVOS);
            return ResultBean.ofSuccess(pageInfo);

        } else if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.INGUARANTEEBALANCE.getCode()) {
            //分页
            PageHelper.startPage(customersLoanFinanceInfoByPartnerParam.getPageIndex(), customersLoanFinanceInfoByPartnerParam.getPageSize(), true);
            List<InGuaranteeBalanceByPartnerVO> inGuaranteeBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectInGuaranteeBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            // 取分页信息
            PageInfo<InGuaranteeBalanceByPartnerVO> pageInfo = new PageInfo<>(inGuaranteeBalanceByPartnerVOS);
            return ResultBean.ofSuccess(pageInfo);

        } else if (customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.LOANBALANCE.getCode()) {
            //分页
            PageHelper.startPage(customersLoanFinanceInfoByPartnerParam.getPageIndex(), customersLoanFinanceInfoByPartnerParam.getPageSize(), true);
            List<LoanBalanceByPartnerVO> loanBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectLoanBalance(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            // 取分页信息
            PageInfo<LoanBalanceByPartnerVO> pageInfo = new PageInfo<>(loanBalanceByPartnerVOS);
            return ResultBean.ofSuccess(pageInfo);

        } else if(customersLoanFinanceInfoByPartnerParam.getCode() == CustomersLoanFinance.COMPENSATION.getCode()){
            FSysCompensationVO fSysCompensationVO = customersLoanFinanceInfoByPartnerMapper.selectCompensationInfoByPartner(customersLoanFinanceInfoByPartnerParam.getPartnerId());
            return ResultBean.ofSuccess(fSysCompensationVO);
        }else{
            return ResultBean.ofError("参数有错误");
        }

    }

    @Override
    public ResultBean selectTotalLoanFinanceInfoByPartner(Long partnerId) {
        Preconditions.checkNotNull(partnerId, "合伙人id不能为空");

        List<BadBalanceByPartnerVO> badBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectBadBalance(partnerId);

        List<OverdueBalanceByPartnerVO> overdueBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectOverdueBalance(partnerId);

        List<InGuaranteeBalanceByPartnerVO> inGuaranteeBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectInGuaranteeBalance(partnerId);

        List<LoanBalanceByPartnerVO> loanBalanceByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectLoanBalance(partnerId);

        TotalLoanFinanceInfoByPartnerVO totalLoanFinanceInfoByPartnerVO = new TotalLoanFinanceInfoByPartnerVO();

        //统计不良总额
        if (badBalanceByPartnerVOS != null && badBalanceByPartnerVOS.size() > 0) {
            Optional<BigDecimal> totalBadBalance = badBalanceByPartnerVOS.stream()
                    .filter(badBalanceByPartnerVO -> badBalanceByPartnerVO.getBadBalance() != null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getBadBalance())
                    .reduce((x, y) -> x.add(y));
            if (totalBadBalance.isPresent()) {
                totalLoanFinanceInfoByPartnerVO.setTotalBadBalance(totalBadBalance.get());
            }
        }

        //统计逾期总额
        if (overdueBalanceByPartnerVOS != null && overdueBalanceByPartnerVOS.size() > 0) {
            Optional<BigDecimal> totalOverdueBalance = overdueBalanceByPartnerVOS.stream()
                    .filter(overdueBalanceByPartnerVO -> overdueBalanceByPartnerVO.getOverdueBalance() != null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getOverdueBalance())
                    .reduce((x, y) -> x.add(y));
            if (totalOverdueBalance.isPresent()) {
                totalLoanFinanceInfoByPartnerVO.setTotalOverdueBalance(totalOverdueBalance.get());
            }
        }

        //统计逾期总额
        if (inGuaranteeBalanceByPartnerVOS != null && inGuaranteeBalanceByPartnerVOS.size() > 0) {
            Optional<BigDecimal> totalInGuaranteeBalance = inGuaranteeBalanceByPartnerVOS.stream()
                    .filter(inGuaranteeBalanceByPartnerVO -> inGuaranteeBalanceByPartnerVO.getInGuaranteeBalance() != null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getInGuaranteeBalance())
                    .reduce((x, y) -> x.add(y));
            if (totalInGuaranteeBalance.isPresent()) {
                totalLoanFinanceInfoByPartnerVO.setTotalInGuaranteeBalance(totalInGuaranteeBalance.get());
            }
        }

        //统计贷款总额
        if (loanBalanceByPartnerVOS != null && loanBalanceByPartnerVOS.size() > 0) {
            Optional<BigDecimal> totalLoanBalance = loanBalanceByPartnerVOS.stream()
                    .filter(loanBalanceByPartnerVO -> loanBalanceByPartnerVO.getFinancialBankPeriodPrincipal() != null)
                    .map(badBalanceByPartnerVO -> badBalanceByPartnerVO.getFinancialBankPeriodPrincipal())
                    .reduce((x, y) -> x.add(y));
            if (totalLoanBalance.isPresent()) {
                totalLoanFinanceInfoByPartnerVO.setTotalLoanBalance(totalLoanBalance.get());
            }
        }
        //统计
        return ResultBean.ofSuccess(totalLoanFinanceInfoByPartnerVO);
    }

    @Override
    public ResultBean getOrderByCustomerId(Long customerId) {
        List<OrderByCustomerIdVO> list = customersLoanFinanceInfoByPartnerMapper.getOrderByCustomerId(customerId);
        //根据订单查询代偿
        if (list != null && list.size() > 0) {
            list.stream()
                    .forEach(orderByCustomerIdVO ->
                    {
                        if (orderByCustomerIdVO.getNum() != null) {
                            List<LoanApplyCompensationDO> loanApplyCompensationDOS = loanApplyCompensationDOMapper.selectByOrderId(orderByCustomerIdVO.getNum());
                            for (LoanApplyCompensationDO loanApplyCompensationDO : loanApplyCompensationDOS) {
                                PartnerCompensations partnerCompensations = new PartnerCompensations();

                                partnerCompensations.setCompensatoryAmount(loanApplyCompensationDO.getPartnerCompensationAmount());
                                partnerCompensations.setCompensatoryTime(loanApplyCompensationDO.getPartnerDcReviewDate());
                                partnerCompensations.setOverdueAmount(loanApplyCompensationDO.getCurrArrears());
                                partnerCompensations.setOverdueDate(loanApplyCompensationDO.getGmtCreate());

                                orderByCustomerIdVO.getPartnerCompensationsList().add(partnerCompensations);
                            }

                            //逾期代偿---repayment_record
                           /* List<LoanApplyCompensationDO> loanApplyCompensationDOS = loanApplyCompensationDOMapper.selectByOrderId(orderByCustomerIdVO.getNum());
                            orderByCustomerIdVO.setLoanApplyCompensationDOS(loanApplyCompensationDOS);*/

                        }
                    });
        }
        return ResultBean.ofSuccess(list);
    }

    @Override
    public ResultBean getCustomerInfoByCustomerName(CustomerInfoByCustomerNameParam customerInfoByCustomerNameParam) {
        List<CustomerInfoForFinanceSys> customerInfoForFinanceSys = customersLoanFinanceInfoByPartnerMapper.getCustomerInfoByCustomerName(customerInfoByCustomerNameParam);
        return ResultBean.ofSuccess(customerInfoForFinanceSys);
    }

    @Override
    public ResultBean selectRefundOrderInfoByPartner(RefundOrderInfoByPartnerParam refundOrderInfoByPartnerParam) {
        Preconditions.checkNotNull(refundOrderInfoByPartnerParam.getPartnerId(), "合伙人id不能为空");
        //分页
        PageHelper.startPage(refundOrderInfoByPartnerParam.getPageIndex(), refundOrderInfoByPartnerParam.getPageSize(), true);
        List<RefundOrderInfoByPartnerVO> refundOrderInfoByPartnerVOS = customersLoanFinanceInfoByPartnerMapper.selectRefundOrderInfoByPartner(refundOrderInfoByPartnerParam.getPartnerId());
        // 取分页信息
        PageInfo<RefundOrderInfoByPartnerVO> pageInfo = new PageInfo<>(refundOrderInfoByPartnerVOS);
        return ResultBean.ofSuccess(pageInfo);
    }

    /**
     *  代偿明细
     * @param param
     * @return
     */
    @Override
    public ResultBean compensationDetail(CustomersLoanFinanceInfoByPartnerParam param) {
        Preconditions.checkNotNull(param.getPartnerId(),"参数有误：合伙人ID不能为空");
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List<FSysCompensationVO> compensationDetails = customersLoanFinanceInfoByPartnerMapper.listCompensationInfoByPartner(param.getPartnerId());
        PageInfo<FSysCompensationVO> pageInfo = new PageInfo<>(compensationDetails);
        return ResultBean.ofSuccess(pageInfo);

    }

    /**
     * 返利明细列表
     * @param param
     * @return
     */
    @Override
    public ResultBean rebateDetailsList(FSysRebateParam param) {
        Preconditions.checkNotNull(param.getType(),"参数有误：是否入账参数不能为空 ");
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List<FSysRebateVO> fSysRebateVOS = customersLoanFinanceInfoByPartnerMapper.rebateDetailsList(param);
        PageInfo<FSysRebateVO> pageInfo = new PageInfo<>(fSysRebateVOS);
        return ResultBean.ofSuccess(pageInfo);
    }

    /**
     * 返利明细详情
     * @param param
     * @return
     */

    @Override
    public ResultBean rebateDetails(FSysRebateParam param) {

        Preconditions.checkNotNull(param.getType(),"参数有误：是否入账参数不能为空 ");
        Preconditions.checkNotNull(param.getPartnerId(),"参数有误：合伙人ID不能为空 ");
        Preconditions.checkNotNull(param.getPeriods(),"参数有误：期数不能为空 ");
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);

        List<FSysRebateDetailVO> fSysRebateDetailVOS = customersLoanFinanceInfoByPartnerMapper.rebateDetail(param);
        PageInfo<FSysRebateDetailVO> pageInfo = new PageInfo<>(fSysRebateDetailVOS);
        return ResultBean.ofSuccess(pageInfo);
    }

    /**
     * 返利明细-入账
     * @param param
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBean rebateEnterAccount(FinancialRebateEnterAccountparam param) {
        Preconditions.checkNotNull(param.getPartnerId(),"参数有误：合伙人ID不能为空 ");
        Preconditions.checkNotNull(param.getPeriods(),"参数有误：期数不能为空 ");

        //更新loan_order 表中的 rebate_entry=2

        param.getIds().stream().filter(Objects::nonNull).forEach(e->{
            LoanOrderDO tmpDo = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(e));
            if(tmpDo !=null){
                tmpDo.setRebateEntry(new Byte("2"));//已入账
                loanOrderDOMapper.updateByPrimaryKeySelective(tmpDo);
            }

        });

        //更新financial_rebate_detail 对应的合伙人 +期数的记录为已入账
        FinancialRebateDetailDO financialRebateDetailDO = new FinancialRebateDetailDO();
        financialRebateDetailDO.setPartnerId(param.getPartnerId());
        financialRebateDetailDO.setPeriods(param.getPeriods());
        financialRebateDetailDO.setEnterAccountFlag(new Byte("2"));//已入账
        financialRebateDetailDO.setGmtModify(new Date());
        financialRebateDetailDOMapper.updateByPrimaryKeySelective(financialRebateDetailDO);
        return ResultBean.ofSuccess("入账成功");
    }

    /**
     * 返利明细-刷新
     * @param param
     * @return
     */
    @Override
    public ResultBean rebateDetailsefresh(FSysRebateParam param) {
        Preconditions.checkNotNull(param.getPartnerId(),"参数有误：合伙人ID不能为空 ");
        Preconditions.checkNotNull(param.getPeriods(),"参数有误：期数不能为空 ");
        FSysRebateVO fSysRebateVO = customersLoanFinanceInfoByPartnerMapper.rebateDetailsefresh(param);
        if(fSysRebateVO!=null){
            FinancialRebateDetailDO financialRebateDetailDO = new FinancialRebateDetailDO();
            financialRebateDetailDO.setPartnerId(fSysRebateVO.getPartnerId());
            financialRebateDetailDO.setPeriods(fSysRebateVO.getPeriods());
            financialRebateDetailDO.setRebateAmount(fSysRebateVO.getAmount());
            financialRebateDetailDO.setGmtModify(new Date());
            int count = financialRebateDetailDOMapper.updateByPrimaryKeySelective(financialRebateDetailDO);
            Preconditions.checkArgument(count>0,"返利明细-刷新失败");
        }
        return ResultBean.ofSuccess(null,"刷新成功");
    }
}
