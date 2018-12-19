package com.yunche.loan.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author liuzhe
 * @date 2018/12/4
 */
@Data
public class LoginUserExtInfo implements Serializable {

    private static final long serialVersionUID = 362978167027927774L;


    private Long loginUserId;

    private String loginUserName;

    private Long partnerId;

    private String partnerName;
}
