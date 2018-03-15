package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.FileVO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@Data
public class AppInfoSupplementParam {
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 文件列表
     */
    private List<FileVO> files;


}
