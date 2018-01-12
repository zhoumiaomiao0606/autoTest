package com.yunche.loan.bo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Data
public class BaseAreaBO {

    private Integer id;

    private Long codeId;

    private Long parentCodeId;

    private String positionName;

    private Byte level;

}
