package com.laborapp.view.panels;

import com.laborapp.service.LaborService;
import com.laborapp.util.DbUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

public class CrudPanel extends JPanel {
    private static final Color BG_CONTENT = Color.WHITE;

    private final Connection connection;
    private final LaborService service;

    private JTable table;
    private DefaultTableModel tableModel;
    private String currentTable = null;
    private Set<Object> originalRowKeys = new HashSet<>();

    public CrudPanel(Connection connection, LaborService service) {
        this.connection = connection;
        this.service = service;

        setLayout(new BorderLayout());
        setBackground(BG_CONTENT);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(BG_CONTENT);

        // ВАШИ таблицы
        String[] tables = {"details", "workers", "labor_norms", "work_production", "assembly"};
        JComboBox<String> tableCombo = new JComboBox<>(tables);
        JButton btnLoad = new JButton("Загрузить");
        JButton btnAdd = new JButton("Добавить");
        JButton btnDelete = new JButton("Удалить");
        JButton btnSave = new JButton("Сохранить");

        btnAdd.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(false);

        topPanel.add(new JLabel("Таблица: "));
        topPanel.add(tableCombo);
        topPanel.add(btnLoad);
        topPanel.add(btnAdd);
        topPanel.add(btnDelete);
        topPanel.add(btnSave);

        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        table.setAutoCreateRowSorter(false);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnLoad.addActionListener(e -> {
            currentTable = (String) tableCombo.getSelectedItem();
            loadTable(currentTable);

            btnAdd.setEnabled(true);
            btnDelete.setEnabled(true);
            btnSave.setEnabled(true);
        });

        btnAdd.addActionListener(e -> {
            if (tableModel != null)
                tableModel.addRow(createEmptyRow(currentTable));
        });

        btnDelete.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            if (rows.length == 0) return;
            for (int i = rows.length - 1; i >= 0; i--)
                tableModel.removeRow(rows[i]);
        });

        btnSave.addActionListener(e -> {
            try {
                saveChanges();
                loadTable(currentTable);
                JOptionPane.showMessageDialog(this, "Изменения сохранены");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения:\n" + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private void loadTable(String tableName) {
        try {
            DbUtils.loadTableData(connection, table, "SELECT * FROM " + tableName);
            tableModel = new DefaultTableModel(getData(), getColumns()) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return isEditableField(tableName, row, column);
                }
            };
            table.setModel(tableModel);

            originalRowKeys.clear();
            for (int row = 0; row < tableModel.getRowCount(); row++)
                originalRowKeys.add(getRowKey(tableName, tableModel, row));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private Object[][] getData() {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        Object[][] d = new Object[m.getRowCount()][m.getColumnCount()];
        for (int r = 0; r < m.getRowCount(); r++)
            for (int c = 0; c < m.getColumnCount(); c++)
                d[r][c] = m.getValueAt(r, c);
        return d;
    }

    private String[] getColumns() {
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        String[] c = new String[m.getColumnCount()];
        for (int i = 0; i < c.length; i++)
            c[i] = m.getColumnName(i);
        return c;
    }

    private boolean isEditableField(String table, int row, int col) {
        boolean isNewRow = !originalRowKeys.contains(getRowKey(table, tableModel, row));

        return switch (table) {
            case "workers" -> col != 0; // worker_id не редактируем
            case "details" -> col != 0; // detail_code не редактируем
            case "labor_norms" -> isNewRow ? true : (col != 0 && col != 1); // ключи не редактируем
            case "work_production" -> isNewRow ? true : col > 3; // ключи не редактируем
            case "assembly" -> isNewRow ? true : col > 2; // составной ключ
            default -> true;
        };
    }

    private Object[] createEmptyRow(String table) {
        return switch (table) {
            case "workers" -> new Object[]{null, 0, 0, 0, 0, "неизвестно", ""};
            case "details" -> new Object[]{null, "покупная", "", "шт", new BigDecimal("0.00")};
            case "labor_norms" -> new Object[]{0, 0, 0, 0, 0, new BigDecimal("0.0"), new BigDecimal("0.0")};
            case "work_production" -> new Object[]{0, new Date(System.currentTimeMillis()), 0, 0, 0, 0, new BigDecimal("0.0")};
            case "assembly" -> new Object[]{0, 0, 0, new BigDecimal("0.0")};
            default -> new Object[0];
        };
    }

    private Object getRowKey(String table, DefaultTableModel m, int row) {
        return switch (table) {
            case "workers" -> m.getValueAt(row, 0);
            case "details" -> m.getValueAt(row, 0);
            case "labor_norms" -> {
                int dc = parseInt(m.getValueAt(row, 0));
                int op = parseInt(m.getValueAt(row, 1));
                yield dc + "_" + op;
            }
            case "work_production" -> {
                int wid = parseInt(m.getValueAt(row, 0));
                Date dt = parseDate(m.getValueAt(row, 1));
                int dc = parseInt(m.getValueAt(row, 2));
                int op = parseInt(m.getValueAt(row, 3));
                yield wid + "_" + dt + "_" + dc + "_" + op;
            }
            case "assembly" -> {
                int ac = parseInt(m.getValueAt(row, 0));
                int cc = parseInt(m.getValueAt(row, 1));
                int op = parseInt(m.getValueAt(row, 2));
                yield ac + "_" + cc + "_" + op;
            }
            default -> null;
        };
    }

    private void saveChanges() throws SQLException {
        Set<Object> currentKeys = new HashSet<>();
        List<Object[]> rows = new ArrayList<>();

        for (int r = 0; r < tableModel.getRowCount(); r++) {
            currentKeys.add(getRowKey(currentTable, tableModel, r));

            Object[] rowData = new Object[tableModel.getColumnCount()];
            for (int c = 0; c < rowData.length; c++)
                rowData[c] = tableModel.getValueAt(r, c);

            rows.add(rowData);
        }

        // Удаление удаленных строк
        for (Object oldKey : originalRowKeys)
            if (!currentKeys.contains(oldKey))
                deleteByKey(currentTable, oldKey);

        // Сохранение/обновление существующих строк
        for (Object[] row : rows) {
            switch (currentTable) {
                case "workers" -> saveWorkerRow(row);
                case "details" -> saveDetailRow(row);
                case "labor_norms" -> saveLaborNormRow(row);
                case "work_production" -> saveWorkRecordRow(row);
                case "assembly" -> saveAssemblyRow(row);
            }
        }
    }

    private void saveWorkerRow(Object[] row) throws SQLException {
        Integer id = parseIntNullable(row[0]);
        com.laborapp.model.Worker w = new com.laborapp.model.Worker(
            id != null ? id : 0,
            parseInt(row[1]), parseInt(row[2]),
            parseInt(row[3]), parseInt(row[4]),
            String.valueOf(row[5]),
            String.valueOf(row[6])
        );

        if (id == null) service.insertWorker(w);
        else service.updateWorker(w);
    }

    private void saveDetailRow(Object[] row) throws SQLException {
        Integer id = parseIntNullable(row[0]);
        com.laborapp.model.Detail d = new com.laborapp.model.Detail(
            id != null ? id : 0,
            String.valueOf(row[1]),
            String.valueOf(row[2]),
            String.valueOf(row[3]),
            parseDecimal(row[4]),
            0, 0 // не используются в этой таблице
        );

        if (id == null) service.insertDetail(d);
        else service.updateDetail(d);
    }

    private void saveLaborNormRow(Object[] row) throws SQLException {
        int dc = parseInt(row[0]);
        int op = parseInt(row[1]);

        com.laborapp.model.LaborNorm n = new com.laborapp.model.LaborNorm(
            dc, op,
            parseInt(row[2]), parseInt(row[3]),
            parseInt(row[4]),
            parseDecimal(row[5]),
            parseDecimal(row[6])
        );

        boolean existed = originalRowKeys.contains(dc + "_" + op);
        if (existed) service.updateLaborNorm(n, dc, op);
        else service.insertLaborNorm(n);
    }

    private void saveWorkRecordRow(Object[] row) throws SQLException {
        int wid = parseInt(row[0]);
        Date dt = parseDate(row[1]);
        int dc = parseInt(row[2]);
        int op = parseInt(row[3]);

        com.laborapp.model.WorkRecord wr = new com.laborapp.model.WorkRecord(
            wid, dt, dc, op,
            parseInt(row[4]), parseInt(row[5]),
            parseDecimal(row[6])
        );

        String key = wid + "_" + dt + "_" + dc + "_" + op;
        if (originalRowKeys.contains(key))
            service.updateWorkRecord(wr, wid, dc, op, dt);
        else
            service.insertWorkRecord(wr);
    }

    private void saveAssemblyRow(Object[] row) throws SQLException {
        int ac = parseInt(row[0]);
        int cc = parseInt(row[1]);
        int op = parseInt(row[2]);

        com.laborapp.model.Assembly a = new com.laborapp.model.Assembly(
            ac, cc, op,
            parseDecimal(row[3])
        );

        String key = ac + "_" + cc + "_" + op;
        // Вам нужен AssemblyDao в сервисе
        // if (originalRowKeys.contains(key)) ...
        // else ...
    }

    private void deleteByKey(String table, Object key) throws SQLException {
        switch (table) {
            case "workers" -> service.deleteWorker(Integer.parseInt(key.toString()));
            case "details" -> service.deleteDetail(Integer.parseInt(key.toString()));
            case "labor_norms" -> {
                String[] p = key.toString().split("_");
                service.deleteLaborNorm(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
            }
            case "work_production" -> {
                String[] p = key.toString().split("_");
                service.deleteWorkRecord(
                    Integer.parseInt(p[0]),
                    Integer.parseInt(p[2]),
                    Integer.parseInt(p[3]),
                    Date.valueOf(p[1])
                );
            }
            // Добавьте для assembly когда создадите методы
        }
    }

    // Вспомогательные методы
    private Integer parseInt(Object v) {
        if (v == null || v.toString().isBlank()) return 0;
        if (v instanceof Integer i) return i;
        return Integer.parseInt(v.toString());
    }

    private Integer parseIntNullable(Object v) {
        if (v == null || v.toString().isBlank()) return null;
        return parseInt(v);
    }

    private BigDecimal parseDecimal(Object v) {
        if (v == null || v.toString().isBlank()) return BigDecimal.ZERO;
        if (v instanceof BigDecimal b) return b;
        return new BigDecimal(v.toString());
    }

    private Date parseDate(Object v) {
        if (v instanceof Date d) return d;
        return Date.valueOf(v.toString());
    }
}