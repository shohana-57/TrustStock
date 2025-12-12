package com.example.truststock;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;

public class StaffDashboardController {

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
        // TODO: load inventory page
    }

    @FXML
    void openQuality() {
        // TODO: load quality checker page
    }

    @FXML
    void openReplace() {
        // TODO: load replace page
    }

    @FXML
    void openAlerts() {
        // TODO: load alerts page
    }


    @FXML
    void logout() throws IOException {
        Stage stage = (Stage) btnLogout.getScene().getWindow();

        Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/welcome_page.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

}
