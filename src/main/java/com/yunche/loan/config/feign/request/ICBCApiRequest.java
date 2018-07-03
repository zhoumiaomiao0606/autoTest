package com.yunche.loan.config.feign.request;

import com.yunche.loan.config.feign.request.group.New;
import com.yunche.loan.config.feign.request.group.Second;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

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
        private ApplyCreditCustomer customer;//客户信息
        @Valid
        @NotNull
        private List<Picture> pictures;//客户照片资料
    }


    /**
     * 通用业务申请
     */
    @Data
    @Validated(ApplyDiviGeneral.class)
    public static class ApplyDiviGeneral extends Pub {
        //----- request ------
        @Valid
        @NotNull
        private ApplyDiviGeneralCustomer customer;//客户信息

        @Valid
        @NotNull
        private ApplyDiviGeneralBusi busi;

        @Valid
        @NotNull
        private List<Picture> pictures;//客户照片资料

    }

    @Data
    public static class ApplyDiviGeneralBusi {
        @Valid
        @NotNull
        private ApplyDiviGeneralCar car;

        @Valid
        @NotNull
        private ApplyDiviGeneralDivi divi;
    }

    /**
     * 专项卡开卡
     */
    @Data
    public static class ApplyBankOpenCard extends Pub{

        private ApplyBankOpenCardCustomer customer;

        private List<Picture> pictures;//客户照片资料

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
    //---------------------------------------car-----------------------------------------------------

    @Data
    public static class ApplyDiviGeneralCar{
        @NotBlank(groups = {New.class, Second.class})
        private String carType;
        @NotBlank(groups = {New.class, Second.class})
        private String Price;
        @NotBlank(groups = {New.class, Second.class})
        private String carNo1;
        @NotBlank(groups = {New.class, Second.class})
        private String carRegNo;
        @NotBlank(groups = {New.class, Second.class})
        private String shorp4s;
        @NotBlank(groups = {New.class, Second.class})
        private String carNo2;
        @NotBlank(groups = {Second.class})
        private String AssessPrice;
        @NotBlank(groups = {Second.class})
        private String AssessOrg;
        @NotBlank(groups = {Second.class})
        private String UsedYears;


    }

    @Data
    public static class  ApplyDiviGeneralDivi{
        @NotBlank(groups = {New.class, Second.class})
        private String PaidAmt;
        @NotBlank(groups = {New.class, Second.class})
        private String Amount;
        @NotBlank(groups = {New.class, Second.class})
        private String Term;
        @NotBlank(groups = {New.class, Second.class})
        private String Interest;
        @NotBlank(groups = {New.class, Second.class})
        private String FeeMode;
        @NotBlank(groups = {New.class, Second.class})
        private String IsPawn;
        @NotBlank(groups = {New.class, Second.class})
        private String PawnGoods;
        @NotBlank(groups = {New.class, Second.class})
        private String IsAssure;
        @NotBlank(groups = {New.class, Second.class})
        private String card;
        @NotBlank(groups = {New.class})
        private String tiexiFlag;
        @NotBlank(groups = {New.class})
        private String tiexiRate;
    }
    //---------------------------------------customer-----------------------------------------------------
    @Data
    public static class ApplyDiviGeneralCustomer{
        @NotBlank
        private String CustName;
        @NotBlank
        private String IdType;
        @NotBlank
        private String IdNo;
        @NotBlank
        private String Mobile;
        @NotBlank
        private String Address;
        @NotBlank
        private String Unit;
        @NotBlank
        private String Note;
    }

    @Data
    public static class ApplyCreditCustomer {
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
    public static class ApplyBankOpenCardCustomer {
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

    //pub

    @Data
    public static class Pub {
        //----- pub -----
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String platno;
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String cmpseq;
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String zoneno;
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String phybrno;
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String orderno;
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String assurerno;
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String cmpdate;
        @NotBlank(groups = {ApplyCredit.class,ApplyDiviGeneral.class})
        private String cmptime;
        @NotBlank(groups = ApplyDiviGeneral.class)
        private String busitype;
    }
}
