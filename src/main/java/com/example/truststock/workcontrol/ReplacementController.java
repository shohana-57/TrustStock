package com.example.truststock.workcontrol;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import com.example.truststock.db.Database;
import com.example.truststock.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ReplacementController {

    public Button goOut;
    public Button btnPrev;
    @FXML private TableView<Product> tableProducts;
        @FXML private TableColumn<Product, Integer> colId;
        @FXML private TableColumn<Product, String> colName;
        @FXML private TableColumn<Product, Integer> colStock;
        @FXML private TableColumn<Product, String> colQuality;

        @FXML private TextField txtQuantity;
        @FXML private Button btnReplace;

        private final ObservableList<Product> productList = FXCollections.observableArrayList();

        @FXML
        public void initialize() {

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            colQuality.setCellValueFactory(new PropertyValueFactory<>("qualityStatus"));


            tableProducts.setRowFactory(tv -> new TableRow<>() {
                @Override
                protected void updateItem(Product product, boolean empty) {
                    super.updateItem(product, empty);
                    if (product == null || empty) {
                        setStyle("");
                    } else {
                        setStyle("-fx-background-color:#ffcccc;");
                    }
                }
            });

            loadBadProducts();
        }


        private void loadBadProducts() {
            productList.clear();

            String sql = "SELECT * FROM products WHERE quality_status='BAD'";

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @FXML
        private void replaceProduct() {
            Product selected = tableProducts.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert("Select a product first.");
                return;
            }

            int qty;
            try {
                qty = Integer.parseInt(txtQuantity.getText());
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                showAlert("Enter a valid replacement quantity.");
                return;
            }

            try (Connection conn = Database.getConnection()) {


                PreparedStatement ps = conn.prepareStatement("""
                UPDATE products
                SET stock = stock + ?, quality_status='GOOD'
                WHERE id = ?
            """);
                ps.setInt(1, qty);
                ps.setInt(2, selected.getId());
                ps.executeUpdate();


                PreparedStatement log = conn.prepareStatement("""
                INSERT INTO restocks(product_id, quantity, restock_date)
                VALUES (?, ?, ?)
            """);
                log.setInt(1, selected.getId());
                log.setInt(2, qty);
                log.setString(3, LocalDate.now().toString());
                log.executeUpdate();

                txtQuantity.clear();
                loadBadProducts();

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Failed to replace product.");
            }
        }

        private void showAlert(String msg) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.show();
        }

    public void goExit(ActionEvent actionEvent) {
        Stage stage = (Stage) goOut.getScene().getWindow();
        stage.close();
    }

    public void gePrev(ActionEvent actionEvent) throws IOException {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/truststock/staff_dashboard.fxml")
            );
            Stage stage = (Stage) btnPrev.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

