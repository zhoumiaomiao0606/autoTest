package come.course.testng;

import org.testng.annotations.Test;

public class dependtest {
    @Test
    public void test1() {

        System.out.println("test1运行");
        throw new RuntimeException();
    }

    @Test(dependsOnMethods = "test1")
    public void test2() {
        System.out.println("test2运行");

    }
}
