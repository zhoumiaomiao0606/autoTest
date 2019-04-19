package come.course.testng;

import org.testng.annotations.*;

public class BasicAnation {

@Test//基本注解，用来把方法标记到测试的一部分
    public void testcase1(){
      System.out.println("这是一个测试");
    }

    @Test
    public void test2(){
        System.out.println("这是第二个测试");
    }
    @BeforeMethod
    public void testbefor(){

        System.out.println("这是测试之前运行的标签");
    }
    @AfterMethod
    public void testafter(){
        System.out.println("这是测试之后运行的方法");
    }


    @BeforeClass
    public void beforclass(){

        System.out.println("这是再类之前运行的");
    }
    @AfterClass
    public void afterclass() {
        System.out.println("这是类之后运行的");
    }
@BeforeSuite
public void beforsuit(){
    System.out.println("beforesuit使用");
        }
        @AfterSuite
        public void aftersuit(){
            System.out.println("aftersuit使用");
        }
    }

