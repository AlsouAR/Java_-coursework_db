package com.laborapp.view.panels;

import com.laborapp.util.DbUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class JoinPanel extends JPanel {
    private static final Color BG_CONTENT = Color.WHITE;

    public JoinPanel(Connection connection) {
        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);

        JButton btnRefresh = new JButton("Показать JOIN трёх таблиц");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(BG_CONTENT);
        top.add(btnRefresh);

        JTable table = new JTable();
        table.setFillsViewportHeight(true);

        btnRefresh.addActionListener(e -> {
            String sql = "SELECT " +
                    "w.worker_id, w.full_name, w.shop_number, w.section_number, " +
                    "ln.detail_code, ln.operation_number, ln.profession_code, ln.worker_grade, " +
                    "wp.work_date, wp.good_qty, wp.defect_qty, wp.defect_pay_percent " +
                    "FROM work_production wp " +
                    "JOIN workers w ON wp.worker_id = w.worker_id " +
                    "JOIN labor_norms ln ON wp.detail_code = ln.detail_code " +
                    "  AND wp.operation_number = ln.operation_number " +
                    "ORDER BY wp.work_date DESC";
            try {
                DbUtils.loadTableData(connection, table, sql);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки данных: " + ex.getMessage());
            }
        });

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}