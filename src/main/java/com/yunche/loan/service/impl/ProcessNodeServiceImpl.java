package com.yunche.loan.service.impl;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.InstProcessNodeDOMapper;
import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import com.yunche.loan.service.ProcessNodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
public class ProcessNodeServiceImpl implements ProcessNodeService {

    @Autowired
    private InstProcessNodeDOMapper instProcessNodeDOMapper;

    @Override
    public ResultBean<InstProcessNodeDO> insert(InstProcessNodeDO instProcessNodeDO) {
        instProcessNodeDOMapper.insert(instProcessNodeDO);

        return ResultBean.ofSuccess(instProcessNodeDO, "创建流程节点成功");
    }

    @Override
    public ResultBean<InstProcessNodeDO> update(InstProcessNodeDO instProcessNodeDO) {
        instProcessNodeDOMapper.insert(instProcessNodeDO);

        return ResultBean.ofSuccess(instProcessNodeDO, "创建流程节点成功");
    }
}
