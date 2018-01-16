package com.yunche.loan.constant.configure.info.car;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/1/14
 */
public class CarConst {

    /**
     * 生产类型（1:国产;2:合资）
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
        productionTypeMap.put("合资", (byte) 2);

        saleStateMap.put("在销", (byte) 1);
        saleStateMap.put("停销", (byte) 2);

        productionStateMap.put("停产", (byte) 1);
        productionStateMap.put("在产", (byte) 2);

        fuelTypeMap.put("汽油", (byte) 1);
        fuelTypeMap.put("柴油", (byte) 2);
    }

}
