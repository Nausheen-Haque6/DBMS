import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class AdminPage extends JPanel {

    private Connection connection;
    private JPanel tablePanel;

    public AdminPage() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome, Admin!", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        tablePanel = new JPanel(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        add(buttonPanel, BorderLayout.SOUTH);

        // Specify the table names
        String[] tableNames = {"APersonalDetails", "FinancialRecords", "MedicalHistory"};

        for (String tableName : tableNames) {
            JButton tableButton = new JButton(tableName);
            buttonPanel.add(tableButton);

            // Add action listener to each button
            tableButton.addActionListener(e -> displayTableData(tableName));
        }

        // Database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cyberdb", "root", "Nausheen@123");
            connection.setCatalog("cyberdb");
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error connecting to the database", SwingConstants.CENTER);
            tablePanel.add(errorLabel, BorderLayout.CENTER);
        }
    }

    private void displayTableData(String tableName) {
        tablePanel.removeAll();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Column names
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Data rows
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            // Create table with data
            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            tablePanel.add(scrollPane, BorderLayout.CENTER);
            tablePanel.revalidate();
            tablePanel.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error retrieving data", SwingConstants.CENTER);
            tablePanel.add(errorLabel, BorderLayout.CENTER);
        }
    }
}
