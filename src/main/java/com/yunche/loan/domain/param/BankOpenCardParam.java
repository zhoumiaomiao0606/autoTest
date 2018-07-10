package com.yunche.loan.domain.param;


import com.yunche.loan.config.feign.request.ICBCApiRequest;
import lombok.Data;

import java.util.Date;

@Data
public class BankOpenCardParam  extends ICBCApiRequest.ApplyBankOpenCard{
        private Long customerId;

        private String namePinyin;

        private Date checkInDate;

        private Date enrollmentDate;

        private String cardReceiveMode;

        private String cardSendAddrType;

        private String balanceChangeRemind;

        private String openEmail;

        private String email;

        private String occupation;

        private String issuingDepartment;

        private String masterCardTel;

        private String bellTel;

        private String balanceChangeTel;

        private String openCardStatus;

        private String repayCardNumber;

}
