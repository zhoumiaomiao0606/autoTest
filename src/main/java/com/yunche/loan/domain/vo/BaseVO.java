package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/25
 */
@Data
public class BaseVO {

    private Long id;

    private String name;

    private Long parentId;

    public BaseVO() {
    }

    public BaseVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
