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
     * 11-定位照;12-合影;13-房子照片;14-家访视频
     */
    private Byte type;
    /**
     * 文件存储路径
     */
    private List<FileDetail> details;

    @Data
    public static class FileDetail {

        private Long id;

        private String name;

        private String url;

        private Byte status;
    }
}
