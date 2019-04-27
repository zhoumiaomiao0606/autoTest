package come.course.testng.multithead;

import org.testng.annotations.Test;

public class multithredtest {

    @Test(invocationCount = 10, threadPoolSize = 3)
    public void multithread() {


        System.out.println(1);
        System.out.printf("thead:%s%n", Thread.currentThread());
    }
}
