package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanBusinessPaymentDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.param.LoanBusinessPaymentParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanBusinessPaymentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.*;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_REFUND;


@Service
@Transactional
public class LoanBusinessPaymentServiceImpl implements LoanBusinessPaymentService{

    @Autowired
    LoanBusinessPaymentDOMapper loanBusinessPaymentDOMapper;
    @Autowired
    LoanCustomerDOMapper loanCustomerDOMapper;
    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;
    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;
    @Autowired
    LoanProcessDOMapper loanProcessDOMapper;
    @Override
    public ResultBean save(LoanBusinessPaymentParam loanBusinessPaymentParam) {
        LoanBusinessPaymentDO loanBusinessPaymentDO = new LoanBusinessPaymentDO();
        BeanUtils.copyProperties(loanBusinessPaymentParam,loanBusinessPaymentDO);
        loanBusinessPaymentDO.setStatus(VALID_STATUS);

        LoanBusinessPaymentDO loanBusinessPaymentDO1 = loanBusinessPaymentDOMapper.selectByPrimaryKey(loanBusinessPaymentParam.getOrderId());
        if(loanBusinessPaymentDO1==null){
            loanBusinessPaymentDO.setGmtCreate(new Date());
            int count = loanBusinessPaymentDOMapper.insert(loanBusinessPaymentDO);
            Preconditions.checkArgument(count>0,"业务申请单保存失败");
        }else{
            loanBusinessPaymentDO.setGmtModify(new Date());
            int count = loanBusinessPaymentDOMapper.updateByPrimaryKeySelective(loanBusinessPaymentDO);
            Preconditions.checkArgument(count>0,"业务申请单更新失败");
        }
        return   ResultBean.ofSuccess("创建成功");
    }

    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO();
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        //客户基本信息
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));

        //业务付款单
        LoanBusinessPaymentDO loanBusinessPaymentDO = loanBusinessPaymentDOMapper.selectByPrimaryKey(orderId);
        LoanBusinessPaymentVO loanBusinessPaymentVO = new LoanBusinessPaymentVO();
        if(loanBusinessPaymentDO!=null){
            BeanUtils.copyProperties(loanBusinessPaymentDO,loanBusinessPaymentVO);
        }
        if(TASK_PROCESS_REFUND.equals(loanProcessDO.getRemitReview())){
            loanBusinessPaymentVO.setIsSendback(K_YORN_YES);
        }else{
            loanBusinessPaymentVO.setIsSendback(K_YORN_NO);
        }
        recombinationVO.setLoanBusinessPaymentVO(loanBusinessPaymentVO);

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
