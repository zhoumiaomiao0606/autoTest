package com.yunche.loan.domain.viewObj;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/2
 */
@Data
public class TaskStateVO {

    private String taskDefinitionKey;

    private String name;

    private Byte taskStatus;
}
