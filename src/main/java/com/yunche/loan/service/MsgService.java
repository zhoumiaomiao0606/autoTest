package com.yunche.loan.service;

import java.util.Map;

/**
 * @program: yunche-biz
 * @description: D
 * @author: Mr.WangGang
 * @create: 2018-08-30 11:50
 **/
public interface MsgService {
    Map creditDetail(Long orderId);

    Map msgDetail(Long msgId);

    String getPinYin(String chinese);
}
