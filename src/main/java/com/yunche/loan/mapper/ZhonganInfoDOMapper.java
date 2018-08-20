package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.ZhonganInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface ZhonganInfoDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ZhonganInfoDO record);

    int insertSelective(ZhonganInfoDO record);

    ZhonganInfoDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ZhonganInfoDO record);

    int updateByPrimaryKey(ZhonganInfoDO record);

    List<ZhonganInfoDO> selectByOrderId(@Param("idcard") String idCard, @Param("customername") String customername);
}