package com.yunche.loan.config.feign.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.response.ApplyCreditResponse;
import feign.FeignException;
import feign.Logger;
import feign.Response;
import feign.Util;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import static com.yunche.loan.config.constant.BaseExceptionEnum.EC00000200;

@Configuration
public class FeignConfig {
    private Set<Class> clazzs = Sets.newHashSet();
    public FeignConfig(){
        clazzs.add(ApplyCreditResponse.class);
    }


    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public ErrorDecoder basicErrorDecoder(){
        return new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                    throw new BizException(methodKey+"接口请求失败:");
            }
        };
    }

    @Bean
    public Decoder basicDecoder() {
        return new Decoder() {
            @Override
            public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {

                if (response.body() == null) {
                    throw new BizException("报文为空,无法解析");
                }
                String result = Util.toString(response.body().asReader());
                if (StringUtils.isBlank(result)) {
                    throw new BizException("报文为空,无法解析");
                }
                Map map = formatMap(result);
                checkMain(map);
                checkData(map);

                for(Class value: clazzs){
                    if (value.equals(type)) {
                        return formatJson(map, value);
                    }
                }
                throw new BizException("找不到指定的解析方法");
            }
        };
    }


    private <T>T formatJson(Map map,Class<T> clazz){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String result =  objectMapper.writeValueAsString(map.get("data"));
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(result,clazz);
        }catch(Exception e){
            throw new BizException("json解析失败");
        }
    }


    private void checkMain(Map map){

        if(map !=null){
            if(map.get("success") == null){
                throw new BizException(map.get("msg")==null?"接口请求失败,未知错误":map.get("msg").toString());
            }

            if(!(boolean)map.get("success")){
                throw new BizException(map.get("msg")==null?"接口请求失败,未知错误":map.get("msg").toString());
            }

            if(map.get("code") == null){
                throw new BizException(map.get("msg")==null?"接口请求失败,未知错误":map.get("msg").toString());
            }

            if(!map.get("code").toString().equals(EC00000200.getCode())){
                throw new BizException(map.get("msg")==null?"报文为空,无法解析":map.get("msg").toString());
            }
        }
    }

    private void checkData(Map map){
        Object dataMap = map.get("data");
        if(dataMap == null){
            throw new BizException("报文为空,无法解析");
        }

        if(StringUtils.isBlank(dataMap.toString())){
            throw new BizException("报文为空,无法解析");
        }
    }

    private Map formatMap(String value){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, Map.class);
        }catch(Exception e){
            throw new BizException("解析报文失败");
        }
    }

}
