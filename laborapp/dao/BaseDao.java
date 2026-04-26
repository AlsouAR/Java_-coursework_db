package com.laborapp.dao;

import com.laborapp.db.DBConnection;

import java.sql.*;

public abstract class BaseDao {

    // Для запросов БЕЗ параметров
    protected <T> T executeQuery(String sql, ResultSetHandler<T> handler) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return handler.handle(rs);
        }
    }

    // Для запросов С параметрами (ИСПРАВЛЕННЫЙ)
    protected <T> T executeQuery(String sql, ParameterSetter setter, ResultSetHandler<T> handler) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) {
                setter.setParameters(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return handler.handle(rs);
            }
        }
    }

    protected int executeUpdate(String sql, ParameterSetter setter) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.setParameters(ps);
            return ps.executeUpdate();
        }
    }

    protected <T> T executeInsertReturning(String sql, ParameterSetter setter, ResultSetHandler<T> handler) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setter.setParameters(ps);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Insert failed: no rows affected");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return handler.handle(rs);
            }
        }
    }

    @FunctionalInterface
    public interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }

    @FunctionalInterface
    public interface ParameterSetter {
        void setParameters(PreparedStatement ps) throws SQLException;
    }
}