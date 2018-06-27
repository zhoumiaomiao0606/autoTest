package com.yunche.loan.config.constant;

import lombok.Getter;
import lombok.Setter;

/*银行流水*/
public enum  BankInterfaceSerialStatusEnum {
    //1 查询成功 2 处理中 3 回退 4 超时

    SUCCESS(new Byte("1")),
    PROCESS(new Byte("2")),
    BACK(new Byte("3")),
    TIMEOUT(new Byte("4"));

    @Getter
    @Setter
    private Byte status;


    BankInterfaceSerialStatusEnum(Byte status) {
        this.status = status;
    }

}
