package com.example.truststock;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class HomePageController {

    @FXML
    private ImageView logoImage;

    @FXML
    private Button btnCustomer;

    @FXML
    private Button btnCompanyStaff;

    @FXML
    private void openCustomerPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("customer_page.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) btnCustomer.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML
    private void openStaffLoginPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("staff_login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) btnCompanyStaff.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
