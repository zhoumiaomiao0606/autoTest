package com.yunche.loan.domain.param;

import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.constant.VideoFaceConst;

import com.yunche.loan.web.aop.GlobalExceptionHandler;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;

/**
 * @author liuzhe
 * @date 2018/6/10
 */
@Data
public class WebSocketParam {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


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

    /**
     * URL decode
     *
     * @return
     */
    public String getAddress() {

        if (StringUtils.isNotBlank(address)) {

            try {
                String decode = URLDecoder.decode(address, "UTF-8");
                return decode;
            } catch (UnsupportedEncodingException e) {
                logger.error("", e);
            }

        }

        return null;
    }

}
