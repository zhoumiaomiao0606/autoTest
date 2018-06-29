package com.yunche.loan.domain.param;


import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
@Data
public class BankOpenCardParam {
        private Long customerId;
        //----- pub -----
        private String platno;//平台编号
        private String cmpseq;//合作机构交易流水号(Yyyymmddhh24miss+6)
        private String zoneno;//业务发生地
        private String phybrno;//业务受理网点
        private String orderno;//合作机构订单号
        private String assurerno;//担保单位编号
        private String cmpdate;//合作机构日期
        private String cmptime;//合作机构时间

        private List<Picture> pictures= Lists.newArrayList();


        @Data
        public static class Picture {
            private String picid;
            private String picname;
            private String picnote;
        }


        private String  feeamount;//手续费
        private String  loanamount;//
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
