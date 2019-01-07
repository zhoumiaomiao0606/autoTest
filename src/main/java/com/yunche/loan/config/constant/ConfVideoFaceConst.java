package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2019/1/7
 */
public class ConfVideoFaceConst {

    // ----------机器面签状态
    /**
     * 关闭
     */
    public static final Byte MACHINE_VIDEO_FACE_STATUS_CLOSE = 0;
    /**
     * 开启   -默认
     */
    public static final Byte MACHINE_VIDEO_FACE_STATUS_OPEN = 1;


    // ----------人工面签状态
    /**
     * 关闭   -默认
     */
    public static final Byte ARTIFICIAL_VIDEO_FACE_STATUS_CLOSE = 0;
    /**
     * 开启
     */
    public static final Byte ARTIFICIAL_VIDEO_FACE_STATUS_OPEN = 1;


    // ----------人工面签-是否开启强制定位
    /**
     * 不需要
     */
    public static final Byte ARTIFICIAL_VIDEO_FACE_NEED_LOCATION_FALSE = 0;
    /**
     * 需要   -默认
     */
    public static final Byte ARTIFICIAL_VIDEO_FACE_NEED_LOCATION_TRUE = 1;
}
