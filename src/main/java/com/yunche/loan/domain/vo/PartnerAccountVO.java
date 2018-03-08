package com.yunche.loan.domain.vo;

import lombok.Data;

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

    private List<AccountInfo> accountInfoList;

    @Data
    public static class AccountInfo {

        private String openBank;

        private String accountName;

        private String bankAccount;
    }
}
