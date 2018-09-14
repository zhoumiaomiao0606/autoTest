package com.yunche.loan.service.impl;

import com.yunche.loan.domain.entity.ConfRefundApplyAccountDO;
import com.yunche.loan.mapper.ConfRefundApplyAccountDOMapper;
import com.yunche.loan.service.ConfRefundApplyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/14
 */
@Service
public class ConfRefundApplyAccountServiceImpl implements ConfRefundApplyAccountService {

    @Autowired
    private ConfRefundApplyAccountDOMapper confRefundApplyAccountDOMapper;


    @Override
    public List<ConfRefundApplyAccountDO> listAll() {

        List<ConfRefundApplyAccountDO> confRefundApplyAccountDOS = confRefundApplyAccountDOMapper.getAll();

        return confRefundApplyAccountDOS;
    }
}
