package com.course.cases;

import com.course.config.testconfig;
import com.course.model.User;
import com.course.model.getuserlistcase;
import com.course.utils.databaseutil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class getuserinfolistcase1 {
@Test(dependsOnGroups = "logintrue",description = "查询出数据为男的数据")
    public void getuserlistinfo()throws IOException {
    SqlSession sqlSession = databaseutil.getSqlSession();
    getuserlistcase getuserlistcase1=sqlSession.selectOne("getuserlistcase","1");
    System.out.println(getuserlistcase1);
    JSONArray result=getresult(getuserlistcase1);
    //验证结果
    Assert.assertEquals(getuserlistcase1.getExpected(),result);
    List<User> userLsist =sqlSession.selectOne("getuserlist","getuserlistcase1");
    for(User u:userLsist){
        System.out.println("获取的user"+u.toString());


    }
JSONArray userlistjson=new JSONArray(userLsist);
    Assert.assertEquals(userlistjson.length(),result.length());
    }

    private JSONArray getresult(getuserlistcase getuserlistcase) throws  IOException {
        HttpPost httpPost=new HttpPost(testconfig.adduserurl);
        JSONObject param=new JSONObject();
        param.put("name",getuserlistcase.getName());
        param.put("age",getuserlistcase.getAge());
        param.put("sex",getuserlistcase.getSex());

        httpPost.setHeader("content-type","application/json");
        StringEntity entity=new StringEntity(param.toString(),"utf-8");
        httpPost.setEntity(entity);
        //设置cookie
        testconfig.defaultHttpClient.setCookieStore(testconfig.cookieStore);
        String result=null;
        HttpResponse response=testconfig.defaultHttpClient.execute(httpPost);
        result= EntityUtils.toString(response.getEntity(),"utf-8");
        JSONArray jsonArray=new JSONArray(result);
        return  jsonArray;
    }
}
