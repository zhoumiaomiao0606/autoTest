package com.yunche.loan.service.configure.info.address;

import com.yunche.loan.vo.configure.info.address.BaseAreaVO;
import com.yunche.loan.obj.configure.info.address.BaseAreaDO;
import com.yunche.loan.query.configure.info.address.BaseAreaQuery;
import com.yunche.loan.result.ResultBean;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
public interface BaseAreaService {

    ResultBean<BaseAreaVO> getById(Long areaId);

    ResultBean<Void> create(BaseAreaDO baseAreaDO);

    ResultBean<Void> update(BaseAreaDO baseAreaBO);

    ResultBean<Void> delete(Long areaId);

    ResultBean<BaseAreaVO> query(BaseAreaQuery queryJObj);
}
