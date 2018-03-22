package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.RepaymentRecordDO;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import com.yunche.loan.domain.param.RepaymentRecordParam;
import com.yunche.loan.domain.vo.RepaymentRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepaymentRecordDOMapper {
    int deleteByPrimaryKey(RepaymentRecordDOKey key);

    int insert(RepaymentRecordDO record);

    int insertSelective(RepaymentRecordDO record);

    RepaymentRecordDO selectByPrimaryKey(RepaymentRecordDOKey key);

    int updateByPrimaryKeySelective(RepaymentRecordDO record);

    int updateByPrimaryKey(RepaymentRecordDO record);

    //还款记录列表
    List<RepaymentRecordVO> selectCustomerOverdueRepayList();

    //还款记录详情查询
    RepaymentRecordParam selectCustomerOverdueRepayDetail(@Param("orderId")Long orderId);

}