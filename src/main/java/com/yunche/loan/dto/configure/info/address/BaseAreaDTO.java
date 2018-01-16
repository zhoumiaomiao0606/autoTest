package com.yunche.loan.dto.configure.info.address;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/12
 */
@Data
public class BaseAreaDTO {

    private Integer id;

    private Long codeId;

    private Long parentCodeId;

    private String positionName;

    private Byte level;

}
