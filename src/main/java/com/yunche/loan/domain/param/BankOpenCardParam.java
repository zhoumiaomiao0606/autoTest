package com.yunche.loan.domain.param;


import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class BankOpenCardParam  extends ICBCApiRequest.ApplyBankOpenCard{
        private  Long orderId;
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

        private Byte education;

        private Byte companyNature;

        private String billSendType;

        private List<FileVO> files = Collections.EMPTY_LIST;

}
