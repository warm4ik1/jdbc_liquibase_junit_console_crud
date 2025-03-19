package org.warm4ik.crud.utils;

import java.sql.*;
import java.util.function.Consumer;
import java.util.function.Function;

public final class ConnectionManager {

    private ConnectionManager() {}

    private static final String URL_KEY = PropertiesYmlUtil.get("database.url");
    private static final String USERNAME_KEY = PropertiesYmlUtil.get("database.username");
    private static final String PASSWORD_KEY = PropertiesYmlUtil.get("database.password");
    private static final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    public static Connection open() {
        try {
            return DriverManager.getConnection(URL_KEY, USERNAME_KEY, PASSWORD_KEY);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при открытии соединения", e);
        }
    }

    public static Connection getConnection() {
        Connection connection = threadLocalConnection.get();
        if (connection == null) {
            connection = open();
            threadLocalConnection.set(connection);
        }
        return connection;
    }

    public static PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public static PreparedStatement getPreparedStatementWithKeys(String sql) throws SQLException {
        return getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    public static void executeInTransaction(Consumer<Connection> operation) {
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);
            operation.accept(connection);
            connection.commit();
        } catch (Exception e) {
            try {
                if (!connection.isClosed()) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
            throw new RuntimeException("Ошибка при выполнении транзакции", e);
        } finally {
            closeConnection();
        }
    }

    public static <T> T executeInTransactionWithResult(Function<Connection, T> operation) {
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);
            T result = operation.apply(connection);
            connection.commit();
            return result;
        } catch (Exception e) {
            try {
                if (!connection.isClosed()) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Ошибка при откате транзакции", rollbackEx);
            }
            throw new RuntimeException("Ошибка при выполнении транзакции", e);
        } finally {
            closeConnection();
        }
    }

    private static void closeConnection() {
        Connection connection = threadLocalConnection.get();
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Ошибка при закрытии соединения", e);
            } finally {
                threadLocalConnection.remove();
            }
        }
    }
}
