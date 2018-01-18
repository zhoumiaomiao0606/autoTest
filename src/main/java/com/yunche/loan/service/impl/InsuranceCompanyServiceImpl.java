package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.common.BaseExceptionEnum;
import com.yunche.loan.domain.valueObj.InsuranceCompanyVO;
import com.yunche.loan.dao.mapper.InsuranceCompanyDOMapper;
import com.yunche.loan.domain.dataObj.InsuranceCompanyDO;
import com.yunche.loan.domain.QueryObj.InsuranceCompanyQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.InsuranceCompanyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Service
public class InsuranceCompanyServiceImpl implements InsuranceCompanyService {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceCompanyServiceImpl.class);

    @Autowired
    private InsuranceCompanyDOMapper insuranceCompanyDOMapper;


    @Override
    public ResultBean<Void> create(InsuranceCompanyDO paddingCompanyDO) {
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getName()), "名称不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getOfficePhone()), "办公室电话不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getFax()), "传真不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getBank()), "开户行不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(paddingCompanyDO.getBankAccount()), "银行账号不能为空");

        int count = insuranceCompanyDOMapper.insertSelective(paddingCompanyDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(InsuranceCompanyDO paddingCompanyDO) {
        Preconditions.checkNotNull(paddingCompanyDO.getId(), "id不能为空");

        int count = insuranceCompanyDOMapper.updateByPrimaryKeyWithBLOBs(paddingCompanyDO);
        Preconditions.checkArgument(count > 0, "编辑失败");
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = insuranceCompanyDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<InsuranceCompanyVO> getById(Long id) {
        Preconditions.checkNotNull(id, "id不能为空");

        InsuranceCompanyDO insuranceCompanyDO = insuranceCompanyDOMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(insuranceCompanyDO, "id有误，数据不存在");

        InsuranceCompanyVO insuranceCompanyVO = new InsuranceCompanyVO();
        BeanUtils.copyProperties(insuranceCompanyDO, insuranceCompanyVO);

        return ResultBean.ofSuccess(insuranceCompanyVO);
    }

    @Override
    public ResultBean<List<InsuranceCompanyVO>> query(InsuranceCompanyQuery query) {
        int totalNum = insuranceCompanyDOMapper.count(query);
        Preconditions.checkArgument(totalNum > 0, "无符合条件的数据");

        List<InsuranceCompanyDO> insuranceCompanyDOS = insuranceCompanyDOMapper.query(query);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(insuranceCompanyDOS), "无符合条件的数据");

        List<InsuranceCompanyVO> insuranceCompanyVOS = insuranceCompanyDOS.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    InsuranceCompanyVO insuranceCompanyVO = new InsuranceCompanyVO();
                    BeanUtils.copyProperties(e, insuranceCompanyVO);
                    return insuranceCompanyVO;
                })
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(insuranceCompanyVOS, totalNum, query.getPageIndex(), query.getPageSize());
    }
}
