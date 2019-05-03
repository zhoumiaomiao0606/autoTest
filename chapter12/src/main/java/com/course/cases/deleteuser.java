package com.course.cases;

import com.course.utils.databaseutil;
import org.apache.ibatis.session.SqlSession;
import org.testng.annotations.Test;
import  com.course.model.updateuserinforcase1;
import java.io.IOException;

public class deleteuser {


    @Test(dependsOnGroups = "logintrue",description = "删除用户信息")
    public void deleteuserinfo()throws IOException {
        SqlSession sqlSession = databaseutil.getSqlSession();
      updateuserinforcase1   deleteuserinfo = sqlSession.selectOne("deleteuserinfo", "1");
        System.out.println(deleteuserinfo);

    }
}
