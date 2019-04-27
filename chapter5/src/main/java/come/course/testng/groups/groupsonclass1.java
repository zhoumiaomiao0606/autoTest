package come.course.testng.groups;

import org.testng.annotations.Test;

@Test(groups = "teacher")
public class groupsonclass1 {

    public void teacher1() {

        System.out.println("这是groupsonclass1中的teacher11111111");
    }

    public void teacher2() {

        System.out.println("这是groupsonclass1中的teacher2222222");
    }
}
