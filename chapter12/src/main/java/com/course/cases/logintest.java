package com.course.cases;

import com.course.config.testconfig;
import com.course.model.interfacename;
import com.course.model.logincase;
import com.course.utils.configfile;
import com.course.utils.databaseutil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class logintest {
    @BeforeTest(groups = "logintrue",description ="测试准备工作")
    public  void befortest(){

        testconfig.adduserurl= configfile.getUrl(interfacename.ADDUSERINFO);
        testconfig.getuserinfourl=configfile.getUrl(interfacename.GETUSERINFO);
        testconfig.getuserlisturl=configfile.getUrl(interfacename.GETUSERLIST);
        testconfig.adduserurl=configfile.getUrl(interfacename.ADDUSERINFO);
        testconfig.loginurl=configfile.getUrl(interfacename.LOGIN);
//        testconfig.defaultHttpClient=new DefaultHttpClient();


    }

@Test(groups = "logintrue",description = "用户登录成功接口")
    public void  logintrue() throws IOException {
    SqlSession sqlSession = databaseutil.getSqlSession();
   logincase loginCase= sqlSession.selectOne("logincase","1");

    System.out.println(loginCase);
    System.out.println(testconfig.loginurl);

    //发送请求
    String result=getresult(loginCase);
     logincase login= sqlSession.selectOne("login","logincase");
    //验证结果
    System.out.println(login.toString());
    Assert.assertEquals(loginCase.getExpected(),result);

    }

    private String getresult(logincase loginCase) throws  IOException {
        HttpPost httpPost=new HttpPost(testconfig.loginurl);
        JSONObject param=new JSONObject();
        param.put("name",loginCase.getName());
        param.put("password",loginCase.getPassword());
        httpPost.setHeader("content-type","application/json");
        StringEntity entity=new StringEntity(param.toString(),"utf-8");
        httpPost.setEntity(entity);
        String result=null;
        HttpResponse response=testconfig.defaultHttpClient.execute(httpPost);
        result= EntityUtils.toString(response.getEntity(),"utf-8");
        testconfig.cookieStore=testconfig.defaultHttpClient.getCookieStore();
        return result;
    }

    @Test(groups = "loginfalse",description = "登录失败")
    public void  loginfalse() throws IOException {
        SqlSession sqlSession = databaseutil.getSqlSession();
        logincase loginCase= sqlSession.selectOne("logincase","1");
        System.out.println(loginCase);
        System.out.println(testconfig.loginurl);}
}
