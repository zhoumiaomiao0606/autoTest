package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class EvaluateVO
{
    private String ncmsrp;//厂商指导价

    private String trimId;//车型 id

    private EvaluatePrice b2CPrices;//商家销售价 B2C 价格 包含 abc 三个车况

    private EvaluatePrice c2BPrices;//收购价 C2B 价格 包含 abc 三个车况


}
