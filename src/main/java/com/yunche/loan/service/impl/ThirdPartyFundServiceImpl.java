package com.yunche.loan.service.impl;

import cn.jiguang.common.utils.Preconditions;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ThirdPartyFundDO;
import com.yunche.loan.mapper.ThirdPartyFundDOMapper;
import com.yunche.loan.service.ThirdPartyFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-05 10:00
 * @description: 第三方资金服务
 **/
@Service
public class ThirdPartyFundServiceImpl implements ThirdPartyFundService
{
    @Autowired
    private ThirdPartyFundDOMapper thirdPartyFundDOMapper;
    @Override
    public ThirdPartyFundDO detail(Long third_party_fund_id)
    {
        Preconditions.checkNotNull(third_party_fund_id, "三方资金id不能为空");
        return thirdPartyFundDOMapper.selectByPrimaryKey(third_party_fund_id);
    }

    @Override
    public ResultBean<Void> update(ThirdPartyFundDO param)
    {
       if(param.getId() !=null && "".equals(param.getId()))
       {
           if (thirdPartyFundDOMapper.selectByPrimaryKey(param.getId())!=null)
           {
               thirdPartyFundDOMapper.updateByPrimaryKey(param);
           }else{
               throw new BizException("传入三方资金id有误");
           }

       }else{
           thirdPartyFundDOMapper.insert(param);

       }
        return ResultBean.ofSuccess(null, "保存成功");

    }

    @Override
    public List<ThirdPartyFundDO> list()
    {
        List<ThirdPartyFundDO> list = thirdPartyFundDOMapper.list();
        return list;
    }
}
