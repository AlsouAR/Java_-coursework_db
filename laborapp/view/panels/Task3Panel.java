package com.laborapp.view.panels;

import com.laborapp.model.*;
import com.laborapp.service.LaborService;
import com.laborapp.util.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Task3Panel extends JPanel {
    private final Connection connection;
    private final LaborService service;
    private final JTextArea textArea;
    private final JTextField quantityField;
    private final JTextField gradeField;
    private final Color BG_CONTENT = Color.WHITE;

    public Task3Panel(Connection connection, LaborService service) {
        this.connection = connection;
        this.service = service;
        
        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);
        
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBackground(BG_CONTENT);
        
        // Первая строка: заголовок
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(new JLabel("Все детали, такие что:"));
        titlePanel.add(new JLabel("в сборке этой детали в КАЖДОЙ операции, которую выполняет рабочий с квалификационным разрядом <"));
        
        // Вторая строка: поля ввода и кнопки
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        gradeField = new JTextField("3", 3);
        inputPanel.add(gradeField);
        inputPanel.add(new JLabel(", используется хотя бы одна деталь-компонент в количестве >"));
        
        quantityField = new JTextField("5", 5);
        inputPanel.add(quantityField);
        
        JButton btnSql = new JButton("SQL (кванторы)");
        JButton btnJava = new JButton("Java (record-oriented)");
        
        inputPanel.add(btnSql);
        inputPanel.add(btnJava);
        
        top.add(titlePanel);
        top.add(inputPanel);
        
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setEditable(false);
        
        btnSql.addActionListener(e -> runSqlQuantifier());
        btnJava.addActionListener(e -> runJavaRecordOriented());
        
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }
    
    private int getQuantity() {
        try {
            return Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Некорректное количество", "Ошибка", JOptionPane.ERROR_MESSAGE);
            quantityField.setText("5");
            return 5;
        }
    }
    
    private int getGrade() {
        try {
            return Integer.parseInt(gradeField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Некорректный разряд", "Ошибка", JOptionPane.ERROR_MESSAGE);
            gradeField.setText("3");
            return 3;
        }
    }
    
    private void runSqlQuantifier() {
        int minQuantity = getQuantity();
        int maxGrade = getGrade();
        
        // SQL с кванторами через NOT EXISTS
        String sql = "SELECT d.detail_code, d.name, d.detail_type " +
                    "FROM details d " +
                    "WHERE NOT EXISTS (" +
                    "  SELECT 1 FROM labor_norms ln " +
                    "  WHERE ln.detail_code = d.detail_code " +
                    "    AND ln.worker_grade < " + maxGrade + " " +
                    "    AND NOT EXISTS (" +
                    "      SELECT 1 FROM assembly a " +
                    "      WHERE a.assembly_code = d.detail_code " +
                    "        AND a.operation_number = ln.operation_number " +
                    "        AND a.used_qty > " + minQuantity +
                    "    )" +
                    ") " +
                    "ORDER BY d.detail_code";
        
        displayResultsInTextArea(sql, "Задача 3: Детали с проверкой условий (SQL с кванторами)");
    }
    
    private void runJavaRecordOriented() {
        int minQuantity = getQuantity();
        int maxGrade = getGrade();
        
        StringBuilder sb = new StringBuilder();
        sb.append("ЗАДАЧА 3: Детали с проверкой условий (Java record-oriented)\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append(String.format("%-10s | %-30s | %-15s\n", "Код", "Наименование", "Тип"));
        sb.append("-".repeat(10)).append("-+-").append("-".repeat(30)).append("-+-").append("-".repeat(15)).append("\n");
        
        try {
            // Используем реализованный метод из сервиса
            List<Detail> result = service.findDetailsWithCondition(minQuantity, maxGrade);
            
            if (result.isEmpty()) {
                sb.append("Детали, удовлетворяющие условиям, не найдены.\n");
                sb.append("Параметры поиска: разряд рабочего < ").append(maxGrade);
                sb.append(", количество компонента > ").append(minQuantity).append("\n");
            } else {
                for (Detail d : result) {
                    sb.append(String.format("%-10d | %-30s | %-15s\n", 
                        d.detailCode, d.name, d.detailType));
                }
                sb.append("\nВсего найдено деталей: ").append(result.size()).append("\n");
            }
            
        } catch (Exception ex) {
            sb.append("Ошибка при выполнении: ").append(ex.getMessage()).append("\n");
            ex.printStackTrace();
        }
        
        textArea.setText(sb.toString());
    }
    
    private void displayResultsInTextArea(String sql, String title) {
        try (Statement stmt = connection.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData md = rs.getMetaData();
            int cols = md.getColumnCount();
            StringBuilder sb = new StringBuilder();
            sb.append(title).append("\n");
            sb.append("=".repeat(80)).append("\n");
            
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
            
            sb.append("\nВсего найдено деталей: ").append(count);
            textArea.setText(sb.toString());
            
        } catch (SQLException e) {
            textArea.setText("Ошибка выполнения запроса:\n" + e.getMessage());
        }
    }
}