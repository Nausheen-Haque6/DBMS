import javax.swing.*;
import java.awt.*;

public class MultiRoleSwingApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MultiRoleSwingApp::new);
    }

    public MultiRoleSwingApp() {
        JFrame frame = new JFrame("Multi-Role Swing Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        CardLayout cardLayout = new CardLayout();
        JPanel mainPanel = new JPanel(cardLayout);

        // Create instances of page classes
        LoginPage loginPage = new LoginPage(cardLayout, mainPanel);
        AdminPage adminPage = new AdminPage();
        AdministratorPage administratorPage = new AdministratorPage();
        ViewerPage viewerPage = new ViewerPage();

        // Add panels to the main panel with CardLayout
        mainPanel.add(loginPage, "LoginPage");
        mainPanel.add(adminPage, "AdminPage");
        mainPanel.add(administratorPage, "AdministratorPage");
        mainPanel.add(viewerPage, "ViewerPage");

        // Set the initial panel
        cardLayout.show(mainPanel, "LoginPage");

        frame.add(mainPanel);
        frame.setVisible(true);
    }
}
