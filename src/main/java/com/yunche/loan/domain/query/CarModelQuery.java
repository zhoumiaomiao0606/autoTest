package com.yunche.loan.domain.query;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/18
 */
@Data
public class CarModelQuery extends BaseQuery {
    /**
     * 所属品牌ID
     */
    private Long brandId;
    /**
     * 车系简称 eg：A3
     */
    private String name;
    /**
     * 车系全称 eg：奥迪A3
     */
    private String fullName;
    /**
     * 首字母
     */
    private String initial;
    /**
     * logo图片URL
     */
    private String logo;
    /**
     * 厂家指导价（min-max,取子车型的两极值）
     */
    private String price;
    /**
     * 座位数(min/max，取子车型的两极值)
     */
    private String seatNum;
    /**
     * 销售状态（1:在售; 2:停售）
     */
    private Byte saleState;
    /**
     * 车系编码
     */
    private String seriesCode;
    /**
     * 助记码
     */
    private String mnemonicCode;
    /**
     * 生产厂商
     */
    private String productionFirm;
    /**
     * 生产类型（1:国产;2:合资）
     */
    private Byte productionType;
}
