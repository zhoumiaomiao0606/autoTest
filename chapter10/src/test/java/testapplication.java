import com.exemple.demo.springbootapplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = springbootapplication.class )
public class testapplication {

    @Test
    public void testapplication(){
        System.out.println("测试成功了");
    }

}
