package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.QueryObj.BaseQuery;
import com.yunche.loan.domain.QueryObj.PartnerQuery;
import com.yunche.loan.domain.QueryObj.RelaQuery;
import com.yunche.loan.domain.dataObj.PartnerDO;
import com.yunche.loan.domain.param.PartnerParam;
import com.yunche.loan.domain.viewObj.BizModelVO;
import com.yunche.loan.domain.viewObj.EmployeeVO;
import com.yunche.loan.domain.viewObj.PartnerVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
public interface PartnerService {
    ResultBean<Long> create(PartnerParam partnerParam);

    ResultBean<Void> update(PartnerDO partnerDO);

    ResultBean<Void> delete(Long id);

    ResultBean<PartnerVO> getById(Long id);

    ResultBean<List<PartnerVO>> query(PartnerQuery query);

    ResultBean<List<BizModelVO>> listBizModel(BaseQuery query);

    ResultBean<Void> bindBizModel(Long id, String bizModelIds);

    ResultBean<Void> unbindBizModel(Long id, String bizModelIds);

    ResultBean<List<EmployeeVO>> listEmployee(RelaQuery query);

    ResultBean<Void> bindEmployee(Long id, String employeeIds);

    ResultBean<Void> unbindEmployee(Long id, String employeeIds);
}
