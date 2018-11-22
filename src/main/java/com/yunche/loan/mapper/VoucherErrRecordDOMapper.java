package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.VoucherErrRecordDO;
import com.yunche.loan.domain.query.FinanceErrQuery;
import com.yunche.loan.domain.vo.FinanceErrVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VoucherErrRecordDOMapper {
    int deleteByPrimaryKey(String serialNo);

    int insert(VoucherErrRecordDO record);

    int insertSelective(VoucherErrRecordDO record);

    VoucherErrRecordDO selectByPrimaryKey(String serialNo);

    int updateByPrimaryKeySelective(VoucherErrRecordDO record);

    int updateByPrimaryKey(VoucherErrRecordDO record);

    List<FinanceErrVO> listErr(FinanceErrQuery financeErrQuery);
}