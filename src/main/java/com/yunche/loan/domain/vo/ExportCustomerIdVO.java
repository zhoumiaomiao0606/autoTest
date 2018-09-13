package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-12 09:49
 * @description:
 **/
@Data
public class ExportCustomerIdVO
{
    private Long pCustomerId;

    //亲属联系人1姓名
    //关系
    //手机号
    private List<FamilyLinkManVO> familyLinkManList;

    private List<GuarantorLinkManVO> guarantorLinkManList;
}
