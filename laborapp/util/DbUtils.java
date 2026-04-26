package com.laborapp.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector;

public class DbUtils {
    public static void loadTableData(Connection connection, JTable table, String sql) throws SQLException {
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            Vector<String> colNames = new Vector<>();
            for (int i = 1; i <= cols; i++) colNames.add(md.getColumnName(i));
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= cols; i++) row.add(rs.getObject(i));
                data.add(row);
            }
            table.setModel(new DefaultTableModel(data, colNames));
        }
    }
    
    public static void loadTableDataWithParams(Connection connection, JTable table, String sql, Object... params)
            throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                Vector<String> colNames = new Vector<>();
                for (int i = 1; i <= cols; i++) {
                    colNames.add(md.getColumnName(i));
                }
                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= cols; i++) {
                        row.add(rs.getObject(i));
                    }
                    data.add(row);
                }
                table.setModel(new DefaultTableModel(data, colNames));
            }
        }
    }
}