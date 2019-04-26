package com.cookie;


import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;

import org.apache.http.client.methods.HttpGet;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class mycookiesforget {
    private  CookieStore store;
private  String url;
private ResourceBundle bundle;
@BeforeTest
    public void befortest(){

    bundle=ResourceBundle.getBundle("application",Locale.CHINA);
    url=bundle.getString("test.url");
}
@Test
    public void testgetcookies()throws IOException {

    String result;
    String uri=bundle.getString("getookies.uri");
    String testurl=this.url + uri;
    HttpGet httpGet= new HttpGet(testurl);
    DefaultHttpClient httpClient =new DefaultHttpClient();
  HttpResponse httpResponse=  httpClient.execute(httpGet);
  result= EntityUtils.toString(httpResponse.getEntity(),"utf-8");
    System.out.println(result);
this.store=httpClient.getCookieStore();
    List<Cookie> cookies=this.store.getCookies();
    for(Cookie cookie :cookies){
        String name=cookie.getName();
        String value=cookie.getValue();
        System.out.println("获取的cookie名字是："+name+"     cookie 的 valnue:" +value);

    }
}
@Test(dependsOnMethods ={"testgetcookies"})
public void test()throws IOException{
    String uri=bundle.getString("test.getwithcookies");
    String testurl= this.url+uri;
    HttpGet httpGet= new HttpGet(testurl);
    DefaultHttpClient httpClient =new DefaultHttpClient();
    httpClient.setCookieStore(this.store);
    HttpResponse httpResponse=httpClient.execute(httpGet);
//获取相应状态码
    int statues=httpResponse.getStatusLine().getStatusCode();
    System.out.println("statues:"+ statues);
    Assert.assertEquals(statues,200);

}


}
