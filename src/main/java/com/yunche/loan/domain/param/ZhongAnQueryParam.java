package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ZhongAnQueryParam {

    private List<ZhongAnCusParam> customers = Lists.newArrayList();

}
