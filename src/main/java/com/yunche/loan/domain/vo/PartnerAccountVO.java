package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/8
 */
@Data
public class PartnerAccountVO {

    private Long partnerId;

    private String partnerName;

    private Byte payMonth;

    private List<AccountInfo> accountInfoList = Collections.EMPTY_LIST;

    @Data
    public static class AccountInfo {

        private String openBank;

        private String bankCode;

        private String accountName;

        private String bankAccount;
    }
}
