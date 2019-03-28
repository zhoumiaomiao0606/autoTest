package com.yunche.loan.config.feign.request;

import com.google.common.collect.Lists;
import com.yunche.loan.config.feign.request.group.ApplyDiviGeneralValidated;
import com.yunche.loan.config.feign.request.group.MultimediaUploadValidated;
import com.yunche.loan.config.feign.request.group.NewValidated;
import com.yunche.loan.config.feign.request.group.SecondValidated;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

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
        private ApplyDiviGeneralInfo info;

        @NotNull
        @Valid
        private List<Picture> pictures;//客户照片资料

    }

    @Data
    public static class ApplyDiviGeneralInfo {
        @NotEmpty
        @Valid
        private String resubmit;

        private String note;
    }

    @Data
    public static class MultimediaUpload extends Pub {

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
    public static class ApplyBankOpenCard extends Pub {

        @NotNull
        @Valid
        private ApplyBankOpenCardCustomer customer;

        @NotNull
        @Valid
        private List<Picture> pictures = Lists.newArrayList(); // 客户照片资料
    }


    /**
     * 文件清单生成通知接口
     */
    @Data
    public static class FileNotice {
        private Pub pub;
        private FileNoticeReq req;

    }

    /**
     * 查询申请进度
     */
    @Data
    public static class Applystatus extends Pub {

    }

    /**
     * 查询专项卡开卡进度
     */
    public static class Applycreditstatus extends Pub {

    }

    /**
     *
     */
    public static class ApplyMediaStatus extends Pub {

    }


    /**
     * 二手车评估预审
     */
    @Data
    public static class Applyevaluate {
        @NotEmpty(message = "平台编号不能为空")
        private String platno;
        @NotEmpty(message = "合作机构交易流水号不能为空")
        private String cmpseq;
        @NotEmpty(message = "合作机构订单号不能为空")
        private String orderno;
        @NotEmpty(message = "合作机构日期不能为空")
        private String cmpdate;
        @NotEmpty(message = "合作机构时间不能为空")
        private String cmptime;
        private String fileNum;
        private String customerId;

        @NotEmpty(message = "证件类型不能为空")
        private String idtype;
        @NotEmpty(message = "证件编号不能为空")
        private String idno;
        @NotEmpty(message = "车辆型号不能为空")
        private String carType;
        @NotEmpty(message = "拟分期金额（元）不能为空")
        private String Price;
        @NotEmpty(message = "车架号不能为空")
        private String carNo1;
        @NotEmpty(message = "所在城市不能为空")
        private String carZone;
        @NotEmpty(message = "行驶里程不能为空")
        private String carMile;
        @NotEmpty(message = "首次上牌日期不能为空")
        private String carDate;
        @NotEmpty(message = "线下评估价格（元）不能为空")
        private String AssessPrice;
        @NotEmpty(message = "评估机构不能为空")
        private String EvaluateOrg;
        private String decorateLevel;

    }

    @Data
    public static class ReturnMsg {
        ReturnPub pub;

        @Data
        public static class ReturnPub {
            private String retcode;
            private String retmsg;
        }
    }


    @Data
    public static class FileNoticeReq {
        private String filetype;
        private String filesrc;
        private String datadt;
    }

    @Data
    public static class Picture {
        @NotEmpty
        private String picid;
        @NotEmpty
        private String picname;

        private String picnote;


        private List picKeyList = Lists.newArrayList();
    }
    //---------------------------------------car-----------------------------------------------------

    @Data
    public static class ApplyDiviGeneralCar {
        @NotEmpty
        private String carType;
        @NotEmpty
        private String Price;
        @NotEmpty
        private String carNo1;
        @NotEmpty(groups = {SecondValidated.class})
        private String carRegNo;

        private String shorp4s;
        @NotEmpty(groups = {SecondValidated.class})
        private String carNo2;
        @NotEmpty(groups = {SecondValidated.class})
        private String AssessPrice;
        @NotEmpty(groups = {SecondValidated.class})
        private String AssessOrg;
        @NotEmpty(groups = {SecondValidated.class})
        private String UsedYears;


    }

    @Data
    public static class ApplyDiviGeneralDivi {
        @NotEmpty
        private String PaidAmt;
        @NotEmpty
        private String Amount;
        @NotEmpty
        private String Term;
        @NotEmpty
        private String Interest;
        @NotEmpty
        private String FeeMode;
        @NotEmpty
        private String IsPawn;
        @NotEmpty
        private String PawnGoods;
        @NotEmpty
        private String IsAssure;

        private String card;
        @NotEmpty(groups = {NewValidated.class})
        private String tiexiFlag;
        @NotEmpty(groups = {NewValidated.class})
        private String tiexiRate;
    }

    //---------------------------------------customer-----------------------------------------------------
    @Data
    public static class ApplyDiviGeneralCustomer {
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
        @NotEmpty(message = "手续费不能为空")
        private String feeamount;
        @NotEmpty(message = "申请分期金额不能为空")
        private String loanamount;
        @NotEmpty(message = "贷款期限不能为空")
        private String term;
        @NotEmpty(message = "贷款成数不能为空")
        private String loanratio;
        @NotEmpty(message = "车辆价格不能为空")
        private String carprice;
        @NotEmpty(message = "手续费率不能为空")
        private String feeratio;
        @NotEmpty(message = "姓名拼音不能为空")
        private String engname;
        @NotEmpty(message = "联系人二联系电话号不能为空")
        private String rtcophon2;
        @NotEmpty(message = "出生日期不能为空")
        private String birthdate;
        @NotEmpty(message = "单位地址省份不能为空")
        private String cprovince;
        @NotEmpty(message = "卡片寄送地址类型不能为空")
        private String drawaddr;
        @NotEmpty(message = "联系人一联系电话号不能为空")
        private String relaphone1;
        @NotEmpty(message = "证件有效期不能为空")
        private String statdate;
        @NotEmpty(message = "工作单位不能为空")
        private String unitname;
        @NotEmpty(message = "对帐单寄送方式不能为空")
        private String accgetm;
        @NotEmpty(message = "手机号码不能为空")
        private String mvblno;
        @NotEmpty(message = "")
        private String caddress;
        @NotEmpty(message = "工作单位地址不能为空")
        private String authref;
        @NotEmpty(message = "住宅地址不能为空")
        private String haddress;
        @NotEmpty(message = "主卡开通余额变动提醒不能为空")
        private String machgf;
        @NotEmpty(message = "主卡余额提醒发送手机号码不能为空")
        private String machgmobile;
        @NotEmpty(message = "进入单位时间不能为空")
        private String joindate;
        @NotEmpty(message = "卡片领取方式不能为空")
        private String drawmode;
        @NotEmpty(message = "单位地址县不能为空")
        private String ccounty;
        @NotEmpty(message = "姓名不能为空")
        private String chnsname;
        @NotEmpty(message = "婚姻状况不能为空")
        private String mrtlstat;
        @NotEmpty(message = "联系人一与主卡申请关系不能为空")
        private String reltship1;
        @NotEmpty(message = "联系人二与主卡申请关系不能为空")
        private String reltship2;
        @NotEmpty(message = "单位性质不能为空")
        private String modelcode;
        @NotEmpty(message = "何时入住现址不能为空")
        private String indate;
        @NotEmpty(message = "住宅地址市不能为空")
        private String hcity;
        @NotEmpty(message = "单位地址选择不能为空")
        private String cadrchoic;
        @NotEmpty(message = "联系人二手机不能为空")
        private String reltmobl2;
        @NotEmpty(message = "住宅电话号码不能为空")
        private String hphoneno;
        @NotEmpty(message = "联系人一手机不能为空")
        private String reltmobl1;
        @NotEmpty(message = "住宅邮编不能为空")
        private String homezip;
        @NotEmpty(message = "主卡发送移动电话不能为空")
        private String mamobile;
        @NotEmpty(message = "证件类型不能为空")
        private String custsort;
        @NotEmpty(message = "单位电话号码不能为空")
        private String cophoneno;
        @NotEmpty(message = "单位邮编不能为空")
        private String corpzip;
        @NotEmpty(message = "证件号码不能为空")
        private String custcode;
        @NotEmpty(message = "手机选择不能为空")
        private String mblchoic;
        @NotEmpty(message = "单位电话区号不能为空")
        private String cophozono;
        @NotEmpty(message = "单位电话分机不能为空")
        private String cophonext;
        @NotEmpty(message = "联系人一性别不能为空")
        private String sex;
        @NotEmpty(message = "住宅地址选择不能为空")
        private String hadrchoic;
        @NotEmpty(message = "住宅地址省份不能为空")
        private String hprovince;
        @NotEmpty(message = "单位地址市不能为空")
        private String ccity;
        @NotEmpty(message = "职业不能为空")
        private String occptn;
        @NotEmpty(message = "联系人二姓名不能为空")
        private String reltname2;
        @NotEmpty(message = "发送短信帐单手机号码不能为空")
        private String smsphone;
        @NotEmpty(message = "联系人一姓名不能为空")
        private String reltname1;
        @NotEmpty(message = "住宅地址县不能为空")
        private String hcounty;
        @NotEmpty(message = "联系人二性别不能为空")
        private String reltsex2;
        @NotEmpty(message = "联系人一性别不能为空")
        private String reltsex1;
        @NotEmpty(message = "开通email对账单不能为空")
        private String emladdrf;
        @NotEmpty(message = "币种不能为空")
        private String fcurrtyp;
        @NotEmpty(message = "对帐单寄送地址不能为空")
        private String accaddrf;
        @NotEmpty(message = "教育程度不能为空")
        private String edulvl;
    }

    //pub

    @Data
    public static class Pub {
        //----- pub -----
        @NotEmpty
        private String platno;
        @NotEmpty(groups = MultimediaUploadValidated.class)
        private String guestPlatno;
        @NotEmpty(groups = MultimediaUploadValidated.class)
        private String idno;
        @NotEmpty
        private String cmpseq;
        @NotEmpty
        private String zoneno;
        @NotEmpty
        private String phybrno;
        @NotEmpty
        private String orderno;
        @NotEmpty
        private String assurerno;
        @NotEmpty
        private String cmpdate;
        @NotEmpty
        private String cmptime;
        @NotEmpty(groups = ApplyDiviGeneralValidated.class)
        private String busitype;
        @NotEmpty
        private String fileNum;
        @NotEmpty
        private String customerId;
        // 担保单位编号
        @NotEmpty
        private String dcCorpno;
        // 经销商编号
        @NotEmpty
        private String sellerno;

        private Integer activeTimes;
    }

    /**
     * pic文件队列
     */
    @Data
    public static class PicQueue {
        private String picId;

        private String picName;

        private String url;
    }


}
