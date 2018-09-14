package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ConfRefundApplyAccountDO;

import java.util.List;

public interface ConfRefundApplyAccountDOMapper {

    int deleteByPrimaryKey(Long id);

    int insert(ConfRefundApplyAccountDO record);

    int insertSelective(ConfRefundApplyAccountDO record);

    ConfRefundApplyAccountDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ConfRefundApplyAccountDO record);

    int updateByPrimaryKey(ConfRefundApplyAccountDO record);

    List<ConfRefundApplyAccountDO> getAll();
}