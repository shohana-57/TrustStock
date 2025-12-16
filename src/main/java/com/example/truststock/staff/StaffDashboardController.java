package com.example.truststock.staff;

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
import java.util.Objects;

public class StaffDashboardController {

    public Label lblWelcome;
    public Label lblRole;
    public Button btnInventory;
    public Button btnReplace;
    public Button btnQuality;
    public Button btnAlerts;
    @FXML
    private Button btnLogout;


    private void loadScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/truststock/" + fxml)));
            Stage stage = (Stage) btnLogout.getScene().getWindow();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openInventory() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/InventoryPage.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)btnInventory.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void openQuality() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/QualityCheck.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)btnQuality.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML
    void openReplace() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/BadItemReplace.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)btnReplace.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML
    void openAlerts() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/AlertForecasting_Page.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage)btnAlerts.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }


    @FXML
    void logout() throws IOException {

        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/truststock/HomePage.fxml")
            );
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }

   }
    private Staff_User user;

    public void setUser(Staff_User user) {
        this.user=user;
        lblWelcome.setText("Welcome, " + user.getFullName());
        lblRole.setText("Role: " + user.getRole());
        applyPermissions();
    }

    private void applyPermissions() {
        String role = user.getRole();

      
        btnInventory.setDisable(!(role.equals("ADMIN") || role.equals("STOCK")));
        btnReplace.setDisable(!(role.equals("ADMIN") || role.equals("STOCK")));
        btnQuality.setDisable(!role.equals("QC"));
        btnAlerts.setDisable(!(role.equals("ADMIN") || role.equals("MANAGER")));
    }

}
