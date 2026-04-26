package com.laborapp.view.panels;

import com.laborapp.service.LaborService;
import com.laborapp.util.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class Task2Panel extends JPanel {
    private final Connection connection;
    private final LaborService service;
    private final JTextArea textArea;
    private final Color BG_CONTENT = Color.WHITE;

    public Task2Panel(Connection connection, LaborService service) {
        this.connection = connection;
        this.service = service;
        
        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(BG_CONTENT);
        
        JButton btnSql = new JButton("SQL (оконные функции)");
        JButton btnJava = new JButton("Java Streams");
        JButton btnView = new JButton("Показать в таблице");
        
        top.add(new JLabel("Для каждой сборочной единицы и операции:"));
        top.add(btnSql);
        top.add(btnJava);
        top.add(btnView);
        
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setEditable(false);
        
        btnSql.addActionListener(e -> runSqlWithWindowFunctions());
        btnJava.addActionListener(e -> runJavaStreams());
        btnView.addActionListener(e -> showInTable());
        
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
    
    private void runSqlWithWindowFunctions() {
        String sql = "SELECT " +
                    "ln.detail_code AS \"Код детали\", " +
                    "ln.operation_number AS \"Номер операции\", " +
                    "ln.prep_time AS \"Время подготовительно-заключительное\", " +
                    "ln.piece_time AS \"Время штучное\", " +
                    "ROUND(ln.prep_time * 100.0 / SUM(ln.prep_time + ln.piece_time) " +
                    "  OVER (PARTITION BY ln.detail_code), 2) AS \"Доля подготовительного времени, %\", " +
                    "ROUND(ln.piece_time * 100.0 / SUM(ln.prep_time + ln.piece_time) " +
                    "  OVER (PARTITION BY ln.detail_code), 2) AS \"Доля штучного времени, %\", " +
                    "MAX(ln.piece_time) OVER (PARTITION BY ln.detail_code) " +
                    "  AS \"Макс. штучное время по детали\" " +
                    "FROM labor_norms ln " +
                    "ORDER BY ln.detail_code, ln.operation_number";
        
        displayResultsInTextArea(sql, "Задача 2: Нормы затрат труда с оконными функциями");
    }
    
    private void runJavaStreams() {
        StringBuilder sb = new StringBuilder();
        sb.append("ЗАДАЧА 2: Статистика по нормам времени (Java Streams)\n");
        sb.append("=".repeat(120)).append("\n");
        sb.append(String.format("%-10s | %-8s | %-8s | %-8s | %-15s | %-15s | %-15s\n",
                "Код дет.", "Операция", "Подг. вр.", "Шт. вр.", 
                "Доля подг. вр., %", "Доля шт. вр., %", "Макс. шт. вр."));
        sb.append("-".repeat(120)).append("\n");
        
        try {
            var stats = service.getLaborNormStats();
            if (stats.isEmpty()) {
                sb.append("Нет данных о нормах времени\n");
            } else {
                for (var stat : stats) {
                    sb.append(String.format("%-10d | %-8d | %-8.2f | %-8.2f | %-15.2f | %-15.2f | %-15.2f\n",
                        stat.detailCode,
                        stat.operationNumber,
                        stat.prepTime.doubleValue(),
                        stat.pieceTime.doubleValue(),
                        stat.prepTimeShare.doubleValue(),
                        stat.pieceTimeShare.doubleValue(),
                        stat.maxPieceTime.doubleValue()
                    ));
                }
                sb.append("\nВсего записей: ").append(stats.size()).append("\n");
            }
        } catch (Exception ex) {
            sb.append("Ошибка: ").append(ex.getMessage()).append("\n");
            ex.printStackTrace();
        }
        
        textArea.setText(sb.toString());
    }
    
    private void showInTable() {
        String sql = "SELECT " +
                    "ln.detail_code as \"Код детали\", " +
                    "ln.operation_number as \"Номер операции\", " +
                    "ln.prep_time as \"Подг. время\", " +
                    "ln.piece_time as \"Шт. время\", " +
                    "ROUND(ln.prep_time * 100.0 / SUM(ln.prep_time + ln.piece_time) " +
                    "  OVER (PARTITION BY ln.detail_code), 2) as \"Доля подг. вр., %\", " +
                    "ROUND(ln.piece_time * 100.0 / SUM(ln.prep_time + ln.piece_time) " +
                    "  OVER (PARTITION BY ln.detail_code), 2) as \"Доля шт. вр., %\", " +
                    "MAX(ln.piece_time) OVER (PARTITION BY ln.detail_code) as \"Макс. шт. вр.\" " +
                    "FROM labor_norms ln " +
                    "ORDER BY ln.detail_code, ln.operation_number";
        
        JFrame frame = new JFrame("Результат задачи 2");
        frame.setSize(1000, 500);
        frame.setLocationRelativeTo(this);
        
        JTable table = new JTable();
        try {
            DbUtils.loadTableData(connection, table, sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage());
        }
        
        frame.add(new JScrollPane(table));
        frame.setVisible(true);
    }
    
    private void displayResultsInTextArea(String sql, String title) {
        try (Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            StringBuilder sb = new StringBuilder();
            sb.append(title).append("\n");
            sb.append("=".repeat(120)).append("\n");
            
            // Заголовки
            for (int i = 1; i <= cols; i++) {
                sb.append(String.format("%-20s | ", md.getColumnName(i)));
            }
            sb.append("\n");
            sb.append("-".repeat(20 * cols + 3 * (cols - 1))).append("\n");
            
            // Данные
            int count = 0;
            while (rs.next()) {
                count++;
                for (int i = 1; i <= cols; i++) {
                    Object value = rs.getObject(i);
                    String strValue = (value != null) ? value.toString() : "NULL";
                    sb.append(String.format("%-20s | ", strValue));
                }
                sb.append("\n");
            }
            
            sb.append("\nВсего записей: ").append(count);
            textArea.setText(sb.toString());
            
        } catch (SQLException e) {
            textArea.setText("Ошибка выполнения запроса:\n" + e.getMessage());
        }
    }
}