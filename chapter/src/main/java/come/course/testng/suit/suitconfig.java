package come.course.testng.suit;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class suitconfig {


@BeforeSuite
public void beforsuit(){
    System.out.println("beforsuit运行了");
}
@AfterSuite
public void aftersuit(){
    System.out.println("aftersuit运行了");
}



}
