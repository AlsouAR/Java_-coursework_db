package com.laborapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

import com.laborapp.db.*;
import com.laborapp.service.*;
import com.laborapp.view.panels.*;

public class LaborApp extends JFrame {
    private final LaborService service = new LaborService();
    private static final Color BG_SIDEBAR = new Color(240, 240, 240);
    private static final Color BG_CONTENT = Color.WHITE;

    private Connection connection;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new LaborApp().setVisible(true));
    }

    public LaborApp() {
        setTitle("Система учёта производства и сборки изделий");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        connectToDb();

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_CONTENT);
        
        // Добавляем ВАШИ панели
        contentPanel.add(new StartPanel(), "WELCOME");
        contentPanel.add(new CrudPanel(connection, service), "CRUD");
        contentPanel.add(new JoinPanel(connection), "JOIN");
        contentPanel.add(new ViewPanel(connection), "VIEW");
        contentPanel.add(new ProcedurePanel(connection), "PROC");
        contentPanel.add(new Task1Panel(connection, service), "TASK1");
        contentPanel.add(new Task2Panel(connection, service), "TASK2");
        contentPanel.add(new Task3Panel(connection, service), "TASK3");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(15, 10, 15, 10));
        sidebar.setPreferredSize(new Dimension(300, getHeight()));

        // Заголовок
        JLabel titleLabel = new JLabel("Учёт производства");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Основные функции
        sidebar.add(createMenuButton("🏠 Главная", "WELCOME"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("📊 Таблицы (CRUD)", "CRUD"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("🔗 Связи JOIN", "JOIN"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("👁 Представления", "VIEW"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("⚙ Хранимые процедуры", "PROC"));
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Задачи курсовой
        JLabel tasksLabel = new JLabel("Задания курсовой");
        tasksLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        tasksLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(tasksLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        sidebar.add(createMenuButton("🔧 Задача 1: Детали для сборки", "TASK1"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("📈 Задача 2: Нормы времени", "TASK2"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createMenuButton("✅ Задача 3: Детали с проверкой", "TASK3"));

        sidebar.add(Box.createVerticalGlue());
        
        // Информация о БД
        JPanel dbInfo = new JPanel();
        dbInfo.setLayout(new BoxLayout(dbInfo, BoxLayout.Y_AXIS));
        dbInfo.setBackground(new Color(220, 220, 220));
        dbInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dbInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel dbLabel = new JLabel("База данных:");
        dbLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        dbLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dbInfo.add(dbLabel);
        
        JLabel dbName = new JLabel("Kursovaya (PostgreSQL)");
        dbName.setAlignmentX(Component.LEFT_ALIGNMENT);
        dbInfo.add(dbName);
        
        JLabel tablesLabel = new JLabel("Таблицы: 6");
        tablesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dbInfo.add(tablesLabel);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(dbInfo);
        
        return sidebar;
    }

    private JButton createMenuButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(280, 45));
        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 240, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return btn;
    }

    private void connectToDb() {
        try {
            connection = DBConnection.getConnection();
            if (!connection.isValid(2)) {
                throw new SQLException("Соединение недействительно");
            }
            System.out.println("✅ Подключение к БД успешно!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Ошибка подключения к базе данных:\n" + e.getMessage() + 
                "\n\nПроверьте:\n" +
                "1. Запущен ли PostgreSQL\n" +
                "2. Существует ли база 'Kursovaya'\n" +
                "3. Пароль в DBConnection.java",
                "Ошибка подключения", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}