package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.domain.vo.InsuranceCompanyVO;
import com.yunche.loan.mapper.InsuranceCompanyDOMapper;
import com.yunche.loan.domain.entity.InsuranceCompanyDO;
import com.yunche.loan.domain.query.InsuranceCompanyQuery;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.InsuranceCompanyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.INVALID_STATUS;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Service
@Transactional
public class InsuranceCompanyServiceImpl implements InsuranceCompanyService {

    private static final Logger logger = LoggerFactory.getLogger(InsuranceCompanyServiceImpl.class);

    @Autowired
    private InsuranceCompanyDOMapper insuranceCompanyDOMapper;


    @Override
    public ResultBean<Long> create(InsuranceCompanyDO insuranceCompanyDO) {
        Preconditions.checkArgument(StringUtils.isNotBlank(insuranceCompanyDO.getName()), "名称不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(insuranceCompanyDO.getOfficePhone()), "办公室电话不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(insuranceCompanyDO.getFax()), "传真不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(insuranceCompanyDO.getBank()), "开户行不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(insuranceCompanyDO.getBankAccount()), "银行账号不能为空");
        Preconditions.checkNotNull(insuranceCompanyDO.getStatus(), "状态不能为空");
        Preconditions.checkArgument(VALID_STATUS.equals(insuranceCompanyDO.getStatus()) || INVALID_STATUS.equals(insuranceCompanyDO.getStatus()),
                "状态非法");

        insuranceCompanyDO.setStatus(VALID_STATUS);
        insuranceCompanyDO.setGmtCreate(new Date());
        insuranceCompanyDO.setGmtModify(new Date());
        int count = insuranceCompanyDOMapper.insertSelective(insuranceCompanyDO);
        Preconditions.checkArgument(count > 0, "创建失败");
        return ResultBean.ofSuccess(insuranceCompanyDO.getId(), "创建成功");
    }

    @Override
    public ResultBean<Void> update(InsuranceCompanyDO paddingCompanyDO) {
        Preconditions.checkNotNull(paddingCompanyDO.getId(), "id不能为空");

        paddingCompanyDO.setGmtModify(new Date());
        int count = insuranceCompanyDOMapper.updateByPrimaryKeySelective(paddingCompanyDO);
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

        InsuranceCompanyDO insuranceCompanyDO = insuranceCompanyDOMapper.selectByPrimaryKey(id, null);
        Preconditions.checkNotNull(insuranceCompanyDO, "id有误，数据不存在");

        InsuranceCompanyVO insuranceCompanyVO = new InsuranceCompanyVO();
        BeanUtils.copyProperties(insuranceCompanyDO, insuranceCompanyVO);

        return ResultBean.ofSuccess(insuranceCompanyVO);
    }

    @Override
    public ResultBean<List<InsuranceCompanyVO>> query(InsuranceCompanyQuery query) {
        int totalNum = insuranceCompanyDOMapper.count(query);
        if (totalNum < 1) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

        List<InsuranceCompanyDO> insuranceCompanyDOS = insuranceCompanyDOMapper.query(query);
        if (CollectionUtils.isEmpty(insuranceCompanyDOS)) {
            return ResultBean.ofSuccess(Collections.EMPTY_LIST);
        }

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
