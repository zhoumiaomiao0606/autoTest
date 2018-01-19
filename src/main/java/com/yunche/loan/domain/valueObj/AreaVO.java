package com.yunche.loan.domain.valueObj;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

/**
 * 全国-省-市  两级联动
 *
 * @author liuzhe
 * @date 2018/1/19
 */
@Data
public class AreaVO {

    private Long id;

    private String name;

    private List<Prov> provs = Lists.newArrayList();

    @Data
    public static class Prov {

        private Long id;

        private String name;

        private List<City> citys = Lists.newArrayList();

    }

    @Data
    public static class City {

        private Long id;

        private String name;

    }


}
