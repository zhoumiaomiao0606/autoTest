package com.course.cases;

import com.course.utils.databaseutil;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.Test;
import  com.course.model.updateuserinforcase1;
import java.io.IOException;

public class updateuserinfocase {
    @Test(dependsOnGroups = "logintrue",description = "更改用户信息")
    public void updateuserinfocase()throws IOException {
        SqlSession sqlSession = databaseutil.getSqlSession();
        updateuserinforcase1 updateuserinfocase = sqlSession.selectOne("updateuserinfocase", "1");
        System.out.println(updateuserinfocase);

    }

}
