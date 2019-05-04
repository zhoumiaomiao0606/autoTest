package com.course.cases;

import com.course.config.testconfig;
import com.course.model.User;
import com.course.model.addusercase;
import com.course.utils.databaseutil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;

public class addusercase1 {

    @Test(dependsOnGroups = "logintrue",description = "添加用户")
    public  void adduser()throws IOException {
        SqlSession sqlSession= databaseutil.getSqlSession();
      addusercase addusercase= sqlSession.selectOne("addusercase","1");
        System.out.println(addusercase);
//发送请求获取结果
        String    result=getResult(addusercase);
        //验证结果
        User user = sqlSession.selectOne("adduser",addusercase);
        System.out.println(user.toString());
        Assert.assertEquals(addusercase.getExpected(),result);


    }

    private String getResult(addusercase adduser) throws  IOException {
        HttpPost httpPost=new HttpPost(testconfig.adduserurl);
        JSONObject param=new JSONObject();
        param.put("name",adduser.getName());
        param.put("password",adduser.getPassword());
        param.put("age",adduser.getAge());
        param.put("sex",adduser.getSex());
        param.put("permition",adduser.getPermition());
        param.put("isdelete",adduser.getIsdelete());
        httpPost.setHeader("content-type","application/json");
        StringEntity entity=new StringEntity(param.toString(),"utf-8");
        httpPost.setEntity(entity);
        //设置cookie
        testconfig.defaultHttpClient.setCookieStore(testconfig.cookieStore);
       String result=null;
        HttpResponse response=testconfig.defaultHttpClient.execute(httpPost);
        result= EntityUtils.toString(response.getEntity(),"utf-8");
        return  result;
    }
}
