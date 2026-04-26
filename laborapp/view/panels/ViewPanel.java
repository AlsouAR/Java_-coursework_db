package com.laborapp.view.panels;

import com.laborapp.util.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewPanel extends JPanel {
    private static final Color BG_CONTENT = Color.WHITE;
    private final Connection connection;

    public ViewPanel(Connection connection) {
        this.connection = connection;

        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);

        JButton btnLoad = new JButton("VIEW v_detail_assembly_full");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(BG_CONTENT);
        top.add(btnLoad);

        JTable table = new JTable();
        table.setFillsViewportHeight(true);

        btnLoad.addActionListener(e -> {
            try {
                DbUtils.loadTableData(connection, table, "SELECT * FROM v_detail_assembly_full");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Ошибка загрузки VIEW. Создайте VIEW в БД:\n" + 
                    "CREATE VIEW v_detail_assembly_full AS ...\n\n" +
                    ex.getMessage());
            }
        });
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}