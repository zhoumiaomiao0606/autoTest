package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Data
public class CustomerParam extends LoanCustomerDO {

    private List<FileVO> files = Collections.EMPTY_LIST;

    private Byte bankCreditStatus;

    private String bankCreditDetail;

    private Byte socialCreditStatus;

    private String socialCreditDetail;
}
