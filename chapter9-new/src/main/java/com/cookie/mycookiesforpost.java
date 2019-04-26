package com.cookie;

import netscape.javascript.JSObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class mycookiesforpost {
    private String url;
    private ResourceBundle bundle;
    private CookieStore store;

    @BeforeTest
    public  void befortest(){
       this.bundle=ResourceBundle.getBundle("application", Locale.CHINA);
       this.url=bundle.getString("test.url");
            }
            @Test
    public void testgetcookies()throws IOException {
        String uri=bundle.getString("getookies.uri");
        String testurl=this.url+uri;
                HttpGet httpGet=new HttpGet(testurl);
                DefaultHttpClient httpClient= new DefaultHttpClient();
            HttpResponse response= httpClient.execute(httpGet);
          String result=  EntityUtils.toString(response.getEntity());
                System.out.println(result);
                this.store=httpClient.getCookieStore();
            List<Cookie> cookies=this.store.getCookies();

                for(Cookie  cookie: cookies){
                    String name=cookie.getName();
                    String value=cookie.getValue();
                    System.out.println("name:"+name+";   value:"+value);

                }
            }
            @Test(dependsOnMethods = {"testgetcookies"})
    public void testwithcookiepost()throws UnsupportedEncodingException,IOException{
        String uri=bundle.getString("test.postwithcookies");
        String testurl=this.url+uri;
                HttpPost httpPost=new HttpPost(testurl);
                DefaultHttpClient httpClient=new DefaultHttpClient();
                JSONObject json=new JSONObject();
                json.put("name","huhansan");
                json.put("age","18");
                httpPost.setHeader("content-type","application/json");
                String  sjson=json.toString();
                StringEntity entity=new StringEntity(sjson,"utf-8");
                httpPost.setEntity(entity);
                httpClient.setCookieStore(this.store);
                HttpResponse response=httpClient.execute(httpPost);
                String result=EntityUtils.toString(response.getEntity());
               JSONObject resultjson= new JSONObject(result);

                System.out.println(result);
                Assert.assertEquals("success",resultjson.get("huhansan"));
                Assert.assertEquals("1",resultjson.get("status"));

            }



}
