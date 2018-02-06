package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import org.activiti.engine.task.Task;

import java.util.List;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
public interface ProcessNodeService {

    ResultBean<InstProcessNodeDO> insert(InstProcessNodeDO instProcessNodeDO);

    ResultBean<InstProcessNodeDO> update(InstProcessNodeDO instProcessNodeDO);

}
