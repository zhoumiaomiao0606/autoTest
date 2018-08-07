/**
 * @author: ZhongMingxiao
 * @create: 2018-08-07 09:14
 * @description: 提车资料VO
 **/
package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class ExtractionCarMaterialCardVO
{

    //客户姓名
    private String customername;

    //身份证
    private String idCard;

    //手机号
    private String mobile;

    //电审通过时间
    private String usertask_telephone_verify;

    //贷款银行
  /*  private String bank;*/
}
