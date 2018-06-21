package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class TelephoneVerifyNodeOrdersVO {
    String commit_status;
    String order_id;
    String customer_name;
    String customer_card_type;
    String customer_id_card;
    String saleman_name;
    String partner_name;
    String saleman_department_name;
    String loan_amount;
    String gps_number;
    String op_user_name;
    String result;
    String action;
    String op_time;
    String op_info;
}
