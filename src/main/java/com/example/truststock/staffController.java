package com.example.truststock;

import com.example.truststock.db.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class staffController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    @FXML
    void handleLogin() {
        String u = txtUsername.getText().trim();
        String p = txtPassword.getText();

        if (u.isEmpty() || p.isEmpty()) {
            lblMessage.setText("Enter username and password.");
            return;
        }

        String role = Database.authenticate(u, p);
        if (role == null) {
            lblMessage.setText("Invalid credentials.");
            return;
        }

        try {
            if (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("MANAGER") || role.equalsIgnoreCase("STOCK")) {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/staff_dashboard.fxml"));
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(new Scene(root));
            } else if (role.equalsIgnoreCase("QC")) {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/quality_checker.fxml"));
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(new Scene(root));
            } else if (role.equalsIgnoreCase("CUSTOMER")) {
                Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/customer_page.fxml"));
                Stage stage = (Stage) txtUsername.getScene().getWindow();
                stage.setScene(new Scene(root));
            } else {
                lblMessage.setText("Unknown role: " + role);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            lblMessage.setText("Failed to open dashboard.");
        }
    }

    @FXML
    void openCustomerView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/customer_page.fxml"));
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
