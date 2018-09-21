package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
public class VideoFaceConst {

    /**
     * 坐席端 -PC
     */
    public static final Byte TYPE_PC = 1;
    /**
     * 客户端 -APP
     */
    public static final Byte TYPE_APP = 2;

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
     * 担保公司  写死
     */
    public static final Long GUARANTEE_COMPANY_ID = 1L;
    public static final String GUARANTEE_COMPANY_NAME = "浙江鑫宝行融资担保有限公司";


    /**
     * 管理员能查看所有银行的面签数据
     */
    public static final Long ADMIN_VIDEO_FACE_BANK_ID = -1L;


    //////////////////////////////////-- conf_video_face_time --> type --///////////////////////////////////////////////
    /**
     * 工作日
     */
    public static final Byte CONF_TYPE_WORK = 1;
    /**
     * 周末
     */
    public static final Byte CONF_TYPE_WEEKEND = 2;
    /**
     * 节假日
     */
    public static final Byte CONF_TYPE_HOLIDAY = 3;
    //////////////////////////////////-- conf_video_face_time --> type --///////////////////////////////////////////////
}
