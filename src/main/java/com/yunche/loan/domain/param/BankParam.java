package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.domain.entity.BankRelaQuestionDO;
import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/5/15
 */
@Data
public class BankParam {
    /**
     * 银行
     */
    private BankDO bank;
    /**
     * 银行面签问卷列表
     */
    private List<BankRelaQuestionDO> bankQuestionList = Collections.EMPTY_LIST;
    /**
     * 机器面签问卷列表
     */
    private List<BankRelaQuestionDO> machineQuestionList = Collections.EMPTY_LIST;
}
