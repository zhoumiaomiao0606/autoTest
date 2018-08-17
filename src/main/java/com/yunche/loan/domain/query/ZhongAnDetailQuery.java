package com.yunche.loan.domain.query;

import com.google.common.collect.Lists;
import com.yunche.loan.domain.entity.ZhonganInfoDO;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ZhongAnDetailQuery {
    private List<ZhonganInfoDO> list = Lists.newArrayList();

}
