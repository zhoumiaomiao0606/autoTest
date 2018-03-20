/*
package com.yunche.loan;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.PushPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main(String[] args){
        */
/**
         * 客户端 给所有平台的一个或者一组用户发送信息
         *//*



    }


    public static void sendAlias(String message, List<String> aliasList)
    {
        JPushClient jpushClient = new JPushClient("b6f47f289ee6dae11a529fcd",
                "21742e62e41164461c4af259");
        Map<String, String> extras = new HashMap<String, String>();
        // 添加附加信息
        extras.put("extMessage", "我是额外的消息--sendAlias");

        PushPayload payload = allPlatformAndAlias(message, extras, aliasList);
        try
        {
            PushResult result = jpushClient.sendPush(payload);
            System.out.println(result);
        }
        catch (APIConnectionException e)
        {
            System.out.println(e);
        }
        catch (APIRequestException e)
        {
            System.out.println(e);
            System.out.println("Error response from JPush server. Should review and fix it. " + e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Msg ID: " + e.getMsgId());
        }
    }


}
*/
