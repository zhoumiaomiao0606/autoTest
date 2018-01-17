package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.common.BaseExceptionEnum;
import com.yunche.loan.domain.valueObj.PaddingCompanyVO;
import com.yunche.loan.dao.mapper.PaddingCompanyDOMapper;
import com.yunche.loan.domain.dataObj.PaddingCompanyDO;
import com.yunche.loan.domain.QueryObj.BaseAreaQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.PaddingCompanyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Service
public class PaddingCompanyServiceImpl implements PaddingCompanyService {

    private static final Logger logger = LoggerFactory.getLogger(PaddingCompanyServiceImpl.class);

    @Autowired
    PaddingCompanyDOMapper paddingCompanyDOMapper;

    @Override
    public ResultBean<Void> create(PaddingCompanyDO paddingCompanyDO) {
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getName()), "名称不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getOfficePhone()), "办公室电话不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getFax()), "传真不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getBank()), "开户行不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getBankAccount()), "银行账号不能为空");

        int count = paddingCompanyDOMapper.insertSelective(paddingCompanyDO);
        Preconditions.checkArgument(count > 1, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(PaddingCompanyDO paddingCompanyDO) {
        Preconditions.checkNotNull(paddingCompanyDO.getId(), "id不能为空");

        int count = paddingCompanyDOMapper.updateByPrimaryKeyWithBLOBs(paddingCompanyDO);
        Preconditions.checkArgument(count > 1, "编辑失败");
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Integer id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = paddingCompanyDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 1, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<PaddingCompanyVO> getById(Integer id) {
        Preconditions.checkNotNull(id, "id不能为空");

        PaddingCompanyDO paddingCompanyDO = paddingCompanyDOMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(paddingCompanyDO, "id有误，数据不存在");

        PaddingCompanyVO paddingCompanyVO = new PaddingCompanyVO();
        BeanUtils.copyProperties(paddingCompanyDO, paddingCompanyVO);

        return ResultBean.ofSuccess(paddingCompanyVO);
    }

    @Override
    public ResultBean<List<PaddingCompanyVO>> query(BaseAreaQuery query) {

        int count = paddingCompanyDOMapper.count(query);
        Preconditions.checkArgument(count > 0, "无符合条件的数据");

        List<PaddingCompanyDO> paddingCompanyDOS = paddingCompanyDOMapper.query(query);

        List<PaddingCompanyVO> paddingCompanyVOS = paddingCompanyDOS.parallelStream()
                .filter(Objects::nonNull)
                .map(e -> {
                    PaddingCompanyVO paddingCompanyVO = new PaddingCompanyVO();
                    BeanUtils.copyProperties(e, paddingCompanyVO);
                    return paddingCompanyVO;
                })
                .collect(Collectors.toList());

        return ResultBean.of(paddingCompanyVOS, true, BaseExceptionEnum.EC00000200, count, query.getPageIndex(), query.getPageSize());
    }
}
