package com.course.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class databaseutil {
    public static SqlSession getSqlSession()throws IOException {
        Reader reader = Resources.getResourceAsReader("databaseconfig.xml");
        SqlSessionFactory factory =new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession=  factory.openSession();
        return  sqlSession;

    }
}
