package com.yunche.loan.config.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongan.scorpoin.biz.common.CommonRequest;
import com.zhongan.scorpoin.biz.common.CommonResponse;
import com.zhongan.scorpoin.common.ZhongAnApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class ZhongAnHttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(ZhongAnHttpUtil.class);

    public static Map queryInfo(String name,String tel,String idcard,String appNo,String loanPeriod,String appRole,String ralationship,String applyNo) throws Exception {
        Map returnMap = new HashMap();
        Map returnMap1 = new HashMap();
        // ZhongAnApiClient client = new ZhongAnApiClient(env, appKey, privateKey, version);
        // env：环境参数，在dev、iTest、uat、prd中取值
        // appKey：开发者的appKey。如何获取appKey,请详见“接入流程说明”
        // privateKey：开发者私钥。如何生成开发者私钥,请详见“接入流程说明”
        // version: 服务的版本号，默认为1.0.0，测试期间版本号请与众安开发人员确认，UAT和生产必须为1.0.0
        String appKey = "7e126228ee03c9cd8d3cd868cdb90075";
//        String privatekey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALtzmprQXmbomt9i" +
//                "o0v+m2di/NUrWaPdWEZLGCgM4aZE5zUDeV3Bibmh9Z/Tsr9cDvn/JPqoY1icDpEv" +
//                "ujLe7Fu8uCO5Ao8KwD9XbG210r+oa7PaVHTa4+jl4YVKpRKhj+kkT5bf8Tx8KGX2" +
//                "Dl2ExZphRW7Kk7S9v/CpBbMFpFatAgMBAAECgYAYw5L/NNvj2ILtVAiit4YsSGWC" +
//                "e/GhtlI9JxqP3/PHlX6+ADF+c10Qixb6AGuy4CSSXmSyQKCvSh6ai6WbptuuoJfv" +
//                "aJBUuqVdqCKQSK//wNebf1HD8sat3L2faewLJ4mQOW1cR7cbdIzHTZTa8RxDy0Gr" +
//                "7GJ9yZbPuqN1sANUAQJBAPc75R6EuLCrSct//aCz104nTjpTkAwlM7SlkxOOBRya" +
//                "eyDVdHBGVD4bgOTTqC2Huwpn+TjgNV1Y6WVhxlFu2S0CQQDCGRLE1zZ6bS5kHlYV" +
//                "tCdtCD7dDb96CybedXsg7hlMwhtP5KWvuuYBXq8vB3sm5FBNp9jbX1eEOKBxYGGw" +
//                "zuOBAkAHPjn4Kus+QcZnr9g+XQZxw7UHAGu3718Ua8VjTUXZEK2KyLYgk+7j4upj" +
//                "yc+jhdZ095bVk7v8gB5WWgb1W8oBAkA8CSNcDTFFluXFg/ieh/W17Nn859a0+iQQ" +
//                "pQfrvJnIuzVVdeSlwUqJW+8VvduiwPXxvxv9ZrUcKaO+zdAJr0SBAkAcupbyP51S" +
//                "k/wbdfM7mCcb10o0EU3XbKumbzp4QDNuqdyuC7G9QG1kyLl6CYnm6WHNBFPcja58" +
//                "QVjDO/WUFUnw";
        String privatekey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJyYXf+gpY/X/KJ/LuCbR548UXZj5aHpJKm+EeOq0aaq+dlBvgEGy368cmZ2OOTn4hzsXea6oEaEqfw6tRrPumzBd59eV6/8VPIj5dXUc2DrGX0gJXoX5wNDBqUScDn826/oLA65Gz26s+5JHYqIzQtvKhegJ6GC9E53rjTwwgrLAgMBAAECgYAiKB4AvzK9wHfrneBEv7oMas2+DCYvfeIwYDQYu87FEvAykmN2Z9wFReeSL+FTFL0+X9RPmo+RMii9yrRsJ7la3f09wLgLujC7gKCdhZzp+oQ5WDi0t5DUNiVTbhywcQQkeSA48BmnXPIQ8XCdNCzJ7DRlKVzSzG/zla7Okwcu0QJBANaxhnLKC2Hub/h6/vbDLBKykj/eej5yAwa22VzvqsKwGZPvoVKbfL7KQFLiQbOeHWZcdtF3sBAFfpyvErblfUMCQQC6uUeSdnq/P2Jbn+GWyAOSW9pEXcMU3gBqRdimHeR1WNCiRKn/Gn3e5MG5Pl8Bz6M/y0sjW7eeQfXGJO8C1V/ZAkEAnC3aa4jVTPGCXNVEwsfqONPUlkfGz8RqtSiw6O2kYCpxAPAygACCd9xzfJgBSaP9KSicevbBinYky+CEEa7SNwJBAJArSDiso/+QB/hojLxnuGJD61XH8zzkX/ut7CXuhJuaNJRlYcAnCzKS+4R0xNRYJlq2M1Ccmzxk/0e68pQEfZkCQCOIdS1YprMHBIdV8RT/6k/qAvyWSZfScsUShyiBjFD8qoA5HUC21SsVxpZdDuorlhDj+2WGB6xPgu+8VoyFUx4=";
        ZhongAnApiClient client = new ZhongAnApiClient("prd", appKey, privatekey, "1.0.0");

        //由于开发环境众安网关地址不定，可在ZhongAnApiClient构造方法中传入url。注意，该方法传入的url只在开发环境有效，其他环境中ZhongAnApiClient只会取默认的url。
        // String url ="http://120.27.167.36:8080/Gateway.do";
        // ZhongAnApiClient client = new ZhongAnApiClient("dev",url, appKey, privatekey, "1.0.0");

        //接口名称
        String serviceName = "zhongan.xdecision.apply";
        CommonRequest request = new CommonRequest(serviceName);

        JSONObject map = new JSONObject();
        map.put("userName", name);
        map.put("userMobile", tel);
        map.put("certNo", idcard);
        map.put("loanPeriod", loanPeriod);
        map.put("appNo", appNo);
        map.put("appRole", appRole);
        map.put("relationship", ralationship);
        //业务参数
        JSONObject param = new JSONObject();
        param.put("merchKey","D63EB54EDE6021213B8D06B11D348C5A");
        param.put("applyNo",applyNo);
        param.put("productCode","YCJRCD");
        param.put("ruleCode","YCJRCD615010");
        param.put("jsonData", map.toJSONString());
        request.setParams(param);
        //发起请求
        CommonResponse response = (CommonResponse) client.call(request);
        String retMsg = response.getBizContent();
        logger.info("众安大数据返回信息:"+retMsg);
        if(retMsg !=null&&!"".equals(retMsg)){
            returnMap = (Map)JSON.parse(retMsg);
            String param1 = (String)returnMap.get("param");
            returnMap1 = (Map)JSON.parse(param1);
        }
        return returnMap1;
    }
}
