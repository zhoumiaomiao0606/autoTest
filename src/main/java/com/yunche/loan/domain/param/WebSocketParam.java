package com.yunche.loan.domain.param;

import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.constant.VideoFaceConst;

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

    private static final Logger logger = LoggerFactory.getLogger(WebSocketParam.class);


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


    /**
     * 网络类型：  0-无网络 / 1-WiFi / 2-2G / 3-3G / 4-4G / 5-5G
     */
    private Integer netType;
    /**
     * 上行网速：200k/s
     */
    private Double uplinkSpeed;
    /**
     * 下行网速：2M/s
     */
    private Double downlinkSpeed;


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


    //////////////////////////////////////////////  network  ///////////////////////////////////////////////////////////

    public String getNetType() {

        String netTypeText = "";

        if (null != netType) {

            switch (netType) {
                case 0:
                    netTypeText = "无网络";
                case 1:
                    netTypeText = "WiFi";
                case 2:
                    netTypeText = "2G";
                case 3:
                    netTypeText = "3G";
                case 4:
                    netTypeText = "4G";
                case 5:
                    netTypeText = "5G";
                default:
                    netTypeText = "未知";
            }
        }

        return netTypeText;
    }

    public String getUplinkSpeed() {

        return getNetSpeed(uplinkSpeed);
    }

    public String getDownlinkSpeed() {

        return getNetSpeed(downlinkSpeed);
    }

    private String getNetSpeed(Double netSpeed) {

        if (null == netSpeed) {

            return "0k/s";

        } else if (netSpeed < 1000) {

            double value = new BigDecimal(netSpeed).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            return value + "k/s";

        } else {

            double m = netSpeed / 1024;

            double value = new BigDecimal(m).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            return value + "M/s";
        }
    }
    //////////////////////////////////////////////  network  ///////////////////////////////////////////////////////////

}
