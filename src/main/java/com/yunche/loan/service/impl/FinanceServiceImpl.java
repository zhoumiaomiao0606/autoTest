package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.RemitDetailsParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.FinanceService;
import com.yunche.loan.service.LoanProcessLogService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanProcessEnum.BUSINESS_REVIEW;
import static com.yunche.loan.config.constant.LoanProcessEnum.LOAN_REVIEW;
import static com.yunche.loan.config.constant.LoanProcessEnum.TELEPHONE_VERIFY;

@Service
public class FinanceServiceImpl implements FinanceService
{

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

    @Resource
    private BusinessReviewManager businessReviewManager;


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

        if (remitDetailsDO ==null)
        {
             remitDetailsDO = new RemitDetailsDO();
            remitDetailsDO.setRemit_account(remitDetailsParam.getRemit_account());
            remitDetailsDO.setRemit_bank(remitDetailsParam.getRemit_bank());
            remitDetailsDO.setRemit_account_number(remitDetailsParam.getRemit_account_number());
            remitDetailsDO.setRemit_business_id(remitDetailsParam.getRemit_business_id());
            remitDetailsDOMapper.insertSelective(remitDetailsDO);

            //进行绑定
            Long id = remitDetailsDO.getId();
            loanOrderDO.setRemitDetailsId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

            return ResultBean.ofSuccess("成功");

        }

        remitDetailsDO.setRemit_account(remitDetailsParam.getRemit_account());
        remitDetailsDO.setRemit_bank(remitDetailsParam.getRemit_bank());
        remitDetailsDO.setRemit_account_number(remitDetailsParam.getRemit_account_number());
        remitDetailsDO.setRemit_business_id(remitDetailsParam.getRemit_business_id());

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
    public ResultBean getAccount()
    {
        String financeResult = businessReviewManager.getFinanceUnisal("/costcalculation/finance/account");

        System.out.println("====="+financeResult);

        FinanceRemitResult financeResult1 = new FinanceRemitResult();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
            financeResult1 = gson.fromJson(financeResult, FinanceRemitResult.class);
        }
        if(financeResult1 != null && !financeResult1.getResultCode().equals("200"))
        {
            throw new BizException("请求财务系统错误");
        }
        return ResultBean.ofSuccess(financeResult1);
    }
}
