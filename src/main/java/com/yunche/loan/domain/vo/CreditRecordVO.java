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
    private CustomerCreditRecord principalLender;
    /**
     * 共贷人列表
     */
    private List<CustomerCreditRecord> commonLenderList = Collections.EMPTY_LIST;
    /**
     * 担保人列表
     */
    private List<CustomerCreditRecord> guarantorList = Collections.EMPTY_LIST;
    /**
     * 紧急联系人列表
     */
    private List<CustomerCreditRecord> emergencyContactList = Collections.EMPTY_LIST;

    @Data
    public static class CustomerCreditRecord {
        /**
         * 征信记录ID
         */
        private Long creditId;
        /**
         * 征信记录结果
         */
        private Byte creditResult;
        /**
         * 征信记录备注
         */
        private String creditInfo;

        /**
         * 征信所属客户ID
         */
        private Long customerId;
        /**
         * 征信所属客户名称
         */
        private String customerName;

        private String mobile;

        private String idCard;

        private Byte custType;

        private Byte custRelation;

        private Byte guaranteeType;

        private List<FileVO> files;

        private List<Long> relevanceOrderlist;
    }
}
