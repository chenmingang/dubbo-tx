package tx;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;

import java.util.UUID;

/**
 * Created by zeal on 17-8-10.
 */
@Activate(group = {Constants.CONSUMER, Constants.PROVIDER}, value = Constants.VALIDATION_KEY, order = 10000)

public class TxConsumerFilter implements Filter {

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if(TxFilter.tx.get()!=null){
            String uuid = UUID.randomUUID().toString();
            RpcContext.getContext().getAttachments().put("txId", uuid);
            TxFilter.dubboMethodThreadLocal.get().add(new DubboMethod(uuid,invoker, invocation));
        }
        return invoker.invoke(invocation);
    }
}
