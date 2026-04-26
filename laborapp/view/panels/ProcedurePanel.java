package com.laborapp.view.panels;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class ProcedurePanel extends JPanel {
    private static final Color BG_CONTENT = Color.WHITE;

    public ProcedurePanel(Connection connection) {
        setBackground(BG_CONTENT);
        setLayout(new GridBagLayout());

        JLabel lbl = new JLabel("Введите код детали для статистики:");
        JTextField tf = new JTextField("1", 15);
        JButton btn = new JButton("Выполнить get_detail_production_stats");
        JTextArea resArea = new JTextArea();
        resArea.setEditable(false);
        resArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resArea.setBorder(new LineBorder(Color.LIGHT_GRAY));
        resArea.setPreferredSize(new Dimension(500, 150));

        btn.addActionListener(e -> {
            try {
                int detailCode = Integer.parseInt(tf.getText());
                String sql = "SELECT * FROM get_detail_production_stats(?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, detailCode);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            int totalGood = rs.getInt("total_good");
                            int totalDefect = rs.getInt("total_defect");
                            double defectRate = rs.getDouble("defect_rate");
                            String result = String.format(
                                "Статистика по детали #%d:\n" +
                                "═══════════════════════════════\n" +
                                "✅ Годных деталей: %d\n" +
                                "❌ Бракованных: %d\n" +
                                "📊 Процент брака: %.2f%%\n" +
                                "═══════════════════════════════\n" +
                                "Всего произведено: %d",
                                detailCode, totalGood, totalDefect, defectRate,
                                totalGood + totalDefect
                            );
                            resArea.setText(result);
                        } else {
                            resArea.setText("Деталь с кодом = " + detailCode + " не найдена.");
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                resArea.setText("Ошибка: введите корректный код детали (целое число)");
            } catch (SQLException ex) {
                resArea.setText("Ошибка выполнения процедуры:\n" + ex.getMessage() + 
                              "\n\nУбедитесь, что процедура создана в БД.");
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        add(lbl, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(tf, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        add(btn, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.weightx = 1; gbc.weighty = 1;
        add(new JScrollPane(resArea), gbc);
    }
}