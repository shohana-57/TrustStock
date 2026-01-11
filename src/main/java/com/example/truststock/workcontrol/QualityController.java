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


   // public Button btnGO;
    public Button BtnGO;
    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, String> colQuality;

    @FXML private ComboBox<String> cbQuality;


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

        loadProducts();
    }

    private void loadProducts() {
        productList.clear();

        String sql = "SELECT * FROM products";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min_stock"),
                        rs.getString("quality_status"),
                        rs.getString("image_path")
                ));
            }

            tableProducts.setItems(productList);

            System.out.println("Products loaded: " + productList.size());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to load products.");
        }
    }



    @FXML
    private void updateQuality() {

        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        String newQuality = cbQuality.getValue();

        if (selected == null) {
            showAlert("Please select a product.");
            return;
        }

        if (newQuality == null) {
            showAlert("Please select quality status.");
            return;
        }

        String sql = "UPDATE products SET quality_status=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newQuality);
            ps.setInt(2, selected.getId());
            ps.executeUpdate();

            loadProducts();
            cbQuality.getSelectionModel().clearSelection();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Quality update failed.");
        }
    }


    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

//    public void goCommentPage() throws IOException {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/CustomerCommentPage.fxml"));
//            Parent root = loader.load();
//            Stage stage = (Stage)btnGO.getScene().getWindow();
//            stage.setTitle("Customer Comments");
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//            new Alert(Alert.AlertType.ERROR, "Failed to open comment page").show();
//        }
//    }

    public void goCustomerPage() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/customer_page.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)BtnGO.getScene().getWindow();
            stage.setTitle("Customer Comments");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open comment page").show();
        }
    }
}

