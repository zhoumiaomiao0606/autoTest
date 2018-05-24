package com.yunche.loan.config.constant;

/**
 * @author liuzhe
 * @date 2018/5/24
 */
public class LoanAmountConst {

    /**
     * 预计贷款额： X < 13W
     */
    public static final Byte EXPECT_LOAN_AMOUNT_LT_13W = 1;
    /**
     * 预计贷款额： 13W <=  X  < 20W
     */
    public static final Byte EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W = 2;
    /**
     * 预计贷款额： X >= 20W
     */
    public static final Byte EXPECT_LOAN_AMOUNT_EQT_20W = 3;


    /**
     * 实际贷款额： 13W
     */
    public static final Integer ACTUAL_LOAN_AMOUNT_13W = 130000;
    /**
     * 实际贷款额： 20W
     */
    public static final Integer ACTUAL_LOAN_AMOUNT_20W = 200000;

}
