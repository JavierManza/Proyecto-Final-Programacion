package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3307/biblioteca_municipal";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Coincide con MYSQL_ROOT_PASSWORD en docker-compose

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Conexión establecida con éxito.");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace(); // Esto nos dirá qué está fallando exactamente
                throw new SQLException("Error de conexión: " + e.getMessage(), e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Métodos para transacciones
    public static void startTransaction() throws SQLException {
        getConnection().setAutoCommit(false);
    }

    public static void commit() throws SQLException {
        if (connection != null) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public static void rollback() {
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
