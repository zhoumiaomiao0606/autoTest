package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class QueryCarTypeByVIN
{
   private String id;  //Int  否  精真估车型 ID。
   private String displacement;  //Double 是  车型排量
   private String environmentProtectionStanda; //rd
   private String makeId;  //Int 否  精真估品牌 ID。
   private String makeName;  //String  否  精真估品牌名称。
   private String manufacturerId; // Int  否  精真估厂商 ID。
   private String manufacturerName; // String  否  精真估厂商名称。
   private String modelId; // Int  否  精真估车系 ID。
   private String modelName;//  String  否  精真估车系名称。
   private String name; //String 否 精真估车型名称。
   private String transmissionType;// String 是 车型变速方式。
   private String year; //Int 是 车型年款
   private String manufacturerGuidePrice; //String 是 新车指导价
   private String makeInfo; //String  否 品牌型号
   private String styleColor; //String 否 车型颜色
   private String dateOfProduction; //String 否 出厂日期 yyyy-MM
}
