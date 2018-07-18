package com.yunche.loan.domain.param;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
@Data
public class ICBCApiCallbackParam implements Serializable {

    @Data
    public static class ApplyCreditCallback {
        @NotNull
        private Pub pub;
        @NotNull
        private ApplyCreditReq req;
    }

    @Data
    public static class ApplyDiviGeneralCallback {
        @NotNull
        private Pub pub;
        @NotNull
        private ApplyDiviGeneralReq req;
    }


    @Data
    public static class MultimediaUploadCallback {
        @NotNull
        private Pub pub;
        @NotNull
        private MultimediaUploadReq req;
    }

    @Data
    public static class CreditCardApplyCallback {
        @NotNull
        private Pub pub;

        @NotNull
        private CreditCardApplyReq req;
    }


    @Data
    public static class Response {
        @NotNull
        private ResponsePub pub;
    }

    @Data
    public static class ResponsePub{
        private String retcode;

        private String retmsg;
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
    public static class ApplyCreditReq {
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

    @Data
    public static class ApplyDiviGeneralReq {
        private String backnote;
    }
    @Data
    public static class MultimediaUploadReq {
        private String backnote;
    }


    @Data
    public static class CreditCardApplyReq {
        private String backnote;
    }
}
