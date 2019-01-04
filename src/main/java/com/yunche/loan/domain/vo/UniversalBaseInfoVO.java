package com.yunche.loan.domain.vo;

import lombok.Data;

import static com.yunche.loan.config.constant.LoanAmountConst.EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W;
import static com.yunche.loan.config.constant.LoanAmountConst.EXPECT_LOAN_AMOUNT_EQT_20W;
import static com.yunche.loan.config.constant.LoanAmountConst.EXPECT_LOAN_AMOUNT_LT_13W;

/**
 * @author liuzhe
 * @date 2018/9/13
 */
@Data
public class UniversalBaseInfoVO {

    private String order_id;
    private String order_gmt_create;

    private Byte financial_expect_loan_amount_val;
    private String financial_expect_loan_amount;

    private String customer_id;
    private String customer_name;
    private String customer_id_card;
    private String customer_mobile;

    private String salesman_id;
    private String salesman_name;

    private String partner_id;
    private String partner_name;
    private String partner_code;
    private String partner_group;

    private String department_id;
    private String department_name;

    private String bank_id;
    private String bank_name;

    private String financial_id;
    private String financial_loan_amount;
    private String financial_loan_time;
    private String financial_car_price;
    private String financial_actual_car_price;

    private String car_detail_id;
    private String car_detail_name;
    private String customer_signature_type;

//    private String remit_application_date;


    /**
     * 预计贷款额转换
     *
     * @return
     */
    public String getFinancial_expect_loan_amount() {

        if (EXPECT_LOAN_AMOUNT_LT_13W.equals(financial_expect_loan_amount_val)) {

            financial_expect_loan_amount = "13万以下";

        } else if (EXPECT_LOAN_AMOUNT_EQT_13W_LT_20W.equals(financial_expect_loan_amount_val)) {

            financial_expect_loan_amount = "13~20万";

        } else if (EXPECT_LOAN_AMOUNT_EQT_20W.equals(financial_expect_loan_amount_val)) {

            financial_expect_loan_amount = "20万以上";
        }

        return financial_expect_loan_amount;
    }
}
