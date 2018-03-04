package com.yunche.loan.dao;

import com.yunche.loan.domain.query.RelaQuery;
import com.yunche.loan.domain.entity.PageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PageDOMapper {
    int deleteByPrimaryKey(Long id);

    int insert(PageDO record);

    int insertSelective(PageDO record);

    PageDO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PageDO record);

    int updateByPrimaryKey(PageDO record);

    List<PageDO> getAll(@Param("status") Byte status);

    /**
     * 统计总量
     *
     * @param query
     * @return
     */
    int countMenuPageAndOperation(RelaQuery query);

    /**
     * 分页条件查询：page列表 和 page所属的menu、及page下的Operation列表
     *
     * @param query
     * @return
     */
    List<PageDO> queryMenuPageAndOperation(RelaQuery query);

    /**
     * 获取所有页面ID列表
     *
     * @param status
     * @return
     */
    List<Long> getAllIdList(@Param("status") Byte status);
}