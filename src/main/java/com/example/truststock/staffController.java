package com.example.truststock;

import com.example.truststock.db.Database;
import com.example.truststock.model.Staff_User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class staffController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;
 private Database db=new Database();
    @FXML
    void handleLogin() throws SQLException {
        String u = txtUsername.getText().trim();
        String p = txtPassword.getText();

        if (u.isEmpty() || p.isEmpty()) {
            lblMessage.setText("Enter username and password.");
            return;
        }
db.getConnection();
        Staff_User user = db.authenticateUser(u, p);

        if (user == null) {
            lblMessage.setText("Invalid credentials.");
            return;
        }

        try {
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            FXMLLoader loader;
            Parent root;

            switch (user.getRole().toUpperCase()) {

                case "ADMIN":
                case "MANAGER":
                case "STOCK":
                    loader = new FXMLLoader(getClass()
                            .getResource("/com/example/truststock/staff_dashboard.fxml"));
                    root = loader.load();

                    StaffDashboardController staffCtrl = loader.getController();
                    staffCtrl.setUser(user);
                    break;

                case "QC":
                    loader = new FXMLLoader(getClass()
                            .getResource("/com/example/truststock/quality_checker.fxml"));
                    root = loader.load();
                    break;

                case "CUSTOMER":
                    loader = new FXMLLoader(getClass()
                            .getResource("/com/example/truststock/customer_page.fxml"));
                    root = loader.load();
                    break;

                default:
                    lblMessage.setText("Unknown role: " + user.getRole());
                    return;
            }

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            lblMessage.setText("Failed to open dashboard.");
        }
    }

    @FXML
    void openCustomerView() {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/truststock/customer_page.fxml")
            );
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exitApp() {
        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();
    }
}
