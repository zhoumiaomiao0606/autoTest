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
    private List<Long> partnerIds;
}
