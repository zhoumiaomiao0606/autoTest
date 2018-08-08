package com.yunche.loan.mapper;

import com.yunche.loan.domain.entity.BizAreaRelaPartnerDO;
import com.yunche.loan.domain.entity.BizAreaRelaPartnerDOKey;
import com.yunche.loan.domain.vo.BizAreaPartnerVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizAreaRelaPartnerDOMapper {
    List<BizAreaPartnerVO> selectByAreaId(Long bizAreaId);

    void deleteByBizAreaId(Long bizAreaId);

    int deleteByPrimaryKey(BizAreaRelaPartnerDOKey key);

    int insert(BizAreaRelaPartnerDO record);

    int insertSelective(BizAreaRelaPartnerDO record);

    BizAreaRelaPartnerDO selectByPrimaryKey(BizAreaRelaPartnerDOKey key);

    int updateByPrimaryKeySelective(BizAreaRelaPartnerDO record);

    int updateByPrimaryKey(BizAreaRelaPartnerDO record);
}