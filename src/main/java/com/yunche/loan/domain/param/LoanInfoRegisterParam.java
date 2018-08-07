package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.BaseVO;
import lombok.Data;

@Data
public class LoanInfoRegisterParam {
    /**
     * 业务单号
     */
    private Long orderId;
    /**
     * 车型信息
     */
    private BaseVO carDetail;

    /**
     * 车辆属性
     */
    private Byte vehicleProperty;
    /**
     * 车辆类型
     */
    private Byte carType;
    /**
     * 车辆颜色
     */
    private String color;
    /**
     * 金融方案
     */
    private LoanFinancialPlanParam loanFinancialPlanParam;

    /**
     *产品ID
     */
    private Long prodId;

    /**
     * 产品大类
     */
    private String categorySuperior;

}
