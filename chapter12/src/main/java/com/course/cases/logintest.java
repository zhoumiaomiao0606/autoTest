package com.course.cases;

import com.course.config.testconfig;
import com.course.model.interfacename;
import com.course.model.logincase;
import com.course.utils.configfile;
import com.course.utils.databaseutil;
import org.apache.ibatis.session.SqlSession;
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

    }
    @Test(groups = "loginfalse",description = "登录失败")
    public void  loginfalse() throws IOException {
        SqlSession sqlSession = databaseutil.getSqlSession();
        logincase loginCase= sqlSession.selectOne("logincase","1");
        System.out.println(loginCase);
        System.out.println(testconfig.loginurl);}
}
