import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class ViewerPage extends JPanel {

    private Connection connection;
    private JPanel tablePanel;

    public ViewerPage() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome, Viewer!", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        tablePanel = new JPanel(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        add(buttonPanel, BorderLayout.SOUTH);

        // Database connection
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/cyberdb", "root", "Nausheen@123");
            connection.setCatalog("cyberdb");

            // Hardcoded table names
            String[] tableNames = {"APersonalDetails", "FinancialRecords", "MedicalHistory"};

            for (int i = 0; i < tableNames.length; i++) {
                String tableName = tableNames[i];
                JButton tableButton = new JButton(tableName);
                buttonPanel.add(tableButton);

                // Add action listener to each button
                boolean encrypt = (i != 0); // Encrypt all except the first table
                tableButton.addActionListener(e -> displayTableData(tableName, encrypt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error connecting to the database", SwingConstants.CENTER);
            tablePanel.add(errorLabel, BorderLayout.CENTER);
        }
    }

    private void displayTableData(String tableName, boolean encrypt) {
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
                    Object value = rs.getObject(i);
                    if (encrypt && i != 1) { // Encrypt all except the first column of the first table
                        row.add(encrypt(value.toString()));
                    } else {
                        row.add(value);
                    }
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

    private String encrypt(String input) {
        char[] key = {'K', 'C', 'Q'}; // Example key for XOR
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            encrypted.append((char) (input.charAt(i) ^ key[i % key.length]));
        }
        return encrypted.toString();
    }
}
