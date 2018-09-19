package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/18
 */
@Data
public class RelaOrderCustomerParam {

    private Long principalCustId;

    private List<Rela> relaList;


    @Data
    public static class Rela {

        private Long relaCustomerId;

        private Byte relaCustType;

        private Byte relaCustRelation;

        private Byte relaGuaranteeType;
    }
}
