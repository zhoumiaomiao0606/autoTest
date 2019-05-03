package com.course.cases;

import com.course.utils.databaseutil;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.Test;

import java.io.IOException;

public class getuserinfocase {
    @Test(dependsOnGroups = "logintrue",description = "查询id为1的数据")
    public void getuserinfocase()throws IOException {
        SqlSession sqlSession = databaseutil.getSqlSession();
        getuserinfocase getuserinfocase = sqlSession.selectOne("getuserinfocase", "1");
        System.out.println(getuserinfocase);
    }

    }
