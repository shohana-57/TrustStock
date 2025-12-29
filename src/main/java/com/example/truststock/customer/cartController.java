package com.example.truststock.customer;
import com.example.truststock.db.Database;
import com.example.truststock.model.CartItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.scene.control.*;


    public class cartController {

        public Label lblTotal;
        public TableView<CartItem> tableCart;

        @FXML private TableColumn<CartItem,String> colName;
        @FXML private TableColumn<CartItem,Integer> colQty;
        @FXML private TableColumn<CartItem,Double> colTotal;

        @FXML private Label lblGrandTotal;
        @FXML private TextField txtPhone;
        @FXML private TextArea txtAddress;

        @FXML
        public void initialize() {
            colName.setCellValueFactory(c ->
                    new SimpleStringProperty(c.getValue().getProduct().getName()));
            colQty.setCellValueFactory(c ->
                    new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
            colTotal.setCellValueFactory(c ->
                    new SimpleDoubleProperty(c.getValue().getTotal()).asObject());

            tableCart.setItems(CartStore.getCartItems());
            updateTotal();
           // CartStore.getCartItems().addListener((ListChangeListener<CartItem>) c -> updateTotal());

        }

        private void updateTotal() {
            double total = 0;
            for (CartItem item : CartStore.getCartItems()) {
                total += item.getTotal();
            }
            lblGrandTotal.setText("Total: $" + total);
        }

        @FXML
        private void placeOrder() {

            if (CartStore.getCartItems().isEmpty()) {
                lblGrandTotal.setText("Cart is empty!");
                return;
            }
            try (Connection conn = Database.getConnection()) {
                conn.setAutoCommit(false);

                for (CartItem item : CartStore.getCartItems()) {


                    PreparedStatement ps = conn.prepareStatement(
                            "UPDATE products SET stock = stock - ? WHERE id=?");
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProduct().getId());
                    ps.executeUpdate();


                    PreparedStatement ps2 = conn.prepareStatement(
                            "INSERT INTO orders(product_id, qty, phone, address) VALUES(?,?,?,?)");
                    ps2.setInt(1, item.getProduct().getId());
                    ps2.setInt(2, item.getQuantity());
                    ps2.setString(3, txtPhone.getText());
                    ps2.setString(4, txtAddress.getText());
                    ps2.executeUpdate();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("Thank you, Order placed successfully!");
                alert.showAndWait();
                CartStore.clear();
                updateTotal();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

