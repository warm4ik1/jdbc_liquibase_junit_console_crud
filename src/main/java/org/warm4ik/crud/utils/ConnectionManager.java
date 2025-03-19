package org.warm4ik.crud.utils;

import java.sql.*;


public final class ConnectionManager {

    private ConnectionManager() {

    }

    private static final String URL_KEY = PropertiesYmlUtil.get("database.url");
    private final static String USERNAME_KEY = PropertiesYmlUtil.get("database.username");
    private final static String PASSWORD_KEY = PropertiesYmlUtil.get("database.password");

    static {
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
//        } - легаси, для  JDBC (<4.0 версии)
    }

    public static Connection open() {
        try {
            return DriverManager.getConnection(URL_KEY, USERNAME_KEY, PASSWORD_KEY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static PreparedStatement getPreparedStatement(String sql) throws SQLException {
        return open().prepareStatement(sql);
    }

    public static PreparedStatement getPreparedStatementWithKeys(String sql) throws SQLException {
        return open().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

}
