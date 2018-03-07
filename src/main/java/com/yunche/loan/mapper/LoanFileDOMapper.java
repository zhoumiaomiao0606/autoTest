package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.LoanFileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoanFileDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(LoanFileDO record);

    int insertSelective(LoanFileDO record);

    LoanFileDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(LoanFileDO record);

    int updateByPrimaryKey(LoanFileDO record);

    /**
     * 根据客户ID查询列表
     *
     * @param customerId
     * @return
     */
    List<LoanFileDO> listByCustomerId(Long customerId);
}