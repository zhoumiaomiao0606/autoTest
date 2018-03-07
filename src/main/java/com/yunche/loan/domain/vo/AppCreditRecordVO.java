package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppCreditRecordVO {
    /**
     * 贷款基本信息
     */
    private LoanBaseInfoVO loanBaseInfo;
    /**
     * 主贷人
     */
    private CreditRecordVO.Customer principalLender;
    /**
     * 共贷人列表
     */
    private List<CreditRecordVO.Customer> commonLenderList = Collections.EMPTY_LIST;

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
