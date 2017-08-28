package tx;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;

public class TransactionManagerFactory {

    /**
     * transactionManager = (DataSourceTransactionManager) ApplicationContextHelper.getBean("transactionManager");
     * 如果以上方式不适合，可自行想办法注入
     */
    private static DataSourceTransactionManager dataSourceTransactionManager = null;

    public TransactionManagerFactory() {
    }

    public TransactionManagerFactory(DataSourceTransactionManager dataSourceTransactionManager) {
        this.dataSourceTransactionManager = dataSourceTransactionManager;
    }

    public static DataSourceTransactionManager getDataSourceTransactionManager() {
        //默认
        if (dataSourceTransactionManager == null) {
            dataSourceTransactionManager = (DataSourceTransactionManager) ApplicationContextHelper.getBean("transactionManager");
        }
        return dataSourceTransactionManager;
    }
}
