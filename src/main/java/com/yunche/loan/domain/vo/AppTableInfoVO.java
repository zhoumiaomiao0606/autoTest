package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class AppTableInfoVO {
    //表头
    private List<String> tableHead = Collections.EMPTY_LIST;
    //筛选项
    private List<String> tableScreen = Collections.EMPTY_LIST;
}
