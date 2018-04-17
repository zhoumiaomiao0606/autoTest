package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class FlowOperationMsgVO {
    private String id;
    private String order_id;
    private String title;
    private String prompt;
    private String msg;
    private String sender;
    private String process_key;
    private String send_date;
    private String read_status;
    private String type;
    private String customer_name;
    private String customer_id_card;
}
