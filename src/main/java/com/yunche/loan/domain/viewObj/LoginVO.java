package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuzhe
 * @date 2018/2/8
 */
@Data
public class LoginVO {

    private Long userId;

    private String username;

    private Serializable token;
}
