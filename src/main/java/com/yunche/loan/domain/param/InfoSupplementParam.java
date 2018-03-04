package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.CustomerVO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/4
 */
@Data
public class InfoSupplementParam {
    /**
     * 客户ID
     */
    private Long customerId;
    /**
     * 文件列表
     */
    private List<CustomerVO.File> files;
}
