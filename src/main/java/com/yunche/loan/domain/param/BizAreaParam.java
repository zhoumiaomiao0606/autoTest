package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.BizAreaDO;
import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/20
 */
@Data
public class BizAreaParam extends BizAreaDO {
    /**
     * 绑定的城市列表
     */
    private List<Long> areaIdList;
}
