package ru.geekbains.sklyarov.network.chat.server;

import java.sql.*;

public class DatabaseConnection {
    private final Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    public DatabaseConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:local.db");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Unable to connect to database");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public PreparedStatement getPreparedStatement(String sqlQuery) throws SQLException {
        return connection.prepareStatement(sqlQuery);
    }

    public Statement getStatement() {
        return statement;
    }

    public void close() {
        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
