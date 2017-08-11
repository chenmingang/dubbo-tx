package tx;

import org.springframework.beans.factory.annotation.Autowired;
import tx.mapper.TestMapper;

/**
 * Created by zeal on 17-8-8.
 */

public class ProviderServiceImpl2 implements ProviderService2 {

    @Autowired
    TestMapper testMapper;

    @Override
    public String hello(int i) {
        String result = "hello " + i;
        System.out.println(result);
        testMapper.insert(i);
        return result;
    }
}
