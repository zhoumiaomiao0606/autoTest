package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import com.yunche.loan.domain.param.RepaymentRecordParam;
import com.yunche.loan.domain.vo.RepaymentRecordVO;

import java.util.List;

public interface RepaymentRecordService {


    ResultBean<List<RepaymentRecordVO>> query(int partnerId,int areaId);


    ResultBean<RepaymentRecordParam> detail(Long  orderId);


    ResultBean<Void> importFile(String pathFileName);





}
