package com.yunche.loan.service;

import com.yunche.loan.domain.param.MaterialUpdateParam;

import java.util.Map;

public interface MaterialService {

    public Map detail(String order_id);

    public void update(MaterialUpdateParam param);
}
