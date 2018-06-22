package com.yunche.loan.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liuzhe
 * @date 2018/6/13
 */
@Data
public class VideoFaceCustomerVO {

    /**
     * 客户ID
     */
    private Long id;
    /**
     * orderId
     */
    private Long orderId;
    /**
     * anyChatUserId   -APP端
     */
    private Long anyChatUserId;
    /**
     * 客户姓名
     */
    private String name;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 实际车价
     */
    private BigDecimal carPrice;
    /**
     * 意向金额
     */
    private BigDecimal expectLoanAmount;
    /**
     * 车型ID
     */
    private Long carDetailId;
    /**
     * 车型名称
     */
    private String carName;

    /**
     * 担保公司 ID
     */
    private Long guaranteeCompanyId;
    /**
     * 担保公司 name
     */
    private String guaranteeCompanyName;
    /**
     * 公安网纹照片
     */
    private String idCardPhotoPath;
    /**
     * 人物照片
     */
    private String livePhotoPath;
    /**
     * 经纬度
     */
    private String latlon;
    /**
     * 位置
     */
    private String address;

}
