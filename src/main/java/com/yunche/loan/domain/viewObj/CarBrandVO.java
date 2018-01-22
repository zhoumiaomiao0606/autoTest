package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/1/18
 */
@Data
public class CarBrandVO {
    private Long id;

    private String name;

    private String initial;

    private String logo;

    private Date gmtCreate;

    private Date gmtModify;
}
