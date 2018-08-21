package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.List;

@Data
public class FileInfoParam {
    private Long id;


    private Long orderId;

    private Long bankRepayImpRecordId;

    /**
     * 资料增补备注
     */
    private String remark;
    /**
     * 文件列表
     */
    private List<FileVO> files;
}