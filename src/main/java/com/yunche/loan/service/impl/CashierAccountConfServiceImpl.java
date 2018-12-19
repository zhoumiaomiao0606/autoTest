package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.CashierAccountConfDO;
import com.yunche.loan.domain.param.CashierAccountConfParam;
import com.yunche.loan.domain.param.QueryCashierAccountConfParam;
import com.yunche.loan.domain.vo.CashierAccountConfVO;
import com.yunche.loan.domain.vo.CashierEmployName;
import com.yunche.loan.mapper.CashierAccountConfDOMapper;
import com.yunche.loan.service.CashierAccountConfService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
public class CashierAccountConfServiceImpl implements CashierAccountConfService
{
    private CashierAccountConfDOMapper cashierAccountConfDOMapper;

    @Override
    public ResultBean<Long> create(CashierAccountConfParam cashierAccountConfParam)
    {
        return null;
    }

    @Override
    public ResultBean<Void> update(CashierAccountConfParam cashierAccountConfParam)
    {
        Preconditions.checkNotNull(cashierAccountConfParam.getEmployeeId(), "工号id不能为空");

        //先清空绑定
        cashierAccountConfDOMapper.deleteByEmployeeId(cashierAccountConfParam.getEmployeeId());

        if (!CollectionUtils.isEmpty(cashierAccountConfParam.getList()))
        {
            //重新绑定
            cashierAccountConfParam.getList()
                    .stream()
                    .forEach(e ->
                    {
                        if (e.getId()!=null)
                        {
                            cashierAccountConfDOMapper.updateByPrimaryKey(e);
                        }else
                            {
                                e.setEmployeeId(cashierAccountConfParam.getEmployeeId());
                                e.setCreateUser(SessionUtils.getLoginUser().getName());
                                cashierAccountConfDOMapper.insertSelective(e);
                            }
                    });


        }
        return ResultBean.ofSuccess(null,"更新成功！！");
    }

    @Override
    public ResultBean<Void> delete(Long id)
    {
        Preconditions.checkNotNull(id, "id不能为空");

        int count = cashierAccountConfDOMapper.deleteByPrimaryKey(id);
        Preconditions.checkArgument(count > 0, "删除失败");

        return ResultBean.ofSuccess(null, "删除成功");
    }

    @Override
    public ResultBean listAll(QueryCashierAccountConfParam queryCashierAccountConfParam)
    {
        PageHelper.startPage(queryCashierAccountConfParam.getPageIndex(), queryCashierAccountConfParam.getPageSize(), true);
        List<CashierAccountConfVO> list = cashierAccountConfDOMapper.listAll(queryCashierAccountConfParam);
        PageInfo<CashierAccountConfVO> pageInfo = new PageInfo(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean listAllEmployName()
    {
        List<CashierEmployName> list = cashierAccountConfDOMapper.listAllEmployName();
        return ResultBean.ofSuccess(list);
    }

    @Override
    public ResultBean listAllCreateUserName()
    {
        List<String> list = cashierAccountConfDOMapper.listAllCreateUserName();
        return ResultBean.ofSuccess(list);
    }

    @Override
    public ResultBean listAllCashierAccountConfByEmployeeId(Long employeeId)
    {
        List<CashierAccountConfDO> list = cashierAccountConfDOMapper.listAllCashierAccountConfByEmployeeId(employeeId);
        return ResultBean.ofSuccess(list);
    }
}
