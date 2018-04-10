package com.yunche.loan.domain.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/4/4
 */
@Data
public class LoanRepeatVO {

    /**
     * 是否为重复贷款
     */
    private Boolean repeat;
    /**
     * 历史订单号
     */
    private List<String> orderIdList = Collections.EMPTY_LIST;

    public Boolean getRepeat() {
        if (CollectionUtils.isEmpty(orderIdList)) {
            return false;
        }
        return true;
    }
}