package com.laborapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // ИЗМЕНИТЕ НА ВАШИ ДАННЫЕ!
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/Kursovaya";
    private static final String USER = "postgres";
    private static final String PASS = "root";  // Измените на ваш пароль!

    private static Connection connection;

    static {
        try {
            // Регистрируем драйвер PostgreSQL
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Драйвер PostgreSQL загружен");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Драйвер PostgreSQL не найден!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            // Настройка для лучшей работы с ResultSet
            connection.setAutoCommit(true);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение с БД закрыто");
            } catch (SQLException e) {
                System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    // Для тестирования подключения
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getDbUrl() { return DB_URL; }
    public static String getUser() { return USER; }
    public static String getPass() { return PASS; }
}