package com.course.cases;

import com.course.utils.databaseutil;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.Test;

import java.io.IOException;

public class getuserinfolistcase {
@Test(dependsOnGroups = "logintrue",description = "查询出数据为男的数据")
    public void getuserlistinfo()throws IOException {
    SqlSession sqlSession = databaseutil.getSqlSession();
    getuserinfolistcase getuserlistcase=sqlSession.selectOne("getuserlistcase","1");
    System.out.println(getuserlistcase);

    }
}
