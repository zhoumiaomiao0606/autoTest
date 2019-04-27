package come.course.testng.groups;

import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

public class grouponmethod {

    @Test(groups = "servers")
    public void test1() {
        System.out.println("这是服务端测试1");
    }

    @Test(groups = "servers")
    public void test2() {
        System.out.println("这是服务端测试2");
    }

    @Test(groups = "client")
    public void test3() {
        System.out.println("这是客户端测试3");
    }

    @Test(groups = "client")
    public void test4() {
        System.out.println("这是客户端测试4");
    }

    @BeforeGroups("servers")
    public void beforgroups() {

        System.out.println("这是服务端运行之前");
    }

    @AfterGroups("servers")
    public void aftergroup() {

        System.out.println("这是服务端运行之后");
    }
}
