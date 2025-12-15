package com.example.truststock.workcontrol;

import com.example.truststock.model.Product;
import javafx.event.ActionEvent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.example.truststock.db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.*;
import java.sql.*;


public class InventoryController {
    public TextField txtName;
    public TextField txtPrice;
    public TextField txtStock;
    public TextField txtMinStock;

    @FXML private ChoiceBox<String> cbQuality;

    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnRefresh;

    @FXML private TableView<Product> tableProducts;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Integer> colMinStock;
    @FXML private TableColumn<Product, String> colQuality;
        private final ObservableList<Product> productList = FXCollections.observableArrayList();

        @FXML
        public void initialize() {

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            colMinStock.setCellValueFactory(new PropertyValueFactory<>("minStock"));
            colQuality.setCellValueFactory(new PropertyValueFactory<>("qualityStatus"));

            cbQuality.getItems().addAll("GOOD", "BAD");

            loadProducts();


            tableProducts.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(Product product, boolean empty) {
                    super.updateItem(product, empty);
                    if (product == null || empty) {
                        setStyle("");
                    } else if ("BAD".equalsIgnoreCase(product.getQualityStatus())) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else if (product.getStock() <= product.getMinStock()) {
                        setStyle("-fx-background-color: #fff2cc;");
                    } else {
                        setStyle("");
                    }
                }
            });


           tableProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> fillFieldsFromTable());
        }

    private void fillFieldsFromTable() {
    }

    private void loadProducts() {
        productList.clear();
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM products");
             ResultSet rs = ps.executeQuery()) {



            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min_stock"),
                        rs.getString("quality_status")
                );
                productList.add(p);
            }
            tableProducts.setItems(productList);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void addProduct() {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO products(name, price, stock, min_stock, quality_status) VALUES(?,?,?,?,?)")) {

            ps.setString(1, txtName.getText().trim());
            ps.setDouble(2, Double.parseDouble(txtPrice.getText().trim()));
            ps.setInt(3, Integer.parseInt(txtStock.getText().trim()));
            ps.setInt(4, Integer.parseInt(txtMinStock.getText().trim()));
            ps.setString(5, cbQuality.getValue());

            ps.executeUpdate();
            clearFields();
            loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
    }


}




