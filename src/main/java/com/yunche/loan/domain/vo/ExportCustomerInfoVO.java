package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @author: ZhongMingxiao
 * @create: 2018-09-10 15:14
 * @description:
 **/
@Data
public class ExportCustomerInfoVO
{
    //姓名
    //姓名拼音
    //性别
    //出生日期
    //证件号码
    //证件有效截止日
    //国籍
    //婚姻状况
    //教育程度
    //手机号
    //住宅地址
    //住宅电话
    //邮编
    //住宅状况
    //单位名称
    //单位地址
    //邮编
    //单位电话
    //单位经济性质
    //所属行业
    //职业
    //职务
    //年收入


    //亲属联系人1姓名
    //关系
    //手机号

    //亲属联系人2姓名
    //关系
    //手机号

    //首付款
    //贷款金额
    //贷款期限
    //还款人月均总收入
    //个人总资产

    //进口车标志
    //生产厂商
    //汽车品牌
    //款式规格
    //购车年月
    //车牌号码
    //车架号
    //发动机号
    //汽车办理抵押地区
    //汽车权属人姓名
    //申请人与抵押物权属人关系


    private List<ExportLinkManVO> linkManList;

}
