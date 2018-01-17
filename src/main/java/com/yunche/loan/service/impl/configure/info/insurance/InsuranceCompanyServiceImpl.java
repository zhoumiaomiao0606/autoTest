package com.yunche.loan.service.impl.configure.info.insurance;

import com.google.common.base.Preconditions;
import com.yunche.loan.common.BaseExceptionEnum;
import com.yunche.loan.vo.configure.info.insurance.InsuranceCompanyVO;
import com.yunche.loan.mapper.configure.info.insurance.InsuranceCompanyDOMapper;
import com.yunche.loan.obj.configure.info.insurance.InsuranceCompanyDO;
import com.yunche.loan.query.configure.info.insurance.InsuranceCompanyQuery;
import com.yunche.loan.result.ResultBean;
import com.yunche.loan.service.configure.info.insurance.InsuranceCompanyService;
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
        Preconditions.checkArgument(count > 1, "创建失败");
        return ResultBean.ofSuccess(null, "创建成功");
    }

    @Override
    public ResultBean<Void> update(InsuranceCompanyDO paddingCompanyDO) {
        Preconditions.checkNotNull(paddingCompanyDO.getId(), "id不能为空");

        int count = insuranceCompanyDOMapper.updateByPrimaryKeyWithBLOBs(paddingCompanyDO);
        Preconditions.checkArgument(count > 1, "编辑失败");
        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<Void> delete(Integer id) {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = insuranceCompanyDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 1, "删除失败");
        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean<InsuranceCompanyVO> getById(Integer id) {
        Preconditions.checkNotNull(id, "id不能为空");

        InsuranceCompanyDO insuranceCompanyDO = insuranceCompanyDOMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(insuranceCompanyDO, "id有误，数据不存在");

        InsuranceCompanyVO insuranceCompanyVO = new InsuranceCompanyVO();
        BeanUtils.copyProperties(insuranceCompanyDO, insuranceCompanyVO);

        return ResultBean.ofSuccess(insuranceCompanyVO);
    }

    @Override
    public ResultBean<List<InsuranceCompanyVO>> query(InsuranceCompanyQuery query) {

        int count = insuranceCompanyDOMapper.count(query);
        Preconditions.checkArgument(count > 0, "无符合条件的数据");

        List<InsuranceCompanyDO> insuranceCompanyDOS = insuranceCompanyDOMapper.query(query);

        List<InsuranceCompanyVO> insuranceCompanyVOS = insuranceCompanyDOS.parallelStream()
                .filter(Objects::nonNull)
                .map(e -> {
                    InsuranceCompanyVO insuranceCompanyVO = new InsuranceCompanyVO();
                    BeanUtils.copyProperties(e, insuranceCompanyVO);
                    return insuranceCompanyVO;
                })
                .collect(Collectors.toList());

        return ResultBean.of(insuranceCompanyVOS, true, BaseExceptionEnum.EC00000200, count, query.getPageIndex(), query.getPageSize());
    }
}
