import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class AdministratorPage extends JPanel {

    private Connection connection;
    private JPanel tablePanel;
    private JPanel controlPanel;
    private JComboBox<String> tableComboBox;
    private JTextField[] inputFields;
    private Vector<String> currentColumns;

    public AdministratorPage() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome, Administrator!", SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        tablePanel = new JPanel(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);

        controlPanel = new JPanel();
        add(controlPanel, BorderLayout.SOUTH);

        tableComboBox = new JComboBox<>();
        controlPanel.add(tableComboBox);

        JButton showButton = new JButton("Show Table");
        showButton.addActionListener(e -> showTable((String) tableComboBox.getSelectedItem()));
        controlPanel.add(showButton);

        JButton addButton = new JButton("Add Data");
        addButton.addActionListener(e -> addData((String) tableComboBox.getSelectedItem()));
        controlPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Data");
        deleteButton.addActionListener(e -> deleteData((String) tableComboBox.getSelectedItem()));
        controlPanel.add(deleteButton);

        // Add the hardcoded table names
        tableComboBox.addItem("APersonalDetails");
        tableComboBox.addItem("FinancialRecords");
        tableComboBox.addItem("MedicalHistory");

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

    private void showTable(String tableName) {
        tablePanel.removeAll();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            currentColumns = new Vector<>();
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                currentColumns.add(columnName);
                columnNames.add(columnName);
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            tablePanel.add(scrollPane, BorderLayout.CENTER);

            JPanel inputPanel = new JPanel(new GridLayout(2, columnCount));
            inputFields = new JTextField[columnCount];
            for (int i = 0; i < columnCount; i++) {
                inputPanel.add(new JLabel(columnNames.get(i)));
                inputFields[i] = new JTextField();
                inputPanel.add(inputFields[i]);
            }

            tablePanel.add(inputPanel, BorderLayout.NORTH);

            tablePanel.revalidate();
            tablePanel.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error retrieving data", SwingConstants.CENTER);
            tablePanel.add(errorLabel, BorderLayout.CENTER);
        }
    }

    private void addData(String tableName) {
        StringBuilder query = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        for (int i = 0; i < currentColumns.size(); i++) {
            query.append(currentColumns.get(i));
            if (i < currentColumns.size() - 1) {
                query.append(", ");
            }
        }
        query.append(") VALUES (");
        for (int i = 0; i < inputFields.length; i++) {
            query.append("?");
            if (i < inputFields.length - 1) {
                query.append(", ");
            }
        }
        query.append(")");

        try (PreparedStatement pstmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < inputFields.length; i++) {
                pstmt.setString(i + 1, inputFields[i].getText());
            }
            pstmt.executeUpdate();
            showTable(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error inserting data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteData(String tableName) {
        StringBuilder query = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
        for (int i = 0; i < currentColumns.size(); i++) {
            query.append(currentColumns.get(i)).append(" = ?");
            if (i < currentColumns.size() - 1) {
                query.append(" AND ");
            }
        }

        try (PreparedStatement pstmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < inputFields.length; i++) {
                pstmt.setString(i + 1, inputFields[i].getText());
            }
            pstmt.executeUpdate();
            showTable(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
