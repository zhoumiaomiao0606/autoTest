package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.yunche.loan.config.common.FinanceConfig;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BusinessReviewService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanProcessEnum.BUSINESS_REVIEW;
import static com.yunche.loan.config.constant.LoanProcessEnum.TELEPHONE_VERIFY;

@Service
@Transactional
public class BusinessReviewServiceImpl implements BusinessReviewService
{

    @Autowired
    private FinanceConfig financeConfig;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private CostDetailsDOMapper costDetailsDOMapper;

    @Resource
    private RemitDetailsDOMapper remitDetailsDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanTelephoneVerifyDOMapper loanTelephoneVerifyDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;

    @Resource
    private BusinessReviewManager businessReviewManager;


    @Override
    public RecombinationVO detail(Long orderId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        universalInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(universalInfoVO);
        UniversalCostDetailsVO universalCostDetailsVO = loanQueryDOMapper.selectUniversalCostDetails(orderId);
        recombinationVO.setCost(universalCostDetailsVO);
        UniversalRemitDetails universalRemitDetails = loanQueryDOMapper.selectUniversalRemitDetails(orderId);
        recombinationVO.setRemit(universalRemitDetails);
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo(BUSINESS_REVIEW.getCode(), orderId));
        recombinationVO.setTelephone_msg(loanQueryDOMapper.selectUniversalApprovalInfo(TELEPHONE_VERIFY.getCode(), orderId));
        recombinationVO.setTelephone_des(loanTelephoneVerifyDOMapper.selectByPrimaryKey(orderId));
        recombinationVO.setSupplement(loanQueryService.selectUniversalInfoSupplementHistory(orderId));
        recombinationVO.setCustomers(customers);

        //如果已经保存过的，则查询出原保存规则调用微调规则请求
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }
        Long costDetailsId = loanOrderDO.getCostDetailsId();//关联ID

        CostDetailsDO costDetailsDO = null;
        if (costDetailsId != null)
        {
            costDetailsDO = costDetailsDOMapper.selectByPrimaryKey(costDetailsId);
        }


        if (costDetailsId == null)
        {

            //请求财务系统初始数据
            ParternerRuleParam param =new ParternerRuleParam();
            param.setPartnerId(loanBaseInfoDO.getPartnerId());
            param.setPayMonth(universalInfoVO.getPartner_pay_month());
            param.setCarType(universalInfoVO.getCar_type());
            param.setFinancialLoanAmount(universalInfoVO.getFinancial_loan_amount());
            param.setFinancialBankPeriodPrincipal(universalInfoVO.getFinancial_bank_period_principal());
            param.setRate(universalInfoVO.getFinancial_sign_rate());
            param.setYear(universalInfoVO.getFinancial_loan_time());
            param.setCarGpsNum(universalInfoVO.getCar_gps_num());
            param.setBankAreaId(universalInfoVO.getBank_id());
            param.setBankRate(universalInfoVO.getFinancial_bank_rate());

            //设置钥匙风险金
            //钥匙风险金信息
            param.setKeyRiskPremiumFee(universalCostDetailsVO.getKey_risk_premium_fee());

            //设置额外费用
            param.setCostExtraFee(universalCostDetailsVO.getCost_extra_fee());

            //设置车商返利
            if (universalRemitDetails!=null)
            {
                param.setRebateTeant(universalRemitDetails.getCar_dealer_rebate());
            }


            //加收保证金
            param.setBail(universalInfoVO.getFinancial_cash_deposit());
            //上牌地城市id
            param.setAreaId(universalInfoVO.getVehicle_apply_license_plate_area_id());

            String financeResult = businessReviewManager.financeUnisal(param,financeConfig.getHOST(), "/costcalculation");
        FinanceResult financeResult1 = new FinanceResult();
        if (financeResult !=null && !"".equals(financeResult))
        {
            Gson gson = new Gson();
             financeResult1 = gson.fromJson(financeResult, FinanceResult.class);
        }

        recombinationVO.setFinanceResult1(financeResult1);

            recombinationVO.setQtype(1);

            return recombinationVO;
        }else
        {
            //CostDetailsDO costDetailsDO = costDetailsDOMapper.selectByPrimaryKey(costDetailsId);
            /*String listrules = costDetailsDO.getListrule();
            if (listrules !=null && !"".equals(listrules)){
                Gson gson = new Gson();
                List<RuleDetailPara> listRule =new ArrayList<>();
                listRule =  gson.fromJson(listrules,listRule.getClass());

                ParternerRuleSharpTuningeParam param =new ParternerRuleSharpTuningeParam();
            }*/
            recombinationVO.setJsonString(costDetailsDO.getListrule());

            recombinationVO.setQtype(2);
            return recombinationVO;
        }
    }

    @Override
    public void update(BusinessReviewUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()));
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }

        System.out.println("=====保存参数"+param);



        //打款金额校验=================================end
        Long costDetailsId = loanOrderDO.getCostDetailsId();//关联ID

        if (costDetailsId == null) {
            //新增提交
            CostDetailsDO V = BeanPlasticityUtills.copy(CostDetailsDO.class, param);

            System.out.println("=====保存数据库"+V);

            costDetailsDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setCostDetailsId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {
            if (costDetailsDOMapper.selectByPrimaryKey(costDetailsId) == null) {
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                CostDetailsDO V = BeanPlasticityUtills.copy(CostDetailsDO.class, param);
                System.out.println("=====保存数据库"+V);

                V.setId(costDetailsId);
                costDetailsDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            } else {
                //代表存在
                //进行更新
                CostDetailsDO V = BeanPlasticityUtills.copy(CostDetailsDO.class, param);
                System.out.println("=====保存数据库"+V);

                V.setId(costDetailsId);
                costDetailsDOMapper.updateByPrimaryKeySelective(V);
            }
        }


        Long remitDetailsId = loanOrderDO.getRemitDetailsId();//关联ID

        if (remitDetailsId == null) {
            //新增提交
            RemitDetailsDO V = BeanPlasticityUtills.copy(RemitDetailsDO.class, param);
            remitDetailsDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setRemitDetailsId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        } else {
            if (remitDetailsDOMapper.selectByPrimaryKey(remitDetailsId) == null) {
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                RemitDetailsDO V = BeanPlasticityUtills.copy(RemitDetailsDO.class, param);
                V.setId(remitDetailsId);
                remitDetailsDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            } else {
                //代表存在
                //进行更新
                RemitDetailsDO V = BeanPlasticityUtills.copy(RemitDetailsDO.class, param);
                V.setId(remitDetailsId);
                remitDetailsDOMapper.updateByPrimaryKeySelective(V);

            }
        }
    }

    @Override
    public BigDecimal calculate(BusinessReviewCalculateParam param) {

        //银行分期本金
        BigDecimal bank_period_principal = initDecimal(param.getBank_period_principal());
        BigDecimal total = bank_period_principal;
        //返利金额
        BigDecimal return_rate_amount = initDecimal(param.getReturn_rate_amount());
        //是否月结 0 否 1 是
        String pay_month = param.getPay_month();


        BigDecimal service_fee = initDecimal(param.getService_fee());//服务费
        BigDecimal apply_license_plate_deposit_fee = initDecimal(param.getApply_license_plate_deposit_fee());//上牌押金
        BigDecimal performance_fee = initDecimal(param.getPerformance_fee());//履约金
        BigDecimal install_gps_fee = initDecimal(param.getInstall_gps_fee());//安装gps费用
        BigDecimal risk_fee = initDecimal(param.getRisk_fee());//风险费用
        BigDecimal fair_assess_fee = initDecimal(param.getFair_assess_fee());//公正评估费
        BigDecimal apply_license_plate_out_province_fee = initDecimal(param.getApply_license_plate_out_province_fee());//上省外牌费用
        BigDecimal based_margin_fee = initDecimal(param.getBased_margin_fee());//基础保证金
        BigDecimal extra_fee = initDecimal(param.getExtra_fee());
        BigDecimal other_fee = initDecimal(param.getOther_fee());
        //月结 0 否 1 是
        if ("1".equals(pay_month)) {
            if(!"3".equals(param.getService_fee_type())){
                total = total.subtract(service_fee);
            }
            if(!"3".equals(param.getApply_license_plate_deposit_fee_type())){
                total = total.subtract(apply_license_plate_deposit_fee);
            }

            if(!"3".equals(param.getPerformance_fee())){
                total = total.subtract(performance_fee);
            }
            if(!"3".equals(param.getInstall_gps_fee())){
                total = total.subtract(install_gps_fee);
            }

            if(!"3".equals(param.getRisk_fee())){
                total = total.subtract(risk_fee);
            }
            if(!"3".equals(param.getFair_assess_fee())){
                total = total.subtract(fair_assess_fee);
            }

            if(!"3".equals(param.getApply_license_plate_out_province_fee())){
                total = total.subtract(apply_license_plate_out_province_fee);
            }
            if(!"3".equals(param.getBased_margin_fee())){
                total = total.subtract(based_margin_fee);
            }

            if(!"3".equals(param.getExtra_fee())){
                total = total.subtract(extra_fee);
            }
            if(!"3".equals(param.getOther_fee())){
                total = total.subtract(other_fee);
            }
            total = total.subtract(return_rate_amount).setScale(2, BigDecimal.ROUND_HALF_UP);
            //月结算
            return total;
        } else {
            //日结
            if(!"3".equals(param.getService_fee_type())){
                total = total.subtract(service_fee);
            }
            if(!"3".equals(param.getApply_license_plate_deposit_fee_type())){
                total = total.subtract(apply_license_plate_deposit_fee);
            }

            if(!"3".equals(param.getPerformance_fee())){
                total = total.subtract(performance_fee);
            }
            if(!"3".equals(param.getInstall_gps_fee())){
                total = total.subtract(install_gps_fee);
            }

            if(!"3".equals(param.getRisk_fee())){
                total = total.subtract(risk_fee);
            }
            if(!"3".equals(param.getFair_assess_fee())){
                total = total.subtract(fair_assess_fee);
            }

            if(!"3".equals(param.getApply_license_plate_out_province_fee())){
                total = total.subtract(apply_license_plate_out_province_fee);
            }
            if(!"3".equals(param.getBased_margin_fee())){
                total = total.subtract(based_margin_fee);
            }

            if(!"3".equals(param.getExtra_fee())){
                total = total.subtract(extra_fee);
            }
            if(!"3".equals(param.getOther_fee())){
                total = total.subtract(other_fee);
            }
            total = total.setScale(2, BigDecimal.ROUND_HALF_UP);
            return total;
        }
    }

    @Override
    public ResultBean parternerRuleSharpTuning(ParternerRuleSharpTuningeParam param)
    {
        Preconditions.checkNotNull(param.getCostType(),"消费类型不能为空");
        Preconditions.checkNotNull(param.getOrderId(),"订单id不能为空");

        //请求财务系统初始数据
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(param.getOrderId());
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(param.getOrderId());
        UniversalRemitDetails universalRemitDetails = loanQueryDOMapper.selectUniversalRemitDetails(param.getOrderId());

        param.setPartnerId(loanBaseInfoDO.getPartnerId());
        param.setPayMonth(universalInfoVO.getPartner_pay_month());
        param.setCarType(universalInfoVO.getCar_type());
        param.setFinancialLoanAmount(universalInfoVO.getFinancial_loan_amount());
        param.setFinancialBankPeriodPrincipal(universalInfoVO.getFinancial_bank_period_principal());
        param.setRate(universalInfoVO.getFinancial_sign_rate());
        param.setYear(universalInfoVO.getFinancial_loan_time());
        param.setCarGpsNum(universalInfoVO.getCar_gps_num());

        //设置车商返利
        if (universalRemitDetails!=null)
        {
            param.setRebateTeant(universalRemitDetails.getCar_dealer_rebate());
        }
        //加收保证金
        param.setBail(universalInfoVO.getFinancial_cash_deposit());

        try {
            String financeResult = businessReviewManager.financeUnisal(param,financeConfig.getHOST(),"/costcalculation/detail");
            FinanceResult financeResult1 = new FinanceResult();
            if (financeResult !=null && !"".equals(financeResult))
            {
                Gson gson = new Gson();
                financeResult1 = gson.fromJson(financeResult, FinanceResult.class);
            }

            if (!"200".equals(financeResult1.getResultCode()))
            {
                throw new BizException("请求财务计算错误！！");
            }

            return ResultBean.ofSuccess(financeResult1);
        }catch (Exception e)
        {
            return ResultBean.ofError("请求财务错误");
        }
    }


    private BigDecimal initDecimal(String fee) {
        BigDecimal decimal = new BigDecimal(0);
        if (!StringUtils.isBlank(fee)) {
            decimal = new BigDecimal(fee);
        }
        return decimal;
    }


}
