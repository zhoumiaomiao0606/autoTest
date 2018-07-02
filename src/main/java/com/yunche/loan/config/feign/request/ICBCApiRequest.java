package com.yunche.loan.config.feign.request;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ICBCApiRequest {

    /**
     * 征信申请
     */
    @Data
    public static class ApplyCredit extends Pub {
        //----- request ------
        @Valid
        @NotNull
        private Customer customer;//客户信息
        @Valid
        @NotNull
        private List<Picture> pictures;//客户照片资料
    }

    /**
     * 专项卡开卡
     */
    @Data
    public static class ApplyBankOpenCard extends Pub{

        private List<Picture> pictures;//客户照片资料

        private String  feeamount;
        private String  loanamount;
        private String  term;
        private String  loanratio;
        private String  carprice;
        private String  feeratio;

        private String  engname;
        private String  rtcophon2;
        private String  birthdate;
        private String  cprovince;
        private String  drawaddr;
        private String  relaphone1;
        private String  statdate;
        private String  unitname;
        private String  accgetm;
        private String  mvblno;
        private String  caddress;
        private String  authref;
        private String  haddress;
        private String  machgf;
        private String  machgmobile;
        private String  joindate;
        private String  drawmode;
        private String  ccounty;
        private String  chnsname;
        private String  mrtlstat;
        private String  reltship1;
        private String  reltship2;
        private String  modelcode;
        private String  indate;
        private String  hcity;
        private String  cadrchoic;
        private String  reltmobl2;
        private String  hphoneno;
        private String  reltmobl1;
        private String  homezip;
        private String  mamobile;
        private String  custsort;
        private String  cophoneno;
        private String  corpzip;
        private String  custcode;
        private String  mblchoic;
        private String  cophozono;
        private String  cophonext;
        private String  sex;
        private String  hadrchoic;
        private String  hprovince;
        private String  ccity;
        private String  occptn;
        private String  reltname2;
        private String  smsphone;
        private String  reltname1;
        private String  hcounty;
        private String  reltsex2;
        private String  reltsex1;
        private String  emladdrf;
    }


    /**
     * 文件清单生成通知接口
     */
    @Data
    public static class FileNotice{
        private Pub pub;
        private FileNoticeReq req;

    }

    @Data
    public static class ReturnMsg{
        ReturnPub pub;

        @Data
        public static class ReturnPub{
            private String retcode;
            private String retmsg;
        }
    }


    @Data
    public static class FileNoticeReq {
        private String  filetype;
        private String  filesrc;
        private String  datadt;
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



    @Data
    public static class Pub {
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
    }
}
