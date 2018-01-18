package com.yunche.loan.domain.valueObj;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 品牌-车系-车型 三级关联关系
 *
 * @author liuzhe
 * @date 2018/1/18
 */
@Data
public class CarThreeLevelVO {

    // all品牌
    private List<Brand> carBrand = Lists.newArrayList();

    /**
     * 三级联动   -- 单个品牌
     */
    @Data
    public static class CarOneBrandThreeLevelVO {
        private Brand carBrand;
    }

    @Data
    public static class Brand {
        private Long id;

        private String name;

        private String initial;

        private String logo;

        private Date gmtCreate;

        private Date gmtModify;
        /**
         * 子车系
         */
        private List<Model> carModel = Lists.newArrayList();
    }

    @Data
    public static class Model {
        /**
         * 车型ID
         */
        private Long id;
        /**
         * 车型简称 eg：A3
         */
        private String name;
        /**
         * 车型全称 eg：奥迪A3
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

        private Date gmtCreate;

        private Date gmtModify;
        /**
         * 子车型
         */
        private List<Detail> carDetail = Lists.newArrayList();
    }

    @Data
    public static class Detail {
        /**
         * 车款ID
         */
        private Long id;
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

        private Date gmtCreate;

        private Date gmtModify;
    }

}
