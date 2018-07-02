package com.yunche.loan.config.feign.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunche.loan.config.constant.IConstant;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.response.ApplyCreditResponse;
import com.yunche.loan.config.feign.response.base.BasicResponse;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import feign.*;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.yunche.loan.config.constant.BaseExceptionEnum.EC00000200;

@Configuration
@DependsOn
public class FeignConfig {
    @Resource
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //获取请求body中的内容
                String req = new String(template.body());
                if(StringUtils.isBlank(req)){
                    throw new BizException("请求参数为空");
                }
                Map reqMap = json2Map(req);
                Object orderno  =  reqMap.get("orderno");
                if(orderno == null){
                    throw new BizException("orderno 参数为空");
                }
                if(StringUtils.isBlank(orderno.toString())){
                    throw new BizException("orderno 参数为空");
                }
                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(orderno.toString()),new Byte("0"));
                if(loanOrderDO == null){
                    throw new BizException("此订单不存在");
                }
                Long customerId = loanOrderDO.getLoanCustomerId();
                if(customerId == null){
                    throw new BizException("此客户id不存在");
                }

                Object cmpseq  =  reqMap.get("cmpseq");
                if(cmpseq == null){
                    throw new BizException("cmpseq 参数为空");
                }
                if(StringUtils.isBlank(cmpseq.toString())){
                    throw new BizException("cmpseq 参数为空");
                }


                String transCode = template.url().substring(template.url().lastIndexOf("/")+1,template.url().length()-1);
                template.header("transCode",transCode);
                template.header("customerId",customerId.toString());
                template.header("serialNo",cmpseq.toString());

            }
        };
    }


    @Bean
    public ErrorDecoder basicErrorDecoder(){
        //feign 将 404 和 非200的状态全部交给errorDecoder
        return new ErrorDecoder() {
            @Override
            public Exception decode(String methodKey, Response response) {
                    Object transCode = response.request().headers().get("transCode");
                    Object customerId = response.request().headers().get("customerId");
                    Object serialNo = response.request().headers().get("serialNo");

                    if(transCode == null){
                        throw new BizException("transCode 参数为空");
                    }
                    if(customerId == null){
                        throw new BizException("customerId 参数为空");
                    }
                    if(serialNo == null){
                        throw new BizException("serialNo 参数为空");
                    }

                    List transCodeList = (List) transCode;

                    List customerIdList = (List) customerId;

                    List serialNoList = (List) serialNo;

                    if(CollectionUtils.isEmpty(transCodeList)){
                        throw new BizException("transCode 参数为空");
                    }

                    if(CollectionUtils.isEmpty(customerIdList)){
                        throw new BizException("customerId 参数为空");
                    }

                    if(CollectionUtils.isEmpty(serialNoList)){
                        throw new BizException("serialNo 参数为空");
                    }

                    BankInterfaceSerialDO V = bankInterfaceSerialDOMapper.selectByPrimaryKey(serialNoList.get(0).toString());
                    BankInterfaceSerialDO DO = new BankInterfaceSerialDO();
                    DO.setSerialNo(serialNoList.get(0).toString());
                    DO.setCustomerId(Long.valueOf(customerIdList.get(0).toString()));
                    DO.setTransCode(transCodeList.get(0).toString());
                    DO.setApiStatus(response.status());
                    DO.setApiMsg(String.valueOf(response.status()));
                    if(V!=null){
                        bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(DO);
                    }else {
                        bankInterfaceSerialDOMapper.insertSelective(DO);
                    }

                    throw new BizException(methodKey+"接口请求失败");
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

                Map map = json2Map(result);
                checkMain(map);
                checkData(map);

                Object transCode = response.request().headers().get("transCode");
                Object customerId = response.request().headers().get("customerId");
                Object serialNo = response.request().headers().get("serialNo");

                if(transCode == null){
                    throw new BizException("transCode 参数为空");
                }
                if(customerId == null){
                    throw new BizException("customerId 参数为空");
                }
                if(serialNo == null){
                    throw new BizException("serialNo 参数为空");
                }

                List transCodeList = (List) transCode;

                List customerIdList = (List) customerId;

                List serialNoList = (List) serialNo;

                if(CollectionUtils.isEmpty(transCodeList)){
                    throw new BizException("transCode 参数为空");
                }

                if(CollectionUtils.isEmpty(customerIdList)){
                    throw new BizException("customerId 参数为空");
                }

                if(CollectionUtils.isEmpty(serialNoList)){
                    throw new BizException("serialNo 参数为空");
                }
                Class clazz = null;
                try {
                    clazz = Class.forName(type.getTypeName());
                } catch (ClassNotFoundException e) {
                    throw new BizException("没有找到返回类型,无法解析");
                }

                if(clazz == null){
                    throw new BizException("没有找到返回类型,无法解析");
                }

                if(!clazz.getGenericSuperclass().equals(BasicResponse.class)){
                    throw new BizException("返回类型不匹配,无法解析");
                }


                if(map.get("data")!=null){
                    Object obj =  map2Obj(map,clazz);
                    String icbcApiRetcode = ((BasicResponse) obj).getIcbcApiRetcode();
                    String icbcApiRetmsg = ((BasicResponse) obj).getIcbcApiRetmsg();
                    String returnCode = ((BasicResponse) obj).getReturnCode();
                    String returnMsg = ((BasicResponse) obj).getReturnMsg();
                    BankInterfaceSerialDO V = bankInterfaceSerialDOMapper.selectByPrimaryKey(serialNoList.get(0).toString());
                    if(IConstant.API_SUCCESS.equals(icbcApiRetcode) && IConstant.SUCCESS.equals(returnCode)){
                        BankInterfaceSerialDO DO = new BankInterfaceSerialDO();
                        DO.setSerialNo(serialNoList.get(0).toString());
                        DO.setCustomerId(Long.valueOf(customerIdList.get(0).toString()));
                        DO.setTransCode(transCodeList.get(0).toString());
                        DO.setStatus(IDict.K_JYZT.PROCESS);
                        DO.setApiStatus(200);
                        DO.setApiMsg("icbcApiRetmsg:"+icbcApiRetmsg+";"+"returnMsg:"+returnMsg);
                        if(V!=null){
                            bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(DO);
                        }else {
                            bankInterfaceSerialDOMapper.insertSelective(DO);
                        }
                    }else {
                        BankInterfaceSerialDO DO = new BankInterfaceSerialDO();
                        DO.setSerialNo(serialNoList.get(0).toString());
                        DO.setCustomerId(Long.valueOf(customerIdList.get(0).toString()));
                        DO.setTransCode(transCodeList.get(0).toString());
                        DO.setStatus(IDict.K_JYZT.REQ_FAIL);
                        DO.setApiStatus(200);
                        DO.setApiMsg("icbcApiRetmsg:"+icbcApiRetmsg+"&"+"returnMsg:"+returnMsg);
                        if(V!=null){
                            bankInterfaceSerialDOMapper.updateByPrimaryKeySelective(DO);
                        }else {
                            bankInterfaceSerialDOMapper.insertSelective(DO);
                        }
                    }
                    return obj;
                }else {
                    return null;
                }


            }
        };
    }


    private <T extends BasicResponse>T map2Obj(Map map, Class<T> clazz){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String result =  objectMapper.writeValueAsString(map.get("data"));
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(result, clazz);
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

    private Map json2Map(String value){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value, Map.class);
        }catch(Exception e){
            throw new BizException("解析报文失败");
        }
    }

}
