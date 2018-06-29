package com.yunche.loan.domain.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
@Data
public class ICBCApiCallbackParam implements Serializable {

    @Data
    public static class Callback {
        @NotNull
        private Pub pub;
        @NotNull
        private Req req;
    }

    @Data
    public static class Pub {
        //----- pub -----
        private String platno;
        private String cmpseq;
        private String zoneno;
        private String phybrno;
        private String orderno;
        private String assurerno;
        private String cmpdate;
        private String cmptime;
    }

    @Data
    public static class Req {
        //----- pub -----
        private String custname;
        private String idno;
        private String relation;
        private String result;
        private String loanCrdt;
        private String cardCrdt;
        private String leftNum;
        private String leftAmount;
        private String note;
    }


}
