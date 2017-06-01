package cn.omsfuk.smart.framework.tx.aspect;

import cn.omsfuk.smart.framework.core.ProxyChain;
import cn.omsfuk.smart.framework.core.annotation.Around;
import cn.omsfuk.smart.framework.core.annotation.Aspect;
import cn.omsfuk.smart.framework.core.annotation.Order;
import cn.omsfuk.smart.framework.core.impl.DefaultBeanContext;
import cn.omsfuk.smart.framework.tx.TransactionNotExistException;
import cn.omsfuk.smart.framework.tx.TransactionalDataSource;
import cn.omsfuk.smart.framework.tx.annotation.Propagation;
import cn.omsfuk.smart.framework.tx.annotation.Transactional;

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
    private Object around(Method method, Object[] args, ProxyChain proxyChain) {
        TransactionalDataSource dataSource = (TransactionalDataSource) DefaultBeanContext.get().getBean("TransactionalDataSource");
        if (method.isAnnotationPresent(Transactional.class)) {
            Propagation propagation = method.getAnnotation(Transactional.class).propagation();
            Boolean transExist = dataSource.isTranactionalExist();
            if (transExist == null) {
                transExist = false;
                dataSource.setTransactionExist(false);
                Object result = execute(propagation, transExist, dataSource, method, args, proxyChain);
                dataSource.removeTransactionExistFlag();
                return result;
            } else {
                return execute(propagation, transExist, dataSource, method, args, proxyChain);
            }

        } else {
            return proxyChain.doProxyChain(method, args);
        }
    }

    private Object execute(Propagation propagation, Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        if (propagation == Propagation.REQUIRED) {
            return executeRequired(transExist, dataSource, method, args, proxyChain);
        } else if (propagation == Propagation.MANDATORY) {
            return executeMandatory(transExist, dataSource, method, args, proxyChain);
        } else if (propagation == Propagation.NESTED) {
            return executeNested(transExist, dataSource, method, args, proxyChain);
        } else if (propagation == Propagation.NEVER) {
            return executeNever(transExist, dataSource, method, args, proxyChain);
        } else if (propagation == Propagation.NOT_SUPPORTED) {
            return executeNotSupported(transExist, dataSource, method, args, proxyChain);
        } else if (propagation == Propagation.REQUIRESNEW) {
            return executeRequiresNew(transExist, dataSource, method, args, proxyChain);
        } else if (propagation == Propagation.SUPPORTS) {
            return executeSupports(transExist, dataSource, method, args, proxyChain);
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

    private Object executeRequired(Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        Connection conn = null;
        if (!transExist) {
            conn = getConnection(dataSource);
            dataSource.setConnection(conn);
            dataSource.setTransactionExist(true);
            setAutoCommit(conn, false);
            try {
                return proxyChain.doProxyChain(method, args);
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
                return proxyChain.doProxyChain(method, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Object executeNotSupported(Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        Connection conn = null;
        Connection oldConn = null;
        if (!transExist) {
            try {
                return proxyChain.doProxyChain(method, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            oldConn = getConnection(dataSource);
            dataSource.removeConnection();

            try {
                return proxyChain.doProxyChain(method, args);
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
    private Object executeRequiresNew(Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        Connection conn = null;
        Connection oldConn = null;
        if (!transExist) {
            conn = getConnection(dataSource);
            dataSource.setConnection(conn);
            dataSource.setTransactionExist(true);
            setAutoCommit(conn, false);
            try {
                return proxyChain.doProxyChain(method, args);
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
                return proxyChain.doProxyChain(method, args);
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
    private Object executeMandatory(Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        if (!transExist) {
            throw new TransactionNotExistException();
        } else {
            try {
                return proxyChain.doProxyChain(method, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Object executeNever(Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        if (transExist) {
            throw new TransactionNotExistException();
        } else {
            try {
                return proxyChain.doProxyChain(method, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Object executeSupports(Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        Connection oldConn = null;
        if (!transExist) {
            try {
                return proxyChain.doProxyChain(method, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            oldConn = getConnection(dataSource);
            dataSource.removeConnection();
            try {
                return proxyChain.doProxyChain(method, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                dataSource.setConnection(oldConn);
                dataSource.removeConnection();
            }
        }
    }


    private Object executeNested(Boolean transExist, TransactionalDataSource dataSource, Method method, Object[] args, ProxyChain proxyChain) {
        return null;
    }

}
