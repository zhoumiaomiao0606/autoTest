package com.yunche.loan.estage;

import com.alibaba.fastjson.JSONObject;
import com.yunche.loan.estage.util.HttpClientUtils;
import com.yunche.loan.estage.util.RSASignature;
import com.yunche.loan.estage.util.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description:
 * author: yu.hb
 * Date: 2019-03-07
 */
@Slf4j
public class DefaultEstageProcessor implements EstageProcessor{

    @Autowired
    private EstageProperties estageProperties;

    @Override
    public JSONObject processInternal(Long orderId) {
        // 组装pub参数域
        JSONObject pub = assemblyPub(orderId);
        // 组装具体业务接口参数域
        JSONObject req = assemblyReq(orderId);

        JSONObject data = encodeData(merge(pub,req));

        String result = HttpClientUtils.post(estageProperties.getSystemUrl(), data.toString());
        log.info("请求e分期业务接口【{}】，参数：{}，返回：{}",buildBusiCode(),data.toString(),result);
        return JSONObject.parseObject(result);
    }


    public JSONObject assemblyReq(Long orderId) {
        return null;
    }

    private JSONObject encodeData(JSONObject params) {
        JSONObject data = new JSONObject();
        data.put("assurerNo",estageProperties.getAssurerNo());
        data.put("data",RSAUtil.encrypt(params.toString(),estageProperties.getPublicKey()));
        data.put("sign", RSASignature.sign(params.toString(),estageProperties.getSignPrivateKey()));
        data.put("bankType","ICBC");
        data.put("busiCode",buildBusiCode());
        data.put("platNo",estageProperties.getPlatNo());
        data.put("bankCode",estageProperties.getBankCode());
        return data;
    }


    private JSONObject merge(JSONObject pub, JSONObject req) {
        JSONObject params = new JSONObject();
        params.put("pub",pub);
        params.put("req",req);
        return params;
    }

    private JSONObject assemblyPub(Long orderId) {
        JSONObject pub = new JSONObject();
        pub.put("assurerNo",estageProperties.getAssurerNo());
        pub.put("platNo",estageProperties.getPlatNo());
        pub.put("bankCode",estageProperties.getBankCode());
        pub.put("orderNo",orderId);
        pub.put("productType",estageProperties.getProductType());
        pub.put("busiCode",buildBusiCode());
        return pub;
    }

    public String buildBusiCode() {
        return StringUtils.EMPTY;
    }
}
