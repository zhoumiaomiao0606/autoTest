package com.yunche.loan.domain.param;

import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.constant.VideoFaceConst;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author liuzhe
 * @date 2018/6/10
 */
@Data
@Component
public class WebSocketParam {
    /**
     * 客户端类型
     *
     * @see VideoFaceConst
     */
    private Integer type;

    private Long bankId;

    private String bankName;

    private Long userId;

    private Long sendUserId;

    private Long receiveUserId;


    ////////////////////////////////////////////////////////////////////////////


    @Autowired
    private BankCache bankCache;

    public Long getBankId() {

        if (StringUtils.isNotBlank(bankName)) {

            Long bankId = bankCache.getBankIdByName(bankName);
            return bankId;
        }

        return null;
    }

}
