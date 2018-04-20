package com.yunche.loan.service.impl;

import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.CostCalculate;
import com.yunche.loan.domain.entity.CostDetailsDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.RemitDetailsDO;
import com.yunche.loan.domain.param.BusinessReviewCalculateParam;
import com.yunche.loan.domain.param.BusinessReviewUpdateParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BusinessReviewService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class BusinessReviewServiceImpl implements BusinessReviewService {

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

    @Override
    public RecombinationVO detail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));
        recombinationVO.setCost(loanQueryDOMapper.selectUniversalCostDetails(orderId));
        recombinationVO.setRemit(loanQueryDOMapper.selectUniversalRemitDetails(orderId));
        recombinationVO.setCurrent_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_business_review",orderId));
        recombinationVO.setTelephone_msg(loanQueryDOMapper.selectUniversalApprovalInfo("usertask_telephone_verify",orderId));
        recombinationVO.setTelephone_des(loanTelephoneVerifyDOMapper.selectByPrimaryKey(orderId));
        return recombinationVO;
    }

    @Override
    public void update(BusinessReviewUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrder_id()),new Byte("0"));
        if(loanOrderDO == null){
            throw new BizException("此业务单不存在");
        }

        /*CostCalculateInfoVO costCalculateInfoVO = loanQueryDOMapper.selectCostCalculateInfo(Long.valueOf(param.getOrder_id()));
        if(costCalculateInfoVO == null){
            throw new BizException("异常:无法计算打款金额");
        }*/
        //打款金额校验=================================start
        /*//银行分期本金
        BigDecimal bank_period_principal = costCalculateInfoVO.getBank_period_principal();
        //返利金额
        BigDecimal return_rate_amount = initDecimal(param.getReturn_rate_amount());
        //是否月结 0 否 1 是
        String pay_month = costCalculateInfoVO.getPay_month();

        BigDecimal remitAmt = new BigDecimal(0);

        BigDecimal service_fee = initDecimal(param.getService_fee());//服务费
        BigDecimal apply_license_plate_deposit_fee = initDecimal(param.getApply_license_plate_deposit_fee());//上牌押金
        BigDecimal performance_fee = initDecimal(param.getPerformance_fee());//履约金
        BigDecimal install_gps_fee = initDecimal(param.getInstall_gps_fee());//安装gps费用
        BigDecimal risk_fee = initDecimal(param.getRisk_fee());//风险费用
        BigDecimal fair_assess_fee = initDecimal(param.getFair_assess_fee());//公正评估费
        BigDecimal apply_license_plate_out_province_fee = initDecimal(param.getApply_license_plate_out_province_fee());//上省外牌费用
        BigDecimal based_margin_fee = initDecimal(param.getBased_margin_fee());//基础保证金

        //月结 0 否 1 是
        if("1".equals(pay_month)){
            //月结算
            remitAmt =  new CostCalculate(bank_period_principal,return_rate_amount)
                    .process(param.getService_fee_type(),service_fee)
                    .process(param.getApply_license_plate_deposit_fee(),apply_license_plate_deposit_fee)
                    .process(param.getPerformance_fee_type(),performance_fee)
                    .process(param.getInstall_gps_fee_type(),install_gps_fee)
                    .process(param.getRisk_fee_type(),risk_fee)
                    .process(param.getFair_assess_fee_type(),fair_assess_fee)
                    .process(param.getApply_license_plate_out_province_fee_type(),apply_license_plate_out_province_fee)
                    .process(param.getBased_margin_fee_type(),based_margin_fee)
                    .finalResult().setScale(6,BigDecimal.ROUND_HALF_UP);
        }else{
            //日结
            remitAmt = bank_period_principal
                    .subtract(service_fee)
                    .subtract(apply_license_plate_deposit_fee)
                    .subtract(performance_fee)
                    .subtract(install_gps_fee)
                    .subtract(risk_fee)
                    .subtract(fair_assess_fee)
                    .subtract(apply_license_plate_out_province_fee)
                    .subtract(based_margin_fee).setScale(6,BigDecimal.ROUND_HALF_UP);
        }
        if(remitAmt.compareTo(new BigDecimal(param.getRemit_amount())) !=0 ){
            throw new BizException("打款金额计算错误");
        }*/

        //打款金额校验=================================end
        Long costDetailsId  = loanOrderDO.getCostDetailsId();//关联ID

        if(costDetailsId == null){
            //新增提交
            CostDetailsDO V =  BeanPlasticityUtills.copy(CostDetailsDO.class,param);
            costDetailsDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setCostDetailsId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(costDetailsDOMapper.selectByPrimaryKey(costDetailsId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                CostDetailsDO V= BeanPlasticityUtills.copy(CostDetailsDO.class,param);
                V.setId(costDetailsId);
                costDetailsDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                CostDetailsDO V= BeanPlasticityUtills.copy(CostDetailsDO.class,param);
                V.setId(costDetailsId);
                costDetailsDOMapper.updateByPrimaryKeySelective(V);
            }
        }


        Long remitDetailsId  = loanOrderDO.getRemitDetailsId();//关联ID

        if(remitDetailsId == null){
            //新增提交
            RemitDetailsDO V =  BeanPlasticityUtills.copy(RemitDetailsDO.class,param);
            remitDetailsDOMapper.insertSelective(V);
            //进行绑定
            Long id = V.getId();
            loanOrderDO.setRemitDetailsId(id);
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        }else{
            if(remitDetailsDOMapper.selectByPrimaryKey(remitDetailsId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                RemitDetailsDO V= BeanPlasticityUtills.copy(RemitDetailsDO.class,param);
                V.setId(remitDetailsId);
                remitDetailsDOMapper.insertSelective(V);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                RemitDetailsDO V= BeanPlasticityUtills.copy(RemitDetailsDO.class,param);
                V.setId(remitDetailsId);
                remitDetailsDOMapper.updateByPrimaryKeySelective(V);

            }
        }
    }

    @Override
    public BigDecimal calculate(BusinessReviewCalculateParam param) {

        //银行分期本金
        BigDecimal bank_period_principal = initDecimal(param.getBank_period_principal());
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
        if("1".equals(pay_month)){
            //月结算
            return bank_period_principal
                    .subtract(service_fee)
                    .subtract(apply_license_plate_deposit_fee)
                    .subtract(performance_fee)
                    .subtract(install_gps_fee)
                    .subtract(risk_fee)
                    .subtract(fair_assess_fee)
                    .subtract(apply_license_plate_out_province_fee)
                    .subtract(based_margin_fee)
                    .subtract(extra_fee)
                    .subtract(other_fee)
                    .subtract(return_rate_amount)
                    .setScale(2,BigDecimal.ROUND_HALF_UP);
        }else{
            //日结
            return bank_period_principal
                    .subtract(service_fee)
                    .subtract(apply_license_plate_deposit_fee)
                    .subtract(performance_fee)
                    .subtract(install_gps_fee)
                    .subtract(risk_fee)
                    .subtract(fair_assess_fee)
                    .subtract(apply_license_plate_out_province_fee)
                    .subtract(based_margin_fee)
                    .subtract(extra_fee)
                    .subtract(other_fee)
                    .setScale(2,BigDecimal.ROUND_HALF_UP);
        }
    }


    private BigDecimal initDecimal(String fee){
        BigDecimal decimal = new BigDecimal(0);
        if(!StringUtils.isBlank(fee)){
            decimal = new BigDecimal(fee);
        }
        return decimal;
    }



}
