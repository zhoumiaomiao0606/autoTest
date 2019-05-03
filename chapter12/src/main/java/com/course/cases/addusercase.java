package com.course.cases;

import com.course.utils.databaseutil;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.Test;

import java.io.IOException;

public class addusercase {

    @Test(dependsOnGroups = "logintrue",description = "添加用户")
    public  void adduser()throws IOException {
        SqlSession sqlSession= databaseutil.getSqlSession();
      addusercase addusercase= sqlSession.selectOne("addusercase","1");
        System.out.println(addusercase);




    }
}
