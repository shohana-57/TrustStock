package com.example.truststock.customer;
import com.example.truststock.db.Database;
import com.example.truststock.model.CartItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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

                double grandTotal = 0;
                for (CartItem item : CartStore.getCartItems()) {
                    grandTotal += item.getTotal();
                }

                PreparedStatement orderPs = conn.prepareStatement(
                        "INSERT INTO orders(phone, address, total, order_date, delivered) VALUES(?,?,?,?,0)",
                        Statement.RETURN_GENERATED_KEYS
                );
                orderPs.setString(1, txtPhone.getText());
                orderPs.setString(2, txtAddress.getText());
                orderPs.setDouble(3, grandTotal);
                orderPs.setString(4, java.time.LocalDateTime.now().toString());
                orderPs.executeUpdate();

                ResultSet rs = orderPs.getGeneratedKeys();
                rs.next();
                int orderId = rs.getInt(1);

                PreparedStatement itemPs = conn.prepareStatement(
                        "INSERT INTO order_items(order_id, product_id, qty, price) VALUES(?,?,?,?)"
                );

                for (CartItem item : CartStore.getCartItems()) {

                    itemPs.setInt(1, orderId);
                    itemPs.setInt(2, item.getProduct().getId());
                    itemPs.setInt(3, item.getQuantity());
                    itemPs.setDouble(4, item.getProduct().getPrice());
                    itemPs.executeUpdate();

                    PreparedStatement stockPs = conn.prepareStatement(
                            "UPDATE products SET stock = stock - ? WHERE id = ?"
                    );
                    stockPs.setInt(1, item.getQuantity());
                    stockPs.setInt(2, item.getProduct().getId());
                    stockPs.executeUpdate();
                }

                conn.commit();

                new Alert(Alert.AlertType.INFORMATION, "Order placed successfully!").showAndWait();
                CartStore.clear();
                updateTotal();

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to place order").show();
            }
        }


    }

