package com.yunche.loan.domain.param;

import com.yunche.loan.domain.dataObj.PartnerDO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/24
 */
@Data
public class PartnerParam extends PartnerDO {
    /**
     * 业务产品ID列表
     */
    private List<Long> bizModelIdList;
}
