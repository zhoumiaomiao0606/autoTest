package com.muke;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import org.apache.http.client.methods.HttpGet;

import org.apache.http.impl.client.DefaultHttpClient;


import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;

import java.io.IOException;

public class demo {
    @Test
    public void testhttpdemo() throws IOException {

        HttpGet httpGet=new HttpGet("http://www.baidu.com");
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response=httpclient.execute(httpGet);

        String result=   EntityUtils.toString(response.getEntity());
        System.out.println(result);





    }
}
