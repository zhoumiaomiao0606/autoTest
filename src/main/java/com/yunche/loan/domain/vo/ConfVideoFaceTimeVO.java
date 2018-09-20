package com.yunche.loan.domain.vo;

import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/19
 */
@Data
public class ConfVideoFaceTimeVO {

    private Long bankId;

    private List<Detail> detailList = Lists.newArrayList();

    @Data
    public static class Detail {

        private BigDecimal startLoanAmount;

        private BigDecimal endLoanAmount;

        private List<Type> typeList = Lists.newArrayList();
    }

    @Data
    public static class Type {

        private Byte type;

        private List<Time> timeList = Lists.newArrayList();
    }

    @Data
    public static class Time {

        private String startTime;

        private String endTime;
    }
}


