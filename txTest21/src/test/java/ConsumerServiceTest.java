import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import tx.Service.ConsumerService;

/**
 * Created by zeal on 17-8-8.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/*.xml")
public class ConsumerServiceTest {
    @Autowired
    ConsumerService consumerService;

    @Test
    public void t(){
        consumerService.say();
    }
}
