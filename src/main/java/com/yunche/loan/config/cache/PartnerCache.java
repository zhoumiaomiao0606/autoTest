package com.yunche.loan.config.cache;

import com.google.common.collect.Maps;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.mapper.PartnerDOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author liuzhe
 * @date 2019/1/21
 */
@Component
public class PartnerCache {

    private static final Map<Long, PartnerDO> map = Maps.newHashMap();

    @Autowired
    private PartnerDOMapper partnerDOMapper;


    public PartnerDO getById(Long partnerId) {

        // get
        PartnerDO partnerDO = map.get(partnerId);
        if (null != partnerDO) {
            return partnerDO;
        }

        // refresh
        refresh();

        // get
        partnerDO = map.get(partnerId);
        return partnerDO;
    }

    @PostConstruct
    public void refresh() {

        List<PartnerDO> list = partnerDOMapper.getAll(BaseConst.VALID_STATUS);

        if (!CollectionUtils.isEmpty(list)) {

            list.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        map.put(e.getId(), e);
                    });
        }
    }
}
