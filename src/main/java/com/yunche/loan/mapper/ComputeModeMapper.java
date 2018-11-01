package com.yunche.loan.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.Map;

@Mapper
public interface ComputeModeMapper {
    //1

    /**
     * 银行分期本金(
     * @param map
     * @return
     */
    BigDecimal periodPrincipal_1(Map<String, BigDecimal> map);

    /**
     *月还款
     * @param map
     * @return
     */
    BigDecimal eachMonthRepay_1(Map<String, BigDecimal> map);

    /**
     *首月还款
     * @param map
     * @return
     */
    BigDecimal firstRepayment_1(Map<String, BigDecimal> map);
    /**
     *贷款利息
     * @param map
     * @return
     */
    BigDecimal loanInterest_1(Map<String, BigDecimal> map);
    /**
     *银行手续费
     * @param map
     * @return
     */
    BigDecimal bankFee_1(Map<String, BigDecimal> map);
    /**
     *还款总额(
     * @param map
     * @return
     */
    BigDecimal totalRepayment_1(Map<String, BigDecimal> map);
    /**
     *贷款成数(
     * @param map
     * @return
     */
    BigDecimal loanToValueRatio_1(Map<String, BigDecimal> map);
    /**
     *银行分期比例
     * @param map
     * @return
     */
    BigDecimal stagingRatio_1(Map<String, BigDecimal> map);



    //2

    /**
     * 月还款
     * @param map
     * @return
     */
    BigDecimal eachMonthRepay_2(Map<String, BigDecimal> map);

    /**
     *首月还款(
     * @param map
     * @return
     */
    BigDecimal firstRepayment_2(Map<String, BigDecimal> map);

    /**
     *银行分期比例
     * @param map
     * @return
     */
    BigDecimal stagingRatio_2(Map<String, BigDecimal> map);

    /**
     *银行手续费
     * @param map
     * @return
     */
    BigDecimal bankFee_2(Map<String, BigDecimal> map);

    /**
     *还款总额
     * @param map
     * @return
     */
    BigDecimal totalRepayment_2(Map<String, BigDecimal> map);


    //公式三

    /**
     *银行分期本金
     * @param map
     * @return
     */
    BigDecimal periodPrincipal_3(Map<String, BigDecimal> map);
    /**
     *月还款
     * @param map
     * @return
     */
    BigDecimal eachMonthRepay_3(Map<String, BigDecimal> map);
    /**
     *首月还款
     * @param map
     * @return
     */
    BigDecimal firstRepayment_3(Map<String, BigDecimal> map);
    /**
     *贷款利息
     * @param map
     * @return
     */
    BigDecimal loanInterest_3(Map<String, BigDecimal> map);
    /**
     *银行分期比例
     * @param map
     * @return
     */
    BigDecimal stagingRatio_3(Map<String, BigDecimal> map);
    /**
     *银行手续费
     * @param map
     * @return
     */
    BigDecimal bankFee_3(Map<String, BigDecimal> map);

    /**
     *还款总额
     * @param map
     * @return
     */
    BigDecimal totalRepayment_3(Map<String, BigDecimal> map);


    //公式四
    BigDecimal periodPrincipal_4(Map<String, BigDecimal> map);
    BigDecimal loanInterest_4(Map<String, BigDecimal> map);
    BigDecimal bankFee_4(Map<String, BigDecimal> map);
    BigDecimal totalRepayment_4(Map<String, BigDecimal> map);
    BigDecimal principalFirstMonthRepay_4(Map<String, BigDecimal> map);
    BigDecimal principalEachMonthRepay_4(Map<String, BigDecimal> map);
    BigDecimal firstMonthBankFee_4(Map<String, BigDecimal> map);
    BigDecimal eachMonthBankFee_4(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_4(Map<String, BigDecimal> map);
    BigDecimal eachMonthRepay_4(Map<String, BigDecimal> map);
    BigDecimal stagingRatio_4(Map<String, BigDecimal> map);



    //公式五

    /**
     * 银行分期本金
     * @param map
     * @return
     */
    BigDecimal periodPrincipal_5(Map<String, BigDecimal> map);

    /**
     * 贷款利息
     * @param map
     * @return
     */
    BigDecimal loanInterest_5(Map<String, BigDecimal> map);

    /**
     * 银行手续费
     * @param map
     * @return
     */
    BigDecimal bankFee_5(Map<String, BigDecimal> map);

    /**
     * 还款总额
     * @param map
     * @return
     */
    BigDecimal totalRepayment_5(Map<String, BigDecimal> map);

    /**
     * 本金首月还款
     * @param map
     * @return
     */
    BigDecimal principalFirstMonthRepay_5(Map<String, BigDecimal> map);

    /**
     * 本金月还款
     * @param map
     * @return
     */
    BigDecimal principalEachMonthRepay_5(Map<String, BigDecimal> map);

    /**
     * 银行首月手续费
     * @param map
     * @return
     */
    BigDecimal firstMonthBankFee_5(Map<String, BigDecimal> map);

    /**
     * 银行每月手续费
     * @param map
     * @return
     */
    BigDecimal eachMonthBankFee_5(Map<String, BigDecimal> map);

    /**
     * 首月还款
     * @param map
     * @return
     */
    BigDecimal firstRepayment_5(Map<String, BigDecimal> map);

    /**
     * 月还款
     * @param map
     * @return
     */

    BigDecimal eachMonthRepay_5(Map<String, BigDecimal> map);

    /**
     * 银行分期比率
     * @param map
     * @return
     */
    BigDecimal stagingRatio_5(Map<String, BigDecimal> map);

    //公式六
    BigDecimal periodPrincipal_6(Map<String, BigDecimal> map);
    BigDecimal eachMonthRepay_6(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_6(Map<String, BigDecimal> map);
    BigDecimal loanInterest_6(Map<String, BigDecimal> map);
    BigDecimal bankFee_6(Map<String, BigDecimal> map);
    BigDecimal totalRepayment_6(Map<String, BigDecimal> map);
    BigDecimal stagingRatio_6(Map<String, BigDecimal> map);

    //公式七
    BigDecimal eachMonthRepay_7(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_7(Map<String, BigDecimal> map);
    BigDecimal bankFee_7(Map<String, BigDecimal> map);
    BigDecimal totalRepayment_7(Map<String, BigDecimal> map);
    BigDecimal stagingRatio_7(Map<String, BigDecimal> map);

    //公式八
    BigDecimal eachMonthRepay_8(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_8(Map<String, BigDecimal> map);
    BigDecimal bankFee_8(Map<String, BigDecimal> map);
    BigDecimal totalRepayment_8(Map<String, BigDecimal> map);
    BigDecimal stagingRatio_8(Map<String, BigDecimal> map);


    //银行分期本金
    BigDecimal periodPrincipal_new(Map<String, BigDecimal> map);




    BigDecimal eachMonthRepay_new_1(Map<String, BigDecimal> map);
    BigDecimal periodPrincipal_new_1(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_new_1(Map<String, BigDecimal> map);
    BigDecimal loanInterest_new_1(Map<String, BigDecimal> map);
    BigDecimal bankFee_new_1(Map<String, BigDecimal> map);
    BigDecimal totalRepayment_new_1(Map<String, BigDecimal> map);
    BigDecimal loanToValueRatio_new_1(Map<String, BigDecimal> map);
    BigDecimal stagingRatio_new_1(Map<String, BigDecimal> map);
    BigDecimal periodPrincipal_new_3(Map<String, BigDecimal> map);
    BigDecimal eachMonthRepay_new_3(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_new_3(Map<String, BigDecimal> map);
    BigDecimal loanInterest_new_3(Map<String, BigDecimal> map);
    BigDecimal stagingRatio_new_3(Map<String, BigDecimal> map);
    BigDecimal bankFee_new_3(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_new_4(Map<String, BigDecimal> map);
    BigDecimal eachMonthRepay_new_4(Map<String, BigDecimal> map);
    BigDecimal firstRepayment_new_6(Map<String, BigDecimal> map);



}
