package tx;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by zeal on 17-8-10.
 */
@Activate(group = {Constants.CONSUMER, Constants.PROVIDER}, value = Constants.VALIDATION_KEY, order = 10000)
public class TxServerFilter implements Filter {

    private static final Map<String, TransactionStatus> map = new ConcurrentHashMap<>();
    private static final Map<String, Long> txId_taskId = new ConcurrentHashMap<>();
    private static final Map<Long, Object> taskId = new ConcurrentHashMap<>();//已经被占用的县城
    private static final int txThreadNum = 20;
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(txThreadNum);


    private static DataSourceTransactionManager transactionManager;

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (transactionManager == null) {
            transactionManager = TransactionManagerFactory.getDataSourceTransactionManager();
        }
        Result result = new RpcResult();
        final String txId = RpcContext.getContext().getAttachment("txId");
        final String txCommit = RpcContext.getContext().getAttachment("txCommit");
        final String txRollBack = RpcContext.getContext().getAttachment("txRollBack");

        if (txCommit != null) {
            addTask("txCommit", txCommit, invoker, invocation);
            System.out.println("commit:" + txCommit);
            map.remove(txCommit);
            return result;
        }
        if (txRollBack != null) {
            addTask("rollBack", txRollBack, invoker, invocation);
            System.out.println("rollBack:" + txRollBack);
            map.remove(txRollBack);
            return result;
        }
        if (txId != null) {
            addTask("add", txId, invoker, invocation);
            result = addTask("invoke", txId, invoker, invocation);
            System.out.println("invoke:" + txId);
            return result;
        } else {
            return invoker.invoke(invocation);
        }
    }

    private Result addTask(String type, final String txId, final Invoker<?> invoker, final Invocation invocation) {
        try {
            if (type.equals("add")) {
                addTx(txId);
            }
            if (type.equals("invoke")) {
                return invokeTx(txId, invoker, invocation);
            }
            if (type.equals("txCommit")) {
                commitTx(txId);
            }
            if (type.equals("rollBack")) {
                rollBackTx(txId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void rollBackTx(final String txId) throws InterruptedException, ExecutionException {
        Boolean finshed = false;
        while (!finshed) {
            Future<Boolean> future = threadPool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Long threadId = Thread.currentThread().getId();
                    if (threadId.equals(txId_taskId.get(txId))) {
                        transactionManager.rollback(map.get(txId));
                        txId_taskId.remove(txId);
                        taskId.remove(threadId);
                        return true;
                    }
                    return false;
                }
            });
            finshed = future.get();
        }
    }

    private void commitTx(final String txId) throws InterruptedException, ExecutionException {
        Boolean finshed = false;
        while (!finshed) {
            Future<Boolean> future = threadPool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Long threadId = Thread.currentThread().getId();
                    if (threadId.equals(txId_taskId.get(txId))) {
                        transactionManager.commit(map.get(txId));
                        txId_taskId.remove(txId);
                        taskId.remove(threadId);
                        return true;
                    }
                    return false;
                }
            });
            finshed = future.get();
        }
    }

    private Result invokeTx(final String txId, final Invoker<?> invoker, final Invocation invocation) throws InterruptedException, ExecutionException {
        Result result = null;
        while (result == null) {
            Future<Result> future = threadPool.submit(new Callable<Result>() {
                @Override
                public Result call() throws Exception {
                    Long threadId = Thread.currentThread().getId();
                    if (threadId.equals(txId_taskId.get(txId))) {
                        return invoker.invoke(invocation);
                    }
                    return null;
                }
            });
            result = future.get();
        }
        return result;
    }

    private void addTx(final String txId) throws InterruptedException, ExecutionException {
        Boolean finshed = false;
        while (!finshed) {
            Future<Boolean> future = threadPool.submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Long threadId = Thread.currentThread().getId();
                    if (taskId.containsKey(threadId)) {
                        return false;
                    }
                    DefaultTransactionDefinition transDefinition = new DefaultTransactionDefinition();
                    transDefinition.setTimeout(10);
                    transDefinition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
                    final TransactionStatus transStatus = transactionManager.getTransaction(transDefinition);
                    map.put(txId, transStatus);
                    txId_taskId.put(txId, threadId);
                    taskId.put(threadId, "");
                    return true;
                }
            });
            finshed = future.get();
        }
    }

    public static DataSourceTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public static void setTransactionManager(DataSourceTransactionManager transactionManager) {
        TxServerFilter.transactionManager = transactionManager;
    }

}
