package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @program: yunche-biz
 * @description: d
 * @author: Mr.WangGang
 * @create: 2018-08-28 15:03
 **/
@Data
public class FlowOperationMsgParam {

    @NotNull
    private Integer pageIndex;
    @NotNull
    private Integer pageSize;

    Long readStatus;
    /**
     * 1:征信反馈;  2:风控结果;  3:业务(其他)通知;  4：众安;
     */
    Long multipartType;
    Long maxGroupLevel;
    private List<Long> bizAreaIdList = Lists.newArrayList();//区域ID列表
    private List<String> bankList = Lists.newArrayList();//银行ID列表
    Set<String> juniorIds = Sets.newHashSet();
}
