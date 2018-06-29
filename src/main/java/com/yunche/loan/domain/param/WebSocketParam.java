package com.yunche.loan.domain.param;

import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.constant.VideoFaceConst;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author liuzhe
 * @date 2018/6/10
 */
@Data
public class WebSocketParam {
    /**
     * 客户端类型
     *
     * @see VideoFaceConst
     */
    private Byte type;

    private Long bankId;

    private String bankName;
    /**
     * 贷款金额
     */
    private BigDecimal loanAmount;
    /**
     * 银行分期本金
     */
    private BigDecimal bankPeriodPrincipal;
    /**
     * anyChat生成的UserId
     */
    private Long anyChatUserId;

    private Long orderId;
    /**
     * customerId / employeeId
     */
    private Long userId;
    /**
     * anyChatUserId  -APP端
     */
    private Long appAnyChatUserId;
    /**
     * anyChatUserId  -PC端
     */
    private Long pcAnyChatUserId;

    /**
     * 人物照片存储路径
     */
    private String livePhotoPath;
    /**
     * APP端面签定位点 经纬度
     */
    private String latlon;
    /**
     * 地址信息
     */
    private String address;


    public Long getBankId(BankCache bankCache) {

        if (null != bankId) {
            return bankId;
        }

        if (StringUtils.isNotBlank(bankName)) {

            Long bankId = bankCache.getIdByName(bankName);
            this.bankId = bankId;
        }

        return bankId;
    }

}
