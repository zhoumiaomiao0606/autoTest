package com.yunche.loan.domain.viewObj;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 省-市 二级关系
 *
 * @author liuzhe
 * @date 2018/1/19
 */
@Data
public class AreaVO {

    private Long id;

    private String name;

    private Byte level;

    private List<City> cityList = Lists.newArrayList();

    @Data
    public static class Prov {

        private Long id;

        private String name;

        private Byte level;

        private City city;
    }

    @Data
    public static class City {

        private Long id;

        private String name;

        private Byte level;
    }

}
