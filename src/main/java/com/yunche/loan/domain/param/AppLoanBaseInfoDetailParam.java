package com.yunche.loan.domain.param;

import com.yunche.loan.domain.vo.BaseVO;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/9
 */
@Data
public class AppLoanBaseInfoDetailParam {


    private Long id;

    private Long orderId;
    /**
     * 合伙人对象
     */
    private BaseVO partner;
    /**
     * 业务员对象
     */
    private BaseVO salesman;
    /**
     * 区域对象
     */
    private BaseVO area;

    private Byte carType;

    private String bank;

    private Byte loanAmount;
}
