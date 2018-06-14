package com.yunche.loan.domain.param;

import lombok.Data;

import java.util.List;

public class ICBCApiParam {

    @Data
    public static class ApplyCredit {
        //----- pub -----
        private String platno;
        private String cmpseq;
        private String zoneno;
        private String phybrno;
        private String orderno;
        private String assurerno;
        private String cmpdate;
        private String cmptime;
        //----- request ------
        private Customer customer;//客户信息
        private List<Picture> pictures;//客户照片资料

    }

    @Data
    public static class Picture {
        private String picid;
        private String picurl;
        private String picnote;
    }

    @Data
    public static class Customer {
        private String mastername;
        private String custname;
        private String idtype;
        private String idno;
        private String relation;
    }



}
