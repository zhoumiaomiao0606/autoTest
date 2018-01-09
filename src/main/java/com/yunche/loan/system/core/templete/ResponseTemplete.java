package com.yunche.loan.system.core.templete;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.yunche.loan.system.core.constant.JsonSymbolicFinalConstant;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by WangGang on 2017/7/4 0004.
 * E-mail userbean@outlook.com
 * The final interpretation of this procedure is owned by the author
 */
public class ResponseTemplete {

    private String service_code;

    private String service_message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    public String getService_code() {
        if(StringUtils.isBlank(service_code)){
            service_code = JsonSymbolicFinalConstant.DEFAULT_SUCCESS_SERVICE_CODE;
        }
        return service_code;
    }

    public void setService_code(String service_code) {
        this.service_code = service_code;
    }

    public String getService_message() {
        if(StringUtils.isBlank(service_message)){
            service_message = JsonSymbolicFinalConstant.DEFAULT_SUCCESS_SERVICE_MESSAGE;
        }
        return service_message;
    }

    public void setService_message(String service_message) {
        this.service_message = service_message;
    }

    public Object getData() {

        if(data == null){
            return null;
        }
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ResponseTemplete(Object data){
        this.data = data;
    }

    public ResponseTemplete(){

    }

}
