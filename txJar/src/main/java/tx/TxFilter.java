package tx;

import com.alibaba.dubbo.rpc.RpcContext;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeal on 17-8-10.
 */
public class TxFilter implements MethodInterceptor, Serializable {

    public static ThreadLocal<List<DubboMethod>> dubboMethodThreadLocal = new ThreadLocal<>();
    public static ThreadLocal<Boolean> tx = new ThreadLocal<>();

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Tx annotation = methodInvocation.getMethod().getAnnotation(Tx.class);
        if (annotation != null) {
            tx.set(true);
            dubboMethodThreadLocal.set(new ArrayList<DubboMethod>());
        }
        Object invoke = null;
        try {
            invoke = methodInvocation.getMethod().invoke(methodInvocation.getThis(), methodInvocation.getArguments());
            for (DubboMethod dubboMethod : dubboMethodThreadLocal.get()) {
                RpcContext.getContext().getAttachments().put("txCommit", dubboMethod.getUuid());
                dubboMethod.invoke();
            }
        } catch (Exception e) {
            for (DubboMethod dubboMethod : dubboMethodThreadLocal.get()) {
                RpcContext.getContext().getAttachments().put("txRollBack", dubboMethod.getUuid());
                dubboMethod.invoke();
            }
            throw e;
        } finally {
            dubboMethodThreadLocal.remove();
            tx.remove();
        }
        return invoke;
    }

}
