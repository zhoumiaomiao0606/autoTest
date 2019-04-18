package come.course.testng.groups;

import org.testng.annotations.Test;

@Test(groups = "student")
public class groupsonclass2 {


    public void student1(){

        System.out.println("这是groupsonclass2中的student11111111");
    }
    public void student2(){

        System.out.println("这是groupsonclass2中的student222222");
    }
}
