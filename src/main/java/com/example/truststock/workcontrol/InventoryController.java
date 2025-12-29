package com.example.truststock.workcontrol;

import com.example.truststock.model.Product;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.example.truststock.db.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.sql.*;


public class InventoryController {
    public TextField txtName;
    public TextField txtPrice;
    public TextField txtStock;
    public TextField txtMinStock;
    public Button btnLogout;
  //  public Label lblImagePath;
    private String selectedImagePath = null;
    public Button btnSelectImage;
    public ImageView imgPreview;

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
                        rs.getString("quality_status"),
                        rs.getString("image_path")
                );
                productList.add(p);
            }
            tableProducts.setItems(productList);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(btnAdd.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            imgPreview.setImage(new Image(file.toURI().toString()));
        }
    }


    @FXML
    private void addProduct() {
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO products(name, price, stock, min_stock, quality_status,image_path) VALUES(?,?,?,?,?,?)")) {

            ps.setString(1, txtName.getText().trim());
            ps.setDouble(2, Double.parseDouble(txtPrice.getText().trim()));
            ps.setInt(3, Integer.parseInt(txtStock.getText().trim()));
            ps.setInt(4, Integer.parseInt(txtMinStock.getText().trim()));
            ps.setString(5, cbQuality.getValue());
            ps.setString(6, selectedImagePath);

            ps.executeUpdate();
            loadProducts();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateProduct() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE products SET name=?, price=?, stock=?, min_stock=?, quality_status=?, image_path=? WHERE id=?")) {

            ps.setString(1, txtName.getText().trim());
            ps.setDouble(2, Double.parseDouble(txtPrice.getText().trim()));
            ps.setInt(3, Integer.parseInt(txtStock.getText().trim()));
            ps.setInt(4, Integer.parseInt(txtMinStock.getText().trim()));
            ps.setString(5, cbQuality.getValue());
            ps.setString(6, selectedImagePath != null ? selectedImagePath : selected.getImagePath());
            ps.setInt(7, selected.getId());

            ps.executeUpdate();
            clearFields();
            loadProducts();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to update product");
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void deleteProduct() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM products WHERE id=?")) {

            ps.setInt(1, selected.getId());
            ps.executeUpdate();
            loadProducts();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Failed to delete product!");
        }
    }

    @FXML
    private void refreshTable() {
        loadProducts();
        clearFields();
    }

    private void fillFieldsFromTable() {
        Product selected = tableProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtName.setText(selected.getName());
            txtPrice.setText(String.valueOf(selected.getPrice()));
            txtStock.setText(String.valueOf(selected.getStock()));
            txtMinStock.setText(String.valueOf(selected.getMinStock()));
            cbQuality.setValue(selected.getQualityStatus());
            selectedImagePath = selected.getImagePath();
            if (imgPreview != null && selectedImagePath != null) {
                imgPreview.setImage(new Image(selectedImagePath, 150, 150, true, true));
            }
        }
    }

    private void clearFields() {
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
        txtMinStock.clear();
        cbQuality.setValue(null);
        selectedImagePath = null;
        if (imgPreview != null) imgPreview.setImage(null);
    }

    public void goBack(ActionEvent actionEvent)throws IOException {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/truststock/staff_dashboard.fxml")
            );
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}




