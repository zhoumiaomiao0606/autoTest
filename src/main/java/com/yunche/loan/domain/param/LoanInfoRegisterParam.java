package com.yunche.loan.domain.param;

import com.yunche.loan.domain.entity.LoanCustomerDO;
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
     * 产品大类
     */
    private String categorySuperior;

    /**
     * 金融方案
     */
    private LoanFinancialPlanParam loanFinancialPlanParam;
    /**
     * 客户信息
     */
    private LoanCustomerDO loanCustomerDO;

    private String workCompanyName;


    private String monthIncome;

}
