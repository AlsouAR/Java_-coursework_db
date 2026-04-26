package com.laborapp.view.panels;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends JPanel {
    private static final Color BG_CONTENT = Color.WHITE;

    public StartPanel() {
        setBackground(BG_CONTENT);
        setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BG_CONTENT);
        
        JLabel title = new JLabel("<html><center><h1>Система учёта производства и сборки изделий</h1></center></html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        
        JLabel subtitle = new JLabel("<html><center><h2>Курсовая работа по базам данных</h2></center></html>");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        JLabel info = new JLabel("<html><center>"
                + "<p>База данных: PostgreSQL</p>"
                + "<p>Таблицы: details, workers, labor_norms, work_production, assembly</p>"
                + "<p>Используйте меню слева для навигации</p>"
                + "</center></html>");
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(20, 0, 10, 0);
        centerPanel.add(title, gbc);
        
        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 20, 0);
        centerPanel.add(subtitle, gbc);
        
        gbc.gridy = 2; gbc.insets = new Insets(20, 0, 0, 0);
        centerPanel.add(info, gbc);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}
// package com.laborapp.view.panels;

// import javax.swing.*;
// import java.awt.*;

// public class StartPanel extends JPanel {
//     private static final Color BG_CONTENT = Color.WHITE;

//     public StartPanel() {
//         setBackground(BG_CONTENT);
//         setLayout(new GridBagLayout());
//         JLabel l = new JLabel("<html><center><h1>Система учета выработки и норм затрат труда</h1>");
//         l.setFont(new Font("SansSerif", Font.PLAIN, 14));
//         add(l);
//     }
// }
