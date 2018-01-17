package com.yunche.loan.service.impl.configure.info.address;

import com.google.common.base.Preconditions;
import com.yunche.loan.vo.configure.info.address.BaseAreaVO;
import com.yunche.loan.mapper.configure.info.address.BaseAeraDOMapper;
import com.yunche.loan.obj.configure.info.address.BaseAreaDO;
import com.yunche.loan.query.configure.info.address.BaseAreaQuery;
import com.yunche.loan.result.ResultBean;
import com.yunche.loan.service.configure.info.address.BaseAreaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
public class BaseAreaServiceImpl implements BaseAreaService {

    @Autowired
    private BaseAeraDOMapper baseAeraDOMapper;

    @Override
    public ResultBean<BaseAreaVO> getById(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        BaseAreaDO baseAreaDO = baseAeraDOMapper.selectByPrimaryKey(areaId);
        Preconditions.checkNotNull(baseAreaDO, "areaId有误，数据不存在.");

        BaseAreaVO baseAreaVO = new BaseAreaVO();
        BeanUtils.copyProperties(baseAreaDO, baseAreaVO);

        return ResultBean.ofSuccess(baseAreaVO);
    }

    @Override
    public ResultBean<Void> create(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");

        int count = baseAeraDOMapper.insert(baseAreaDO);
        Preconditions.checkArgument(count > 1, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(BaseAreaDO baseAreaDO) {
        Preconditions.checkArgument(null != baseAreaDO && null != baseAreaDO.getAreaId(), "areaId不能为空");

        int count = baseAeraDOMapper.updateByPrimaryKeySelective(baseAreaDO);
        Preconditions.checkArgument(count > 1, "更新失败");
        return ResultBean.ofSuccess(null, "更新成功");
    }

    @Override
    public ResultBean<Void> delete(Long areaId) {
        Preconditions.checkNotNull(areaId, "areaId不能为空");

        int count = baseAeraDOMapper.deleteByPrimaryKey(areaId);
        Preconditions.checkArgument(count > 1, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<BaseAreaVO> query(BaseAreaQuery query) {

        List<BaseAreaDO> baseAreaDOS = baseAeraDOMapper.query(query);

        return null;
    }


}
