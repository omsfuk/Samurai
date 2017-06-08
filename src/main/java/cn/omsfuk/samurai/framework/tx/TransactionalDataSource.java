package cn.omsfuk.samurai.framework.tx;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Created by omsfuk on 17-5-31.
 */
public class TransactionalDataSource extends AbstractDataSource {

    private ThreadLocal<Connection> connections = new ThreadLocal<>();

    private ThreadLocal<Boolean> isTransactionExist = new ThreadLocal<>();

    public TransactionalDataSource(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Connection getConnection() {
        // 保证所有带有@Transactional注解的方法调用getConnection之前，都会调用setConnection方法。若是没有，则证明是非事务方法。
        Connection conn = null;
        if ((conn = connections.get()) == null) {
            conn = super.getConnection();
        }
        return conn;
    }

    public void setConnection(Connection conn) {
        connections.set(conn);
    }

    public void removeConnection() {
        connections.remove();
    }

    public void setTransactionExist(boolean transactionExist) {
        isTransactionExist.set(transactionExist);
    }

    public void removeTransactionExistFlag() {
        isTransactionExist.remove();
    }

    public Boolean isTranactionalExist() {
        return isTransactionExist.get();
    }
}
