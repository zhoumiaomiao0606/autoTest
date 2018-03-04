package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 征信录入详情页
 *
 * @author liuzhe
 * @date 2018/3/3
 */
@Data
public class CreditRecordVO {
    /**
     * 贷款基本信息
     */
    private LoanBaseInfoVO loanBaseInfo;
    /**
     * 主贷人
     */
    private Customer principalLender;
    /**
     * 共贷人列表
     */
    private List<Customer> commonLenderList = Collections.EMPTY_LIST;

    @Data
    public static class Customer {

        private Long id;

        private String name;

        private String mobile;

        private String idCard;

        private Byte custType;

        private Byte custRelation;

        private Byte creditStatus;

        private String creditDetail;
    }
}
