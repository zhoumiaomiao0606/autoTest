package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.YuncheBoardDO;
import com.yunche.loan.domain.param.YuncheBoardParam;
import com.yunche.loan.mapper.YuncheBoardDOMapper;
import com.yunche.loan.service.YuncheBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class YuncheBoardServiceImpl implements YuncheBoardService
{
    @Autowired
    private YuncheBoardDOMapper yuncheBoardDOMapper;

    @Override
    public ResultBean<Long> create(YuncheBoardParam yuncheBoardParam)
    {
        return null;
    }

    @Override
    public ResultBean update(YuncheBoardParam yuncheBoardParam)
    {

        //VehicleHandleDO vehicleHandleDO =new VehicleHandleDO();
        //BeanUtils.copyProperties(param, vehicleHandleDO);
        if (null == yuncheBoardParam.getId()) {
            // create
            yuncheBoardParam.setApplyMan(SessionUtils.getLoginUser().getName());
            int count = yuncheBoardDOMapper.insertSelective(yuncheBoardParam);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            yuncheBoardParam.setApplyMan(SessionUtils.getLoginUser().getName());
            int count = yuncheBoardDOMapper.updateByPrimaryKeySelective(yuncheBoardParam);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(yuncheBoardParam.getId(), "保存成功");
    }

    @Override
    public ResultBean listAll(YuncheBoardParam yuncheBoardParam) {
        PageHelper.startPage(yuncheBoardParam.getPageIndex(), yuncheBoardParam.getPageSize(), true);
        List<YuncheBoardDO> list = yuncheBoardDOMapper.selectBoards(yuncheBoardParam);
        PageInfo<YuncheBoardDO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<Void> delete(Integer id)
    {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = yuncheBoardDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public YuncheBoardDO detail(Integer id)
    {
        Preconditions.checkNotNull(id,"id不能为空！！！");
        return yuncheBoardDOMapper.selectByPrimaryKey(id);
    }
}
