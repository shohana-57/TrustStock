package com.example.truststock;
import com.example.truststock.model.Staff_User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.awt.*;
import java.io.IOException;

public class StaffDashboardController {

    public Label lblWelcome;
    public Label lblRole;
    @FXML
    private Button btnLogout;


    private void loadScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/" + fxml));
            Stage stage = (Stage) btnLogout.getScene().getWindow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openInventory() {

    }

    @FXML
    void openQuality() {

    }

    @FXML
    void openReplace() {

    }

    @FXML
    void openAlerts() {

    }


    @FXML
    void logout() throws IOException {
        Stage stage = (Stage) btnLogout.getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/welcome_page.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public void setUser(Staff_User user) {
        lblWelcome.setText("Welcome, " + user.getFullName());
        lblRole.setText("Role: " + user.getRole());
    }
}
