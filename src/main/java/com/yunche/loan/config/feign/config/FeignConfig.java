package com.yunche.loan.config.feign.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.response.ApplyCreditResponse;
import com.yunche.loan.config.feign.response.base.BasicResponse;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
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
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import static com.yunche.loan.config.constant.BaseExceptionEnum.EC00000200;

@Configuration
@DependsOn
public class FeignConfig {
    private Set<Class> clazzs = Sets.newHashSet();
    public FeignConfig(){
        clazzs.add(ApplyCreditResponse.class);
    }

    @Resource
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public ErrorDecoder basicErrorDecoder(){
        //feign 将 404 和 非200的状态全部交给errorDecoder
        return new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                    //BankInterfaceSerialDO DO = new BankInterfaceSerialDO();
                    //DO.setSerialNo(GeneratorIDUtil.execute());
                    //DO.setOrderId();
                    //DO.setTransCode();
                    //DO.setApiStatus(IDict.K_JYZT.REQ_FAIL);
                    //DO.setApiMsg();
                    //bankInterfaceSerialDOMapper.insertSelective();
                    throw new BizException(methodKey+"接口请求失败:");
            }
        };
    }

    @Bean
    public <T extends BasicResponse>Decoder basicDecoder() {
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
                        if(!value.getSuperclass().equals(BasicResponse.class)){
                            throw new BizException("不符合规范的返回类型,无法解析");
                        }
                        Object obj =  formatJson(map,value);
                        ((BasicResponse) obj).getIcbcApiRetcode();
                        ((BasicResponse) obj).getIcbcApiRetmsg();




                    }
                }
                throw new BizException("找不到指定的解析方法");
            }
        };
    }


    private <T extends BasicResponse>T formatJson(Map map,Class<T> clazz){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String result =  objectMapper.writeValueAsString(map.get("data"));
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(result,clazz);
        }catch(Exception e){
            throw new BizException("json解析失败");
        }
    }

    private void processSerial(){

    };

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
