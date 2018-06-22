package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
public class VideoFaceConst {

    /**
     * 坐席端 -PC
     */
    public static final Integer TYPE_PC = 1;
    /**
     * 客户端 -APP
     */
    public static final Integer TYPE_APP = 2;

    /**
     * 面签结果：通过
     */
    public static final Byte ACTION_PASS = 1;
    /**
     * 面签结果：不通过
     */
    public static final Byte ACTION_NOT_PASS = 2;


    /**
     * 人工面签
     */
    public static final Byte FACE_SIGN_TYPE_ARTIFICIAL = 1;
    /**
     * 机器面签
     */
    public static final Byte FACE_SIGN_TYPE_MACHINE = 2;


    /**
     * 中国工商银行杭州城站支行
     */
    public static final Long BANK_ID_ICBC_HangZhou_City_Station_Branch = 1L;
    /**
     * 中国工商银行哈尔滨顾乡支行
     */
    public static final Long BANK_ID_ICBC_Harbin_GuXiang_Branch = 2L;
    /**
     * 中国工商银行台州路桥支行
     */
    public static final Long BANK_ID_ICBC_TaiZhou_LuQiao_Branch = 3L;
    /**
     * 中国工商银行南京江宁支行
     */
    public static final Long BANK_ID_ICBC_NanJing_JiangNing_Branch = 4L;

    /**
     * diskName
     */
    public static final String OSS_DISK_NAME = "videoFace/log/";

    /**
     * 担保公司
     */
    public static final Long GUARANTEE_COMPANY_ID = 1L;
    public static final String GUARANTEE_COMPANY_NAME = "云车金融";

}
