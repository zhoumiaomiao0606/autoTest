package come.course.testng.groups;

import org.testng.annotations.Test;

@Test(groups = "stu")
public class groupsonclass3 {


    public void stu1() {

        System.out.println("这是groupsonclass3中的stu11111111");
    }

    public void stu2() {

        System.out.println("这是groupsonclass3中的stu22222");
    }
}
