package com.yunche.loan.config.constant;

import org.omg.CORBA.TIMEOUT;

public class IDict {


    /**
     * 证件类型
     */
    public static abstract interface K_JJLX{
        /**
         * 身份证
         */
        public static final String IDCARD="000";
        /**
         * 护照
         */
        public static final String PASSPORT="001";
        /**
         * 军官证
         */
        public static final String CERTIFICATE_OF_OFFICERS="002";
        /**
         * 士兵证
         */
        public static final String SOLDIER_CARD="003";
        /**
         * 港澳台居民往来通行证
         */
        public static final String HK_MACAO_PASS="004";
        /**
         * 临时身份证
         */
        public static final String TEMP_IDCARD="005";
        /**
         * 户口本
         */
        public static final String HUKOU_BOOK="006";
        /**
         * 其他
         */
        public static final String OTHER="007";
        /**
         *无
         */
        public static final String NOTHING="008";
        /**
         * 警官证
         */
        public static final String POLICE_CARD="009";
        /**
         * 外国人永久居留证
         */
        public static final String FOREIGN_PERMANENT_RESIDENCE="012";
        /**
         * 边民通行证
         */
        public static final String BORDER_PASS="021";
    }

    /**
     * 关系
     */
    public static abstract interface K_RELA{
        public  static final String ONESELF="本人";
        public  static final String SPOUSE="配偶";
        public  static final String ANTI_GUARANTEE ="反担保";
    }

    /**
     * K_JJSTS
     */
    public static abstract interface K_JJSTS{
        public  static final String SUCCESS = "1";
        public  static final String PROCESS = "2";
        public  static final String BACK = "3";
        public  static final String TIMEOUT = "4";
    }



    /**
     * 工行接口名称
     */
    public static abstract interface K_API{
        /**
         * 合作机构征信查询请求接口
         */
        public  static final String APPLYCREDIT ="com.icbc.bcis.apply.applycredit";
        /**
         * 查询申请进度
         */
        public  static final String APPLYSTATUS="com.icbc.bcis.apply.applystatus";
        /**
         * 专项卡申请信息上送接口
         */
        public  static final String CREDITCARDAPPLY ="com.icbc.bcis.apply.creditcardapply";
        /**
         * 文件清单信息下载接口
         */
        public  static final String FILEDOWNLOAD ="com.icbc.bcis.apply.filedownload";
        /**
         * 文件清单信息上送接口
         */
        public  static final String FILEUPLOAD ="com.icbc.bcis.apply.fileupload";
        /**
         * 通用业务申请接口
         */
        public  static final String APPLYDIVIGENERAL ="com.icbc.bcis.apply.applydivigeneral";
        /**
         * 查询专项卡开卡进度
         */
        public  static final String APPLYCREDITSTATUS ="com.icbc.bcis.apply.applycreditstatus";

    }
}
