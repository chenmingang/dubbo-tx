package tx.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tx.ProviderService;
import tx.ProviderService2;
import tx.Tx;

/**
 * Created by zeal on 17-8-8.
 */
@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Autowired
    ProviderService providerService;
    @Autowired
    ProviderService2 providerService2;

    @Tx
    @Override
    public void say() {
        System.out.println(providerService.hello(1));
        System.out.println(providerService.hello(2));
        System.out.println(providerService.hello(3));
        System.out.println(providerService.hello(4));
        System.out.println(providerService.hello(5));
        System.out.println(providerService.hello(6));
        System.out.println(providerService.hello(7));
        System.out.println(providerService.hello(8));
        System.out.println(providerService.hello(9));
        System.out.println(providerService.hello(10));
        System.out.println(providerService2.hello(11));
        System.out.println(providerService2.hello(12));
        System.out.println(providerService2.hello(13));
        System.out.println(providerService2.hello(14));
//        throw new RuntimeException("Test");
    }
}
