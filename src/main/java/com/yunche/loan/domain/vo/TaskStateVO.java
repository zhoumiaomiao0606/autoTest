package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/3/2
 */
@Data
public class TaskStateVO {

    private String taskDefinitionKey;

    private String taskName;

    private Byte taskStatus;

    private String taskStatusText;
}
