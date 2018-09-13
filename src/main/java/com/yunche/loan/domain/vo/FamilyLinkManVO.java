package com.yunche.loan.domain.vo;

import lombok.Data;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-12 11:33
 * @description:
 **/
@Data
public class FamilyLinkManVO
{
    //亲属联系人1姓名
    //关系
    //手机号
    private String familyLinkManName;

    private String familyLinkManRelationship;

    private String familyLinkManMobile;
}
