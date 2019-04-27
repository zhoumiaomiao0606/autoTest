package come.course.testng.paramiter;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class providerdate {
    @Test(dataProvider = "test")
    public void dateprovider(String name, int age) {

        System.out.println("name:" + name + ";" + "age:" + age);
    }

    @DataProvider(name = "test")
    public Object[][] dateprovidertest() {

        Object[][] o = new Object[][]{{"zhoumiaomiao", 10}, {"zhouhaohao", 17}};
        return o;
    }

    @Test(dataProvider = "zmm")
    public void dateprovider1(String name, int age) {

        System.out.println("fangfa111111name:" + name + ";" + "age:" + age);

    }

    @Test(dataProvider = "zmm")
    public void dateprovider2(String name, int age) {

        System.out.println("fangfa2222222name:" + name + ";" + "age:" + age);
    }


    @DataProvider(name = "zmm")
    public Object[][] dateprovidertest1(Method method) {

        Object[][] o = null;

        if (method.getName().equals("dateprovider1")) {
            o = new Object[][]{{"zhangsan", 19}, {"lisi", 20}};
        } else if (method.getName().equals("dateprovider2")) {
            o = new Object[][]{{"wangwu", 40}, {"zhaoliu", 41}};
        }
        ;

        return o;
    }


}
