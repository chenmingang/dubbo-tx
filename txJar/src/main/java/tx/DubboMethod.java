package tx;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;

import java.util.Map;

/**
 * Created by zeal on 17-8-10.
 */
public class DubboMethod {

    private String uuid;
    private Invoker<?> invoker;
    private Invocation invocation;

    public DubboMethod(String uuid,Invoker<?> invoker, Invocation invocation) {
        this.uuid = uuid;
        this.invoker = invoker;
        this.invocation = invocation;
    }

    public Result invoke() {
        Map<String, String> attachments = invocation.getAttachments();
        if (attachments != null && attachments.containsKey("txId")) {
            attachments.remove("txId");
        }
        return invoker.invoke(invocation);
    }

    public String getUuid() {
        return uuid;
    }
}
