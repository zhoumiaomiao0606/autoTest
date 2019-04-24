package com.yunche.loan;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * 单元测试继承该类即可
 */
@RunWith(SpringRunner.class)    // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = App.class)    // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration    //由于是Web项目，Junit需要模拟ServletContext，因此我们需要给我们的测试类加上@WebAppConfiguration。
@Transactional
@Rollback
public  class BaseTest {
    @Test
    public void test(){
        System.out.println("这是一个测试");
    }





}



