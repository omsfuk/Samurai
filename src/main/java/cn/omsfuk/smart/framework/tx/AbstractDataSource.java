package cn.omsfuk.smart.framework.tx;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Created by omsfuk on 17-5-31.
 */
public class AbstractDataSource implements DataSource {

    private DataSource dataSource;

    public AbstractDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) {
        try {
            return dataSource.getConnection(username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        try {
            return dataSource.unwrap(iface);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        try {
            return dataSource.isWrapperFor(iface);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PrintWriter getLogWriter() {
        try {
            return dataSource.getLogWriter();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        try {
            dataSource.setLogWriter(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setLoginTimeout(int seconds) {
        try {
            dataSource.setLoginTimeout(seconds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getLoginTimeout() {
        try {
            return dataSource.getLoginTimeout();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        try {
            return dataSource.getParentLogger();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
