package com.yunche.loan.mapper;

import com.yunche.loan.domain.query.BizModelQuery;
import com.yunche.loan.domain.entity.BizModelDO;
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
    int countListBizModelByPartnerId(BizModelQuery query);

    /**
     * @param query
     * @return
     */
    List<BizModelDO> listBizModelByPartnerId(BizModelQuery query);

    /**
     * 根据ID列表查询
     *
     * @param bizIdList
     * @return
     */
    List<BizModelDO> getByIdList(List<Long> bizIdList);
}