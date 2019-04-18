package come.course.testng;

import org.testng.annotations.Test;

public class ignoretest {
@Test
    public void test1(){
        System.out.println("ignoretest1执行");
    }

    @Test(enabled = false)
    public void test2() {
        System.out.println("ignoretest2执行");
    }

    @Test(enabled = true)
    public void test3() {
        System.out.println("ignoretest3执行");
    }
    }
