package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/19
 */
@Data
public class ConfVideoFaceTimeVO {

    private Long bankId;

    private List<Detail> detailList = Collections.EMPTY_LIST;

    @Data
    public static class Detail {

        private BigDecimal startLoanAmount;

        private BigDecimal endLoanAmount;

        private List<Type> typeList = Collections.EMPTY_LIST;
    }

    @Data
    public static class Type {

        private Byte type;

        private List<Time> timeList = Collections.EMPTY_LIST;
    }

    @Data
    public static class Time {

        private String startTime;

        private String endTime;
    }
}


