package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfThirdPartyMoneyDO;
import com.yunche.loan.domain.query.ConfThirdPartyMoneyQuery;
import com.yunche.loan.mapper.ConfThirdPartyMoneyDOMapper;
import com.yunche.loan.service.ConfThirdPartyMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/5
 */
@Service
public class ConfThirdPartyMoneyServiceImpl implements ConfThirdPartyMoneyService {

    @Autowired
    private ConfThirdPartyMoneyDOMapper confThirdPartyMoneyDOMapper;


    @Override
    public Long create(ConfThirdPartyMoneyDO confThirdPartyMoneyDO) {

        confThirdPartyMoneyDO.setGmtCreate(new Date());
        confThirdPartyMoneyDO.setGmtModify(new Date());

        int count = confThirdPartyMoneyDOMapper.insertSelective(confThirdPartyMoneyDO);
        Preconditions.checkArgument(count > 0, "插入失败");

        return confThirdPartyMoneyDO.getId();
    }

    @Override
    public Void update(ConfThirdPartyMoneyDO confThirdPartyMoneyDO) {
        Preconditions.checkNotNull(confThirdPartyMoneyDO, "id不能为空");
        Preconditions.checkNotNull(confThirdPartyMoneyDO.getId(), "id不能为空");

        confThirdPartyMoneyDO.setGmtModify(new Date());

        int count = confThirdPartyMoneyDOMapper.insertSelective(confThirdPartyMoneyDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return null;
    }

    @Override
    public Void delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = confThirdPartyMoneyDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return null;
    }

    @Override
    public ConfThirdPartyMoneyDO detail(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(id);

        return confThirdPartyMoneyDO;
    }

    @Override
    public ResultBean<List<ConfThirdPartyMoneyDO>> query(ConfThirdPartyMoneyQuery query) {

        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<ConfThirdPartyMoneyDO> list = confThirdPartyMoneyDOMapper.query(query);
        PageInfo<ConfThirdPartyMoneyDO> pageInfo = new PageInfo<>(list);

        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(),
                pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
