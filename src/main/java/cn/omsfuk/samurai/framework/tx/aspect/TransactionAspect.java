package cn.omsfuk.samurai.framework.tx.aspect;

import cn.omsfuk.samurai.framework.core.annotation.Around;
import cn.omsfuk.samurai.framework.core.annotation.Aspect;
import cn.omsfuk.samurai.framework.core.aop.Invocation;
import cn.omsfuk.samurai.framework.core.aop.ProxyChain;
import cn.omsfuk.samurai.framework.core.bean.BeanContextManager;
import cn.omsfuk.samurai.framework.tx.TransactionNotExistException;
import cn.omsfuk.samurai.framework.tx.TransactionalDataSource;
import cn.omsfuk.samurai.framework.tx.annotation.Propagation;
import cn.omsfuk.samurai.framework.tx.annotation.Transactional;
import cn.omsfuk.samurai.framework.core.annotation.Order;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by omsfuk on 17-5-31.
 */

@Aspect
@Order(10000)
public class TransactionAspect {

    @Around(anno = Transactional.class)
    private Object around(Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        TransactionalDataSource dataSource = (TransactionalDataSource) BeanContextManager.get().getBean("TransactionalDataSource");
        if (method.isAnnotationPresent(Transactional.class)) {
            Propagation propagation = method.getAnnotation(Transactional.class).propagation();
            Boolean transExist = dataSource.isTranactionalExist();
            if (transExist == null) {
                transExist = false;
                dataSource.setTransactionExist(false);
                Object result = execute(propagation, transExist, dataSource, invocation, proxyChain);
                dataSource.removeTransactionExistFlag();
                return result;
            } else {
                return execute(propagation, transExist, dataSource, invocation, proxyChain);
            }

        } else {
            return proxyChain.doProxyChain(invocation);
        }
    }

    private Object execute(Propagation propagation, Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        if (propagation == Propagation.REQUIRED) {
            return executeRequired(transExist, dataSource, invocation, proxyChain);
        } else if (propagation == Propagation.MANDATORY) {
            return executeMandatory(transExist, dataSource, invocation, proxyChain);
        } else if (propagation == Propagation.NESTED) {
            return executeNested(transExist, dataSource, invocation, proxyChain);
        } else if (propagation == Propagation.NEVER) {
            return executeNever(transExist, dataSource, invocation, proxyChain);
        } else if (propagation == Propagation.NOT_SUPPORTED) {
            return executeNotSupported(transExist, dataSource, invocation, proxyChain);
        } else if (propagation == Propagation.REQUIRESNEW) {
            return executeRequiresNew(transExist, dataSource, invocation, proxyChain);
        } else if (propagation == Propagation.SUPPORTS) {
            return executeSupports(transExist, dataSource, invocation, proxyChain);
        }
        return null;
    }

    private Connection getConnection(TransactionalDataSource dataSource) {
        Connection conn = dataSource.getConnection();
        return conn;
    }

    private void rollback(Connection conn) {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setAutoCommit(Connection conn, boolean bool) {
        try {
            conn.setAutoCommit(bool);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void commit(Connection conn) {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Object executeRequired(Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        Connection conn = null;
        if (!transExist) {
            conn = getConnection(dataSource);
            dataSource.setConnection(conn);
            dataSource.setTransactionExist(true);
            setAutoCommit(conn, false);
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                rollback(conn);
                throw new RuntimeException(e);
            } finally {
                commit(conn);
                setAutoCommit(conn, true);
                dataSource.setTransactionExist(false);
                dataSource.removeConnection();
            }
        } else {
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Object executeNotSupported(Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        Connection conn = null;
        Connection oldConn = null;
        if (!transExist) {
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            oldConn = getConnection(dataSource);
            dataSource.removeConnection();

            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                rollback(conn);
                throw new RuntimeException(e);
            } finally {
                commit(conn);
                setAutoCommit(conn, true);
                dataSource.setConnection(oldConn);
            }
        }

    }
    private Object executeRequiresNew(Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        Connection conn = null;
        Connection oldConn = null;
        if (!transExist) {
            conn = getConnection(dataSource);
            dataSource.setConnection(conn);
            dataSource.setTransactionExist(true);
            setAutoCommit(conn, false);
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                rollback(conn);
                throw new RuntimeException(e);
            } finally {
                commit(conn);
                setAutoCommit(conn, true);
                dataSource.setTransactionExist(false);
                dataSource.removeConnection();
            }
        } else {
            oldConn = conn;
            conn = getConnection(dataSource);
            dataSource.setConnection(conn);
            setAutoCommit(conn, false);
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                rollback(conn);
                throw new RuntimeException(e);
            } finally {
                commit(conn);
                setAutoCommit(conn, true);
                dataSource.setConnection(oldConn);
            }
        }
    }
    private Object executeMandatory(Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        if (!transExist) {
            throw new TransactionNotExistException();
        } else {
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Object executeNever(Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        if (transExist) {
            throw new TransactionNotExistException();
        } else {
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Object executeSupports(Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArgs();
        Connection oldConn = null;
        if (!transExist) {
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            oldConn = getConnection(dataSource);
            dataSource.removeConnection();
            try {
                return proxyChain.doProxyChain(invocation);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                dataSource.setConnection(oldConn);
                dataSource.removeConnection();
            }
        }
    }


    private Object executeNested(Boolean transExist, TransactionalDataSource dataSource, Invocation invocation, ProxyChain proxyChain) {
        return null;
    }

}
