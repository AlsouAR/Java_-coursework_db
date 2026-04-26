package com.laborapp.view.panels;

import com.laborapp.model.DetailForAssembly;
import com.laborapp.service.LaborService;
import com.laborapp.util.DbUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Task1Panel extends JPanel {
    private final Connection connection;
    private final LaborService service;
    private final JTable table;
    private final JTextField assemblyCodeField;
    private final Color BG_CONTENT = Color.WHITE;

    public Task1Panel(Connection connection, LaborService service) {
        this.connection = connection;
        this.service = service;
        
        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);
        
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(BG_CONTENT);
        
        top.add(new JLabel("Код изделия для сборки:"));
        assemblyCodeField = new JTextField("1", 10);
        assemblyCodeField.setMaximumSize(new Dimension(100, 30));
        top.add(assemblyCodeField);
        
        JButton btnSql = new JButton("SQL запрос");
        JButton btnJava = new JButton("Java Streams");
        
        top.add(btnSql);
        top.add(btnJava);
        
        table = new JTable();
        table.setFillsViewportHeight(true);
        
        btnSql.addActionListener(e -> loadFromSql());
        btnJava.addActionListener(e -> loadFromJavaStreams());
        
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    
    private int getAssemblyCode() {
        try {
            String text = assemblyCodeField.getText().trim();
            if (text.isEmpty()) return 1;
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Некорректный код изделия. Используется значение по умолчанию: 1",
                "Предупреждение", JOptionPane.WARNING_MESSAGE);
            assemblyCodeField.setText("1");
            return 1;
        }
    }
    
    private void loadFromSql() {
        int assemblyCode = getAssemblyCode();
        
        String sql = "SELECT " +
                    "d.name AS \"Наименование детали\", " +
                    "a.operation_number AS \"Номер операции\", " +
                    "d.detail_type AS \"Тип детали\", " +
                    "a.used_qty AS \"Используемое количество\" " +
                    "FROM assembly a " +
                    "JOIN details d ON a.component_code = d.detail_code " +
                    "WHERE a.assembly_code = ? " +
                    "ORDER BY a.operation_number";
        
        try {
            DbUtils.loadTableDataWithParams(connection, table, sql, assemblyCode);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки данных: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void loadFromJavaStreams() {
        int assemblyCode = getAssemblyCode();
        
        try {
            List<DetailForAssembly> details = service.getDetailsForAssembly(assemblyCode);
            displayDetailsForAssembly(details);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке данных: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private void displayDetailsForAssembly(List<DetailForAssembly> list) {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Наименование детали");
        model.addColumn("Номер операции");
        model.addColumn("Тип детали");
        model.addColumn("Используемое количество");
        
        for (DetailForAssembly d : list) {
            model.addRow(new Object[]{
                d.detailName,
                d.operationNumber,
                d.detailType,
                d.usedQty
            });
        }
        table.setModel(model);
    }
}