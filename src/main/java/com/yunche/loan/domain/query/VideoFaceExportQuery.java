package com.yunche.loan.domain.query;

import com.google.common.collect.Sets;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class VideoFaceExportQuery {
    //审核状态    2领取未审   1已审核
    private String videoFaceFlag;
    //审核开始时间
    private String gmtCreateStart1;
//审核结束时间
    private String gmtCreateEnd1;
    //贷款银行列表
    private List<String> bankList;
}
