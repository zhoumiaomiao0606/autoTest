package come.course.testng;

import org.omg.CORBA.TIMEOUT;
import org.testng.annotations.Test;

public class timeout {
@Test(timeOut = 3000)//单位是毫秒值
    public void testtimeout() throws  InterruptedException{
   Thread.sleep(2000);

    }

    @Test(timeOut = 3000)//单位是毫秒值
    public void testtimeout1() throws  InterruptedException{
        Thread.sleep(4000);

    }
}
