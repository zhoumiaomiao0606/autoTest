package com.yunche.loan.estage;

import com.alibaba.fastjson.JSONObject;

/**
 * Description:
 * author: yu.hb
 * Date: 2019-03-07
 */
public interface EstageProcessor {

    public JSONObject processInternal(Long orderId);
}
