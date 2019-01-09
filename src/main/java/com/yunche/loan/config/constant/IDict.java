package com.yunche.loan.config.constant;

public class IDict {


    /**
     * 证件类型
     */
    public static abstract interface K_JJLX {
        /**
         * 身份证
         */
        public static final String IDCARD = "000";
        /**
         * 护照
         */
        public static final String PASSPORT = "001";
        /**
         * 军官证
         */
        public static final String CERTIFICATE_OF_OFFICERS = "002";
        /**
         * 士兵证
         */
        public static final String SOLDIER_CARD = "003";
        /**
         * 港澳台居民往来通行证
         */
        public static final String HK_MACAO_PASS = "004";
        /**
         * 临时身份证
         */
        public static final String TEMP_IDCARD = "005";
        /**
         * 户口本
         */
        public static final String HUKOU_BOOK = "006";
        /**
         * 其他
         */
        public static final String OTHER = "007";
        /**
         * 无
         */
        public static final String NOTHING = "008";
        /**
         * 警官证
         */
        public static final String POLICE_CARD = "009";
        /**
         * 外国人永久居留证
         */
        public static final String FOREIGN_PERMANENT_RESIDENCE = "012";
        /**
         * 边民通行证
         */
        public static final String BORDER_PASS = "021";
    }

    /**
     * 关系
     */
    public static abstract interface K_RELA {
        public static final String ONESELF = "本人";
        public static final String SPOUSE = "配偶";
        public static final String ANTI_GUARANTEE = "反担保";
    }


    /**
     * 关系
     */
    public static abstract interface K_RESULT {
        /*
        001:通过；
        003:不通过；
        099:退回，由于资料不全等原因退回
        */
        public static final String PASS = "001";
        public static final String NOPASS = "003";
        public static final String BACK = "099";
    }

    /**
     * K_JJSTS
     */
    public static abstract interface K_JJSTS {
        public static final String SUCCESS_ERROR = "0";
        public static final String SUCCESS = "1";
        public static final String PROCESS = "2";
        public static final String BACK = "3";
        public static final String TIMEOUT = "4";
    }

    public static abstract interface K_FEEMODE {
        public static final String FIRST = "1";

        public static final String TERM = "2";
    }

    public static abstract interface K_ISPAWN {
        public static final String YES = "1";

        public static final String NO = "0";
    }

    public static abstract interface K_ISASSURE {
        public static final String YES = "1";

        public static final String NO = "0";
    }

    public static abstract interface K_TIEXIFLAG {
        public static final String YES = "1";

        public static final String NO = "0";
    }

    /**
     * 工行接口code
     */
    public static abstract interface K_TRANS_CODE {
        /**
         * 合作机构征信查询请求接口
         */
        public static final String APPLYCREDIT = "applyCredit";
        /**
         * 查询申请进度
         */
        public static final String APPLYSTATUS = "applystatus";
        /**
         * 多媒体补偿接口
         */
        public static final String MULTIMEDIAUPLOAD = "multimediaupload";
        /**
         * 专项卡申请信息上送接口
         */
        public static final String CREDITCARDAPPLY = "creditcardapply";
        /**
         * 文件清单信息下载接口
         */
        public static final String FILEDOWNLOAD = "filedownload";
        /**
         * 文件清单信息上送接口
         */
        public static final String FILEUPLOAD = "fileupload";
        /**
         * 通用业务申请接口
         */
        public static final String APPLYDIVIGENERAL = "applydivigeneral";
        /**
         * 查询专项卡开卡进度
         */
        public static final String APPLYCREDITSTATUS = "applycreditstatus";

    }


    /**
     * 工行接口名称
     */
    public static abstract interface K_API {
        /**
         * 合作机构征信查询请求接口
         */
        public static final String APPLYCREDIT = "com.icbc.bcis.apply.applycredit";
        /**
         * 查询申请进度
         */
        public static final String APPLYSTATUS = "com.icbc.bcis.apply.applystatus";
        /**
         * 专项卡申请信息上送接口
         */
        public static final String CREDITCARDAPPLY = "com.icbc.bcis.apply.creditcardapply";
        /**
         * 文件清单信息下载接口
         */
        public static final String FILEDOWNLOAD = "com.icbc.bcis.apply.filedownload";
        /**
         * 文件清单信息上送接口
         */
        public static final String FILEUPLOAD = "com.icbc.bcis.apply.fileupload";
        /**
         * 通用业务申请接口
         */
        public static final String APPLYDIVIGENERAL = "com.icbc.bcis.apply.applydivigeneral";
        /**
         * 查询专项卡开卡进度
         */
        public static final String APPLYCREDITSTATUS = "com.icbc.bcis.apply.applycreditstatus";


    }

    /**
     * 图片资料ID
     */
    public static abstract interface K_PIC_ID {
        /**
         * 授权书签字照片
         */
        public static final String AUTHORIZATION_SIGNATURE_PHOTO = "0004";
        /**
         * 客户征信查询授权书+身份证正反面.doc
         */
        public static final String AUTHORIZATION_SIGNATURE_IDCARD_WORD = "0005";
        /**
         * 【开卡】专项额度核定申请表
         */
        public static final String SPECIAL_QUOTA_APPLY = "1001";
        /**
         * 【开卡】开卡申请表(和身份证正反面合并成一张图片)
         */
        public static final String OPEN_CARD_DATA = "1002";
        /**
         * 【分期】视频面签文件
         */
        public static final String VIDEO_INTERVIEW = "8888";
    }


    /**
     * 交易状态
     */
    public static abstract interface K_JYZT {

        /**
         * 处理失败
         */
        public static final Byte SUCCESS_ERROR = 0;
        /**
         * 处理成功
         */
        public static final Byte SUCCESS = 1;
        /**
         * 进行中
         */
        public static final Byte PROCESS = 2;
        /**
         * 银行驳回
         */
        public static final Byte BACK = 3;
        /**
         * 超时
         */
        public static final Byte TIMEOUT = 4;
        /**
         * 请求成功
         */
        public static final Byte REQ_SUCC = 5;
        /**
         * 请求失败
         */
        public static final Byte FAIL = 6;

        /**
         * 交易处理前
         */
        public static final Byte PRE_TRANSACTION = 7;
        /**
         * 文件下载完成
         */
        public static final Byte DOWNLOAD = 8;

    }

    /**
     * 文件类型
     */
    public static abstract interface K_WJLX {
        /**
         * 开卡清单文件
         */
        public static final String WJLX_0 = "0";
        /**
         * 逾期催收清单文件
         */
        public static final String WJLX_1 = "1";
        /**
         * 未抵押明细清单文件
         */
        public static final String WJLX_2 = "2";
        /**
         * 逾期催缴清单文件
         */
        public static final String WJLX_3 = "3";
    }

    public static abstract interface K_BANK {
        /**
         * 中国工商银行杭州城站支行
         */
        public static final String ICBC_HZCZ = "1";
        /**
         * 中国工商银行哈尔滨顾乡支行
         */
        public static final String ICBC_HEBGX = "2";
        /**
         * 中国工商银行台州路桥支行
         */
        public static final String ICBC_TZLQ = "3";
        /**
         * 中国工商银行南京江宁支行
         */
        public static final String ICBC_NJJN = "4";

    }

    /**
     * 是/否
     */
    public static abstract interface K_YORN {
        public static final Byte K_YORN_NO = 0;
        public static final Byte K_YORN_YES = 1;

    }

    /**
     * 币种
     */
    public static abstract interface K_BZ {
        public static final String K_RMB = "001";
    }

    /**
     * 婚姻转台
     */
    public static abstract interface K_HYZT {

        public static final String K_HYZT_0 = "0";
        public static final String K_HYZT_1 = "1";

    }

    //A.客户逾期；B.车辆回收后余额代偿结清；C.替客户偿还；D.拿车前代偿

    /**
     * 代偿原因
     */
    public static abstract interface K_DCYY {
        /**
         * 客户逾期
         */
        public static final String K_DCYY_A = "客户逾期";
        /**
         * 车辆回收后余额代偿结清
         */
        public static final String K_DCYY_B = "车辆回收后余额代偿结清";
        /**
         * 替客户偿还
         */
        public static final String K_DCYY_C = "替客户偿还";
        /**
         * 拿车前代偿
         */
        public static final String K_DCYY_D = "拿车前代偿";
    }

    /**
     * 签单类型
     */
    public static abstract interface K_QDLX {
        /**
         * 夫妻单签
         */
        public static final Byte K_QDLX_1 = 1;

        /**
         * 夫妻双签
         */
        public static final Byte K_QDLX_2 = 2;

        /**
         * 其他
         */
        public static final Byte K_QDLX_3 = 3;


    }

    public static abstract interface  K_CONF_THIRD_PARTY{
        /**
         * 金投行配置ID
         */
        public static final Long K_JTH = 1L;
    }


    /**
     * 第三方资金出借状态
     */
    public static abstract interface K_CJZT{

        /**
         * 未出借
         */
        public static final Byte K_CJZT_NO = 1;
        /**
         * 已出借
         */
        public static final Byte K_CJZT_YES = 2;
        /**
         * 拒绝
         */
        public static final Byte K_CJZT_REJECT = 3;
        /**
         * 处理中
         */
        public static final Byte K_CJZT_INHAND = 4;
        /*
        * 借款人信息认证失败
         */
        public static final Byte K_CJZT_AUTHINFIO_ERROR = 5;
        /*
         * 资产发布失败
         */
        public static final Byte K_CJZT_ASSETRELEASE_ERROR = 6;
        /*
        *金投行推送成功
         */
        public static final Byte K_CJZT_SUCCESS = 7;
        /*
         *金投审核不通过
         */
        public static final Byte K_CJZT_NOPASS = 8;
        /*
        *返回数据处理异常
         */
        public static final Byte K_CJZT_HANDLE_ERROR = 9;



    }

    /**
     * 文件后缀
     */
    public static abstract interface K_SUFFIX{

        public static final String K_SUFFIX_JPG = ".jpg";

        public static final String K_SUFFIX_DOC = ".doc";
    }

    /**
     * 银行征信图片导出状态
     */
    public static abstract interface K_CREDIT_PIC_EXP{
        public static final Byte K_SUFFIX_JPG_NO = 1;
        public static final Byte K_SUFFIX_JPG_YES = 2;
    }


    /**
     * 财务系统凭证type
     */
    public static abstract interface K_VOUCHER{

        /**
         * 代客户垫款
         */
        public static final Byte K_VOUCHER_1 = 1;
        /**
         * 收到银行款项
         */
        public static final Byte K_VOUCHER_2 = 2;
        /**
         * 客户退款
         */
        public static final Byte K_VOUCHER_3 = 3;
        /**
         * 公司代客户偿款
         */
        public static final Byte K_VOUCHER_8 = 8;
    }


    /**
     * 开卡状态
     */
    public static abstract interface K_KKZT{
        /**
         * 待申请
         */
        public static final String WAIT="1";
        /**
         * 已申请
         */
        public static final String HAVE_APP="2";
        /**
         * 异常退回
         */
        public static final String ERR_BACK="3";
        /**
         * 银行退回
         */
        public static final String BANK_BACK="4";
        /**
         * 开卡成功
         */
        public static final String OPEN_SUCC="5";
    }

    /**
     * 评估类型
     */
    public static abstract interface K_EVALUATION_TYPE{
        /**
         * 在线评估
         */
        public static final Byte ONLINE=1;

        /**
         * 人工评估
         */
        public static final Byte ARTIFICIAL=2;

    }

    /**
     * 打款状态(打款状态--默认为-0-未打款----1-打款中---2-打款成功---3--打款失败)
     */
    public static abstract interface K_DKZT{
        /**
         * 未打款
         */
        public static final Byte NO_PAY=0;

        /**
         * 打款中
         */
        public static final Byte PAYING=1;


        /**
         * 打款成功
         */
        public static final Byte PAY_SUCC=2;

        /**
         * 打款成功
         */
        public static final Byte PAY_FAIL=3;

    }
}

