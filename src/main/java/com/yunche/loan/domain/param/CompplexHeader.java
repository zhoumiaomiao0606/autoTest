package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-13 10:51
 * @description:
 **/
@Data
public class CompplexHeader
{
    private ArrayList<String> pheader;

    private ArrayList<String> aheader;

    private int count;
}
