package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/3/14
 */
public class LoanFileConst {

    /**
     * 常规上传
     */
    public static final Byte UPLOAD_TYPE_NORMAL = 1;
    /**
     * 增补上传
     */
    public static final Byte UPLOAD_TYPE_SUPPLEMENT = 2;

    /**
     * 文件类型-类型名  映射关系
     * <p>
     * 文件类型：1-身份证;2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;
     * 6-驾驶证;7- 户口本;8- 银行流水;9-结婚证;10-房产证;
     * 11-定位照;12-合影照片;13-家访视频; 14-收入证明; 15-面签照;
     * 16-家访照片; 17-车辆照片; 18-其他资料;19-发票;20-合格证/登记证书;
     * 21-保单;22-提车合影;
     */
    public static final Map<Byte, String> TYPE_NAME_MAP = Maps.newConcurrentMap();

    static {
        TYPE_NAME_MAP.put((byte) 1, "身份证");
        TYPE_NAME_MAP.put((byte) 2, "身份证正面");
        TYPE_NAME_MAP.put((byte) 3, "身份证反面");
        TYPE_NAME_MAP.put((byte) 4, "授权书");
        TYPE_NAME_MAP.put((byte) 5, "授权书签字照");
        TYPE_NAME_MAP.put((byte) 6, "驾驶证");
        TYPE_NAME_MAP.put((byte) 7, "户口本");
        TYPE_NAME_MAP.put((byte) 8, "银行流水");
        TYPE_NAME_MAP.put((byte) 9, "结婚证");
        TYPE_NAME_MAP.put((byte) 10, "房产证");
        TYPE_NAME_MAP.put((byte) 11, "定位照");
        TYPE_NAME_MAP.put((byte) 12, "合影照片");
        TYPE_NAME_MAP.put((byte) 13, "家访视频");
        TYPE_NAME_MAP.put((byte) 14, "收入证明");
        TYPE_NAME_MAP.put((byte) 15, "面签照");
        TYPE_NAME_MAP.put((byte) 16, "家访照片");
        TYPE_NAME_MAP.put((byte) 17, "车辆照片");
        TYPE_NAME_MAP.put((byte) 18, "其他资料");
        TYPE_NAME_MAP.put((byte) 19, "发票");
        TYPE_NAME_MAP.put((byte) 20, "合格证/登记证书");
        TYPE_NAME_MAP.put((byte) 21, "保单");
        TYPE_NAME_MAP.put((byte) 22, "提车合影");


        //
    }
}
