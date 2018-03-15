package com.yunche.loan.config.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/1/14
 */
public class CarConst {
    /**
     * 品牌
     */
    public static final Byte CAR_BRAND = 1;
    /**
     * 车系
     */
    public static final Byte CAR_MODEL = 2;
    /**
     * 车型
     */
    public static final Byte CAR_DETAIL = 3;

    /**
     * 生产类型（1:国产;2:进口）
     */
    public static Map<String, Byte> productionTypeMap = Maps.newHashMap();
    /**
     * 销售状态（1:在售; 2:停售）
     */
    public static Map<String, Byte> saleStateMap = Maps.newHashMap();
    /**
     * 生产状态(1:在产;2:停产)
     */
    public static Map<String, Byte> productionStateMap = Maps.newHashMap();
    /**
     * 燃油类型（1:汽油;2:柴油）
     */
    public static Map<String, Byte> fuelTypeMap = Maps.newHashMap();

    static {
        productionTypeMap.put("国产", (byte) 1);
        productionTypeMap.put("进口", (byte) 2);

        saleStateMap.put("在销", (byte) 1);
        saleStateMap.put("停销", (byte) 2);

        productionStateMap.put("停产", (byte) 1);
        productionStateMap.put("在产", (byte) 2);

        fuelTypeMap.put("汽油", (byte) 1);
        fuelTypeMap.put("柴油", (byte) 2);
    }
}
