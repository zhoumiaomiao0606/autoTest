package com.yunche.loan.domain.dataObj;

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

    private Integer level;

    private List<City> cityList = Lists.newArrayList();

    @Data
    public static class City {

        private Long id;

        private String name;

        private Integer level;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
