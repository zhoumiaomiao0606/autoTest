package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/17
 */
@Data
public class CarDetailVO {
    /**
     * 车款ID
     */
    private Long id;
    /**
     * 所属车系ID
     */
    private Long modelId;
    /**
     * 车款名称
     */
    private String name;
    /**
     * 首字母
     */
    private String initial;
    /**
     * logo图片URL
     */
    private String logo;
    /**
     * 厂家指导价(单位：万)
     */
    private String price;
    /**
     * 商家报价(单位：万)
     */
    private String salePrice;
    /**
     * 座位数
     */
    private Integer seatNum;
    /**
     * 门数
     */
    private Integer doorNum;
    /**
     * 年款
     * eg：2018
     */
    private String yearType;
    /**
     * 生产状态(1:在产;2:停产)
     */
    private Byte productionState;
    /**
     * 销售状态（1:在销; 2:停销)
     */
    private Byte saleState;
    /**
     * 尺寸类型  (eg:紧凑型车)
     */
    private String sizeType;
    /**
     * 燃油类型（1:汽油;2:柴油）
     */
    private Byte fuelType;

    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;
}