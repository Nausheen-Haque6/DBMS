import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class LoginPage extends JPanel {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Map<String, String> credentials;

    public LoginPage(CardLayout cardLayout, JPanel mainPanel) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        credentials = new HashMap<>();
        
        
        // Predefined user credentials
        credentials.put("Administrator:admin123", "AdministratorPage");
        credentials.put("Admin:aadmin123", "AdminPage");
        credentials.put("Viewer:view123", "ViewerPage");

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        JLabel roleLabel = new JLabel("Role:");
        String[] roles = { "Administrator", "Admin", "Viewer" };
        JComboBox<String> roleCombo = new JComboBox<>(roles);
        JButton loginButton = new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(userField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(roleLabel, gbc);
        gbc.gridx = 1;
        add(roleCombo, gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            String key = role + ":" + password;

            if (credentials.containsKey(key) && username.equalsIgnoreCase(role)) {
                cardLayout.show(mainPanel, credentials.get(key));
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
