//package com.example.truststock.workcontrol;
//
//import com.example.truststock.db.Database;
//import com.example.truststock.model.Product;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.chart.LineChart;
//import javafx.scene.chart.XYChart;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.stage.Stage;
//import javafx.fxml.FXMLLoader;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//
//public class AlertForecastingController {
//
//    @FXML private TableView<Product> tableAlerts;
//    @FXML private TableColumn<Product, Integer> colId;
//    @FXML private TableColumn<Product, String> colName;
//    @FXML private TableColumn<Product, Integer> colStock;
//    @FXML private TableColumn<Product, Integer> colMinStock;
//    @FXML private TableColumn<Product, String> colStatus;
//    @FXML private Button btnRefreshAlerts;
//    @FXML private LineChart<String, Number> salesChart;
//    @FXML private Button btnRefreshForecast;
//    @FXML private Button GoBack;
//
//    private final ObservableList<Product> alertList = FXCollections.observableArrayList();
//
//    @FXML
//    public void initialize() {
//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
//        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
//        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
//        colMinStock.setCellValueFactory(new PropertyValueFactory<>("minStock"));
//        colStatus.setCellValueFactory(new PropertyValueFactory<>("qualityStatus"));
//
//        loadAlerts();
//        loadForecast();
//    }
//
//    /** Load products which are below minimum stock */
//    @FXML
//    private void loadAlerts() {
//        alertList.clear();
//        String sql = "SELECT * FROM products WHERE stock <= min_stock";
//
//        try (Connection conn = Database.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            while (rs.next()) {
//                Product p = new Product(
//                        rs.getInt("id"),
//                        rs.getString("name"),
//                        rs.getDouble("price"),
//                        rs.getInt("stock"),
//                        rs.getInt("min_stock"),
//                        rs.getString("quality_status"),
//                        rs.getString("image_path")
//                );
//                alertList.add(p);
//            }
//            tableAlerts.setItems(alertList);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Failed to load alerts.");
//        }
//    }
//
//    /** Load sales forecast */
//    @FXML
//    private void loadForecast() {
//        salesChart.getData().clear();
//
//        String sql = "SELECT strftime('%Y-%m', sale_date) as month, SUM(quantity) as total " +
//                "FROM sales GROUP BY month ORDER BY month";
//
//        try (Connection conn = Database.getConnection();
//             PreparedStatement ps = conn.prepareStatement(sql);
//             ResultSet rs = ps.executeQuery()) {
//
//            XYChart.Series<String, Number> series = new XYChart.Series<>();
//            series.setName("Sales");
//
//            while (rs.next()) {
//                String month = rs.getString("month");
//                int total = rs.getInt("total");
//                series.getData().add(new XYChart.Data<>(month, total));
//            }
//            salesChart.getData().add(series);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Failed to load forecast data.");
//        }
//    }
//
//    private void showAlert(String msg) {
//        Alert alert = new Alert(Alert.AlertType.WARNING);
//        alert.setHeaderText(null);
//        alert.setContentText(msg);
//        alert.show();
//    }
//
//
//    public void OnDashboard(ActionEvent actionEvent) {
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("/com/example/truststock/staff_dashboard.fxml"));
//            Stage stage = (Stage) GoBack.getScene().getWindow();
//            stage.setScene(new Scene(root));
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlert("Failed to go back to dashboard.");
//        }
//    }
//}

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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AlertForecastingController {

    public Button btnRefreshForecast;
    public Button GoBack;
    public Button btnRefreshAlerts;
    @FXML private TableView<Product> tableAlerts;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private TableColumn<Product, Integer> colMinStock;
    @FXML private TableColumn<Product, String> colStatus;
    @FXML private LineChart<String, Number> salesChart;

    private final ObservableList<Product> alertList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> c.getValue().idProperty().asObject());
        colName.setCellValueFactory(c -> c.getValue().nameProperty());
        colStock.setCellValueFactory(c -> c.getValue().stockProperty().asObject());
        colMinStock.setCellValueFactory(c -> c.getValue().minStockProperty().asObject());
        colStatus.setCellValueFactory(c -> c.getValue().qualityStatusProperty());

        loadAlerts();
        loadForecast();
    }

    @FXML
    private void loadAlerts() {
        alertList.clear();
        String sql = "SELECT * FROM products WHERE stock <= min_stock";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alertList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min_stock"),
                        rs.getString("quality_status"),
                        rs.getString("image_path")
                ));
            }

            tableAlerts.setItems(alertList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadForecast() {
        salesChart.getData().clear();
        String sql = "SELECT p.name, strftime('%Y-%m', s.sale_date) AS month, SUM(s.quantity) AS total " +
                "FROM sales s JOIN products p ON s.product_id = p.id " +
                "GROUP BY p.name, month ORDER BY p.name, month";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("6-Month Sales Forecast");

            while (rs.next()) {
                String product = rs.getString("name");
                String month = rs.getString("month");
                int total = rs.getInt("total");


                series.getData().add(new XYChart.Data<>(month + " - " + product, total));
            }

            salesChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDashboard(ActionEvent actionEvent)throws IOException {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/truststock/staff_dashboard.fxml")
            );
            Stage stage = (Stage) GoBack.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
