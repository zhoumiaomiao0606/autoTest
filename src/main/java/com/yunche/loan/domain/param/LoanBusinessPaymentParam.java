package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class LoanBusinessPaymentParam {

    private Long orderId;

    private Date remit_application_date;

    private String remit_beneficiary_bank;

    private String remit_bankCode;

    private String remit_beneficiary_account;

    private String remit_beneficiary_account_number;

    private Byte remit_is_sendback;

    private String remark;

    private List<FileVO> files = Collections.EMPTY_LIST;
}
