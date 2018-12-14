package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.AllCustDetailParam;
import com.yunche.loan.domain.param.CustomerListQuery;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.domain.vo.*;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/1/29.
 */
public interface LoanCustomerService {

    ResultBean<Void> faceOff(Long orderId, Long principalLenderId, Long commonLenderId);

    ResultBean<CustDetailVO> detailAll(Long orderId, Byte fileUploadType);

    ResultBean<Void> updateAll(AllCustDetailParam allCustDetailParam);

    ResultBean<Long> create(LoanCustomerDO loanCustomerDO);

    ResultBean<Void> update(LoanCustomerDO loanCustomerDO);

    CustomerVO getById(Long id);

    ResultBean<LoanRepeatVO> checkRepeat(String idCard, Long orderId);

    ResultBean<CustDetailVO> customerDetail(Long orderId);

    ResultBean<Long> addRelaCustomer(CustomerParam param);

    ResultBean<Long> delRelaCustomer(Long customerId);

    BankAndSocietyResultVO bankPicExport(List<Long> list);

    BankAndSocietyResultVO societyPicExport(List<Long> list);

    Long enable(String ids, Byte enableType);

    /**
     * 重置订单下所有客户的可编辑标记
     *
     * @param principalId
     */
    void updateCustomerEnable(Long principalId);

    ResultBean<List<CustomerListVO>> queryCustomerList(CustomerListQuery customerListQuery);
}
