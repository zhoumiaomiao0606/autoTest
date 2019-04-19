package com.test.demo;

import org.testng.Assert;
import org.testng.annotations.Test;

public class testmethod {


    @Test
    public void test1(){

        Assert.assertEquals(1,2);
    }
    @Test
    public void test2(){

        Assert.assertEquals(1,1);
    }

    @Test
    public void test3(){

        System.out.println("这是自己的日志");
        throw  new RuntimeException("这是我自己弄的异常");
    }

    @Test(timeOut = 1000)
    public void test4() throws InterruptedException{

        Thread.sleep(2000);
    }
}
