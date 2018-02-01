package com.yunche.loan.dao.mapper;

import com.yunche.loan.domain.queryObj.BaseQuery;
import com.yunche.loan.domain.queryObj.BizModelQuery;
import com.yunche.loan.domain.dataObj.BizModelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BizModelDOMapper {
    int deleteByPrimaryKey(Long bizId);

    long insert(BizModelDO record);

    int insertSelective(BizModelDO record);

    BizModelDO selectByPrimaryKey(Long bizId);

    int updateByPrimaryKeySelective(BizModelDO record);

    int updateByPrimaryKey(BizModelDO record);

    List<BizModelDO> selectByCondition(BizModelQuery bizModelQuery);

    /**
     * 根据合伙人ID统计业务产品总量
     *
     * @param query
     * @return
     */
    int countListBizModelByPartnerId(BaseQuery query);

    /**
     * 根据合伙人ID分页查询业务产品列表
     *
     * @param query
     * @return
     */
    List<BizModelDO> listBizModelByPartnerId(BaseQuery query);
}