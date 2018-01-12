package com.yunche.loan.service.impl;

import com.yunche.loan.bo.BaseAreaBO;
import com.yunche.loan.mapper.BaseAeraDOMapper;
import com.yunche.loan.obj.BaseAeraDO;
import com.yunche.loan.result.ResultBOBean;
import com.yunche.loan.service.BaseAreaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Service
public class BaseAreaServiceImpl implements BaseAreaService {

    @Autowired
    private BaseAeraDOMapper baseAeraDOMapper;

    @Override
    public ResultBOBean<BaseAreaBO> getById(Integer id) {

        BaseAeraDO baseAeraDO = baseAeraDOMapper.selectByPrimaryKey(id);

        BaseAreaBO baseAreaBO = new BaseAreaBO();
        BeanUtils.copyProperties(baseAeraDO, baseAreaBO);

        return ResultBOBean.ofSuccess(baseAreaBO);
    }


}
