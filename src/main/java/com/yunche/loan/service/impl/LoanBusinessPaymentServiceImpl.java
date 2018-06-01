package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.entity.RemitDetailsDO;
import com.yunche.loan.domain.param.LoanBusinessPaymentParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanBusinessPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.K_YORN_NO;
import static com.yunche.loan.config.constant.BaseConst.K_YORN_YES;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_REFUND;


@Service
@Transactional
public class LoanBusinessPaymentServiceImpl implements LoanBusinessPaymentService{


    @Autowired
    LoanCustomerDOMapper loanCustomerDOMapper;
    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;
    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;
    @Autowired
    LoanProcessDOMapper loanProcessDOMapper;
    @Autowired
    RemitDetailsDOMapper remitDetailsDOMapper;
    @Override
    public ResultBean save(LoanBusinessPaymentParam loanBusinessPaymentParam) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanBusinessPaymentParam.getOrderId(), null);
        Long remitDetailsId  = loanOrderDO.getRemitDetailsId();//关联ID

        if(remitDetailsId == null){
            RemitDetailsDO remitDetailsDO = new RemitDetailsDO();
            remitDetailsDO.setBeneficiary_account(loanBusinessPaymentParam.getReceiveAccount());//收款账户
            remitDetailsDO.setBeneficiary_account_number(loanBusinessPaymentParam.getAccountNumber());//收款账号
            remitDetailsDO.setBeneficiary_bank(loanBusinessPaymentParam.getReceiveOpenBank());//收款银行
            remitDetailsDO.setPayment_organization(loanBusinessPaymentParam.getPaymentOrganization());//付款组织
            remitDetailsDO.setApplication_date(new Date());//申请日期
            remitDetailsDOMapper.insertSelective(remitDetailsDO);
            loanOrderDO.setRemitDetailsId(remitDetailsDO.getId());
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

        }else{
            if(remitDetailsDOMapper.selectByPrimaryKey(remitDetailsId) == null){
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                RemitDetailsDO remitDetailsDO =  new RemitDetailsDO();
                remitDetailsDO.setBeneficiary_account(loanBusinessPaymentParam.getReceiveAccount());//收款账户
                remitDetailsDO.setBeneficiary_account_number(loanBusinessPaymentParam.getAccountNumber());//收款账号
                remitDetailsDO.setBeneficiary_bank(loanBusinessPaymentParam.getReceiveOpenBank());//收款银行
                remitDetailsDO.setPayment_organization(loanBusinessPaymentParam.getPaymentOrganization());//付款组织
                remitDetailsDO.setApplication_date(new Date());//申请日期
                remitDetailsDO.setId(remitDetailsId);
                remitDetailsDOMapper.insertSelective(remitDetailsDO);
                //但是不用更新loanOrder 因为已经存在
            }else {
                //代表存在
                //进行更新
                RemitDetailsDO remitDetailsDO =  new RemitDetailsDO();
                remitDetailsDO.setBeneficiary_account(loanBusinessPaymentParam.getReceiveAccount());//收款账户
                remitDetailsDO.setBeneficiary_account_number(loanBusinessPaymentParam.getAccountNumber());//收款账号
                remitDetailsDO.setBeneficiary_bank(loanBusinessPaymentParam.getReceiveOpenBank());//收款银行
                remitDetailsDO.setPayment_organization(loanBusinessPaymentParam.getPaymentOrganization());//付款组织
                remitDetailsDO.setApplication_date(new Date());//申请日期
                remitDetailsDO.setId(remitDetailsId);
                remitDetailsDOMapper.updateByPrimaryKeySelective(remitDetailsDO);

            }
        }

//        LoanBusinessPaymentDO loanBusinessPaymentDO1 = loanBusinessPaymentDOMapper.selectByPrimaryKey(loanBusinessPaymentParam.getOrderId());
//        if(loanBusinessPaymentDO1==null){
//            loanBusinessPaymentDO.setGmtCreate(new Date());
//            int count = loanBusinessPaymentDOMapper.insertSelective(loanBusinessPaymentDO);
//            Preconditions.checkArgument(count>0,"业务申请单保存失败");
//        }else{
//            loanBusinessPaymentDO.setGmtModify(new Date());
//            int count = loanBusinessPaymentDOMapper.updateByPrimaryKeySelective(loanBusinessPaymentDO);
//            Preconditions.checkArgument(count>0,"业务申请单更新失败");
//        }
        return   ResultBean.ofSuccess("创建成功");
    }

    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO();
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO,"订单不存在");
        //客户基本信息
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));

        UniversalRemitDetails universalRemitDetails = loanQueryDOMapper.selectUniversalRemitDetails(orderId);
        if(universalRemitDetails == null){
            universalRemitDetails = new UniversalRemitDetails();
        }
        if(TASK_PROCESS_REFUND.equals(loanProcessDO.getRemitReview())){
            universalRemitDetails.setRemit_is_sendback(K_YORN_YES);
        }else{
            universalRemitDetails.setRemit_is_sendback(K_YORN_NO);
        }
        recombinationVO.setRemit(universalRemitDetails);

        //共贷人信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        recombinationVO.setCustomers(customers);

        //贷款信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);
        recombinationVO.setFinancial(financialSchemeVO);



        return ResultBean.ofSuccess(recombinationVO);
    }
}
