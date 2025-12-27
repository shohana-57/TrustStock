package com.example.truststock.workcontrol;

import com.example.truststock.db.Database;
import com.example.truststock.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;

public class QualityController {

    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colQuality;

    @FXML private ComboBox<String> cbQuality;
    @FXML private Button btnGO;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colQuality.setCellValueFactory(new PropertyValueFactory<>("qualityStatus"));
        
        cbQuality.getItems().addAll("GOOD", "BAD");
        
        tableProducts.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);

                if (p == null || empty) {
                    setStyle("");
                } else if ("BAD".equalsIgnoreCase(p.getQualityStatus())) {
                    setStyle("-fx-background-color:#ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });


    }
    


}
