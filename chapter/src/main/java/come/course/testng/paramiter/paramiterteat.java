package come.course.testng.paramiter;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class paramiterteat {
    @Test
    @Parameters({"name","age"})
    public void test(String name,int age){
        System.out.println("name:"+ name +"     "+"age:"+age);


    }



}
