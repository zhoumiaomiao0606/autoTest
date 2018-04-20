package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class InfoSupplementParam {
    /**
     * 增补单ID
     */
    private Long supplementOrderId;
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 资料增补备注
     */
    private String remark;
    /**
     * 文件列表
     */
    private List<FileVO> files;
}
