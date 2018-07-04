package com.yunche.loan.config.feign.request;

import com.yunche.loan.config.feign.request.group.ApplyCreditValidated;
import com.yunche.loan.config.feign.request.group.ApplyDiviGeneralValidated;
import com.yunche.loan.config.feign.request.group.NewValidated;
import com.yunche.loan.config.feign.request.group.SecondValidated;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ICBCApiRequest {

    /**
     * 征信申请
     */
    @Data
    public static class ApplyCredit extends Pub {
        //----- request ------
        @NotNull
        @Valid
        private ApplyCreditCustomer customer;//客户信息
        @NotNull
        @Valid
        private List<Picture> pictures;//客户照片资料
    }


    /**
     * 通用业务申请
     */
    @Data
    public static class ApplyDiviGeneral extends Pub {
        //----- request ------
        @NotNull
        @Valid
        private ApplyDiviGeneralCustomer customer;//客户信息

        @NotNull
        @Valid
        private ApplyDiviGeneralBusi busi;

        @NotNull
        @Valid
        private List<Picture> pictures;//客户照片资料

    }

    @Data
    public static class ApplyDiviGeneralBusi {
        @NotNull
        @Valid
        private ApplyDiviGeneralCar car;

        @NotNull
        @Valid
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
        @NotEmpty
        private String picid;
        @NotEmpty
        private String picname;
        @NotEmpty
        private String picnote;
    }
    //---------------------------------------car-----------------------------------------------------

    @Data
    public static class ApplyDiviGeneralCar{
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String carType;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String Price;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String carNo1;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String carRegNo;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String shorp4s;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String carNo2;
        @NotEmpty(groups = {SecondValidated.class})
        private String AssessPrice;
        @NotEmpty(groups = {SecondValidated.class})
        private String AssessOrg;
        @NotEmpty(groups = {SecondValidated.class})
        private String UsedYears;


    }

    @Data
    public static class  ApplyDiviGeneralDivi{
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String PaidAmt;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String Amount;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String Term;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String Interest;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String FeeMode;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String IsPawn;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String PawnGoods;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String IsAssure;
        @NotEmpty(groups = {NewValidated.class, SecondValidated.class})
        private String card;
        @NotEmpty(groups = {NewValidated.class})
        private String tiexiFlag;
        @NotEmpty(groups = {NewValidated.class})
        private String tiexiRate;
    }
    //---------------------------------------customer-----------------------------------------------------
    @Data
    public static class ApplyDiviGeneralCustomer{
        @NotEmpty
        private String CustName;
        @NotEmpty
        private String IdType;
        @NotEmpty
        private String IdNo;
        @NotEmpty
        private String Mobile;
        @NotEmpty
        private String Address;
        @NotEmpty
        private String Unit;
        @NotEmpty
        private String Note;
    }

    @Data
    public static class ApplyCreditCustomer {
        @NotEmpty
        private String mastername;
        @NotEmpty
        private String custname;
        @NotEmpty
        private String idtype;
        @NotEmpty
        private String idno;
        @NotEmpty
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
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String platno;
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String cmpseq;
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String zoneno;
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String phybrno;
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String orderno;
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String assurerno;
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String cmpdate;
        @NotEmpty(groups = {ApplyCreditValidated.class, ApplyDiviGeneralValidated.class})
        private String cmptime;
        @NotEmpty(groups = ApplyDiviGeneralValidated.class)
        private String busitype;
    }
}
