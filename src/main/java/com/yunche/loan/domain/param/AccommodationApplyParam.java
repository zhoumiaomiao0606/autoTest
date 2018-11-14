package com.yunche.loan.domain.param;

import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class AccommodationApplyParam {

    private List<IDPair> idPairs = Lists.newArrayList();//业务单号列表

    private IDPair idPair;

    private Date lendDate;//借款日期

    private BigDecimal lendAmount;//借款金额

    private Byte repayType;//还款类型

    private Date repayDate;//还款日期

    private String repayRemark;//还款备注

    private BigDecimal interest;//利息

    private BigDecimal poundage;//手续费

    private Date repayInterestDate;//还息日期

    private String repayRegisterRemark;//登记备注

    private Byte lendStatus;//出借状态 K_CJZT

    private String bankCard;

    private String tel;

    private List<Long> bridgeIdList = Lists.newArrayList();
    @Data
    public static class IDPair{
        Long orderId;
        Long bridgeProcessId;
    }
}
