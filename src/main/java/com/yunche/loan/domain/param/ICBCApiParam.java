package com.yunche.loan.domain.param;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ICBCApiParam {

    @Data
    public static class ApplyCredit {
        //----- pub -----
        @NotBlank
        private String platno;
        @NotBlank
        private String cmpseq;
        @NotBlank
        private String zoneno;
        @NotBlank
        private String phybrno;
        @NotBlank
        private String orderno;
        @NotBlank
        private String assurerno;
        @NotBlank
        private String cmpdate;
        @NotBlank
        private String cmptime;
        //----- request ------
        @Valid
        @NotNull
        private Customer customer;//客户信息
        @Valid
        @NotNull
        private List<Picture> pictures;//客户照片资料
    }

    @Data
    public static class Picture {
        @NotBlank
        private String picid;
        @NotBlank
        private String picname;
        @NotBlank
        private String picnote;
    }

    @Data
    public static class Customer {
        @NotBlank
        private String mastername;
        @NotBlank
        private String custname;
        @NotBlank
        private String idtype;
        @NotBlank
        private String idno;
        @NotBlank
        private String relation;
    }



}
