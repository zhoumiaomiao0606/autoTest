package com.yunche.loan.domain.vo;

import lombok.Data;

@Data
public class FirstCarSiteWebVO
{
    private String BrandType;//品牌类型

    private String Model_Name;//车系：欧力威

    private String Brand;//厂商：长安   --？？？

    private String VersionYear;//车辆年款：201304  --- model

    private String ProductionDate;//车辆生产时间：2015-01-12  --- model

    private String EngineNo;//发动机号：E3300998766   --detail

    private String EngineModel;//发动机型号：JL473qa  --engine

    private String Name;//车辆型号：SC6388AV4     --- model -款式名称 --？

   /* private String BrandType;//外型尺寸：3800/1700/1680*/

    private String Weight;//整备质量（kg）：1095   --- body

    private String Displacement;//排量（L）：1.2  -- model

    private String Emission;//排放标准：国IV   ---basic

    private String Doors;//车门数：5    ---body

    private String Seating;//乘员数：5   -- body

    private String TransmissionType;//变速器形式：5挡 手动  --model

    private String DrivingMethod;//驱动方式：前驱    --chassis

    private String CarBodyForm;//车体形式：无   --body

    private String FuelType;//燃料类型：无铅汽油92#   ---engine

    private String Color;//车身颜色：闪光梦幻棕   --detail

}
