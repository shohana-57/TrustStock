package com.example.truststock.staff;

import com.example.truststock.model.Staff_User;
import com.example.truststock.session.Staffsession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class StaffDashboardController {

    @FXML private Label lblWelcome;
    @FXML private Label lblRole;

    @FXML private Button btnInventory;
    @FXML private Button btnReplace;
    @FXML private Button btnQuality;
    @FXML private Button btnAlerts;
    @FXML private Button btnOrder;
    @FXML private Button btnLogout;



    @FXML
    public void initialize() {

        Staff_User user = Staffsession.getUser();

        if (user == null) {
            lblWelcome.setText("Welcome");
            lblRole.setText("");
            disableAllButtons();
            return;
        }

        lblWelcome.setText("Welcome, " + user.getFullName());
        lblRole.setText("Role: " + user.getRole());

        applyPermissions(user.getRole());
    }


    private void disableAllButtons() {
        btnInventory.setDisable(true);
        btnReplace.setDisable(true);
        btnQuality.setDisable(true);
        btnAlerts.setDisable(true);
        btnOrder.setDisable(true);
    }

    private void applyPermissions(String role) {


        disableAllButtons();

        switch (role) {
            case "ADMIN" -> {
                btnInventory.setDisable(false);
                btnReplace.setDisable(false);
                btnAlerts.setDisable(false);
            }
            case "STOCK" -> {
                btnInventory.setDisable(false);
                btnReplace.setDisable(false);
            }
            case "QC" -> btnQuality.setDisable(false);
            case "MANAGER" -> btnAlerts.setDisable(false);
            case "DELIVER" -> btnOrder.setDisable(false);
        }
    }



    @FXML
    void openInventory() throws IOException {
        loadPage("InventoryPage.fxml", btnInventory);
    }

    @FXML
    void openQuality() throws IOException {
        loadPage("QualityCheck.fxml", btnQuality);
    }

    @FXML
    void openReplace() throws IOException {
        loadPage("BadItemReplace.fxml", btnReplace);
    }

    @FXML
    void openAlerts() throws IOException {
        loadPage("AlertForecasting_Page.fxml", btnAlerts);
    }

    @FXML
    void openOrder() throws IOException {
        loadPage("Staff_order.fxml", btnOrder);
    }

    private void loadPage(String fxml, Button source) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource("/com/example/truststock/" + fxml)
        );
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }



    @FXML
    void logout() {
        try {

            Staffsession.clear();

            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/truststock/HomePage.fxml")
            );
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

