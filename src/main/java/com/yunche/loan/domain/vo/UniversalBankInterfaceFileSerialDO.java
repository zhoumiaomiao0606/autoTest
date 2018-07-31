package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class UniversalBankInterfaceFileSerialDO {

    private String id;
    private String serial_no;
    private String file_name;
    private String file_path;
    private String file_type;
    private String success;
    private String request_time;
    private String error;

}
