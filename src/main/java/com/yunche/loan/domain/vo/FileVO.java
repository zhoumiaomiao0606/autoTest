package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
@Data
public class FileVO {
    /**
     * 文件类型：
     * 1-身份证;2-身份证正面;3-身份证反面;4-授权书;5-授权书签字照;
     * 6-驾驶证;7- 户口本;8- 银行流水;9-结婚证;10-房产证;
     * 11-定位照;12-合影照片;13-家访视频; 14-收入证明; 15-面签照;
     * 16-家访照片; 17-车辆照片; 18-其他资料;19-发票;20-合格证/登记证书;
     * 21-保单;22-提车合影;23-行驶证;24-评估资料;
     */
    private Byte type;
    /**
     * 类型名称
     */
    private String name;
    /**
     * 文件存储路径
     */
    private List<String> urls;
}
