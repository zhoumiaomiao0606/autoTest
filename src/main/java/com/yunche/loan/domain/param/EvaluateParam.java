package com.yunche.loan.domain.param;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;


@Data
public class EvaluateParam
{
    @JSONField(name="TrimId")
    private String trimId;//车型 ID

    @JSONField(name="Mileage")
    private String mileage;//里程

    @JSONField(name="BuyCarDate")
    private String buyCarDate;//上牌时间

    private String carCard;//车牌号

    private String cityId;//城市

    @Override
    public String toString() {
        return JSON.toJSONString(this, new SerializerFeature[]{SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNullNumberAsZero, SerializerFeature.WriteNullBooleanAsFalse,
                SerializerFeature.UseISO8601DateFormat });

    }
}
