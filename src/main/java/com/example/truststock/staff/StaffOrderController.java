package com.example.truststock.staff;

import com.example.truststock.db.Database;
import com.example.truststock.model.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class StaffOrderController {

    @FXML private TableView<Order> tableOrders;
    @FXML private TableColumn<Order, Integer> colOrderId;
    @FXML private TableColumn<Order, String> colPhone;
    @FXML private TableColumn<Order, String> colAddress;
    @FXML private TableColumn<Order, String> colOrderDate;
    @FXML private TableColumn<Order, Double> colTotal;
    @FXML private TableColumn<Order, String> colItems;

    private final ObservableList<Order> ordersList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(c -> c.getValue().orderIdProperty().asObject());
        colPhone.setCellValueFactory(c -> c.getValue().phoneProperty());
        colAddress.setCellValueFactory(c -> c.getValue().addressProperty());
        colOrderDate.setCellValueFactory(c -> c.getValue().orderDateProperty());
        colTotal.setCellValueFactory(c -> c.getValue().totalProperty().asObject());
        colItems.setCellValueFactory(c -> c.getValue().itemsProperty());

        loadOrders();
    }

    private void loadOrders() {
        ordersList.clear();

        String sql = """
            SELECT o.id, o.phone, o.address, o.order_date, o.total
            FROM orders o
            WHERE o.delivered = 0
            ORDER BY o.order_date ASC
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int orderId = rs.getInt("id");
                String phone = rs.getString("phone");
                String address = rs.getString("address");
                String orderDate = rs.getString("order_date");
                double total = rs.getDouble("total");


                String itemsSql = """
                    SELECT p.name, oi.qty 
                    FROM order_items oi
                    JOIN products p ON oi.product_id = p.id
                    WHERE oi.order_id = ?
                """;
                ArrayList<String> productList = new ArrayList<>();
                try (PreparedStatement ps2 = conn.prepareStatement(itemsSql)) {
                    ps2.setInt(1, orderId);
                    try (ResultSet rs2 = ps2.executeQuery()) {
                        while (rs2.next()) {
                            productList.add(rs2.getString("name") + " x" + rs2.getInt("qty"));
                        }
                    }
                }
                String itemsString = String.join(", ", productList);

                ordersList.add(new Order(orderId, phone, address, orderDate, total, itemsString));
            }

            tableOrders.setItems(ordersList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void markDelivered() {
        Order selected = tableOrders.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE orders SET delivered = 1 WHERE id = ?")) {

            ps.setInt(1, selected.getOrderId());
            ps.executeUpdate();

            ordersList.remove(selected);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void refreshOrders() {
        loadOrders();
    }
}
