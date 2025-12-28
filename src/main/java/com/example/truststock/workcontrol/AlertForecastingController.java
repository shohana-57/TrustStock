package com.example.truststock.workcontrol;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import com.example.truststock.db.Database;
import com.example.truststock.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class AlertForecastingController {

    public Button GoBack;
    @FXML private TableView<Product> tableAlerts;
        @FXML private TableColumn<Product, Integer> colId;
        @FXML private TableColumn<Product, String> colName;
        @FXML private TableColumn<Product, Integer> colStock;
        @FXML private TableColumn<Product, Integer> colMinStock;
        @FXML private TableColumn<Product, String> colStatus;
        @FXML private Button btnRefreshAlerts;

        private final ObservableList<Product> alertList = FXCollections.observableArrayList();

        @FXML private LineChart<String, Number> salesChart;
        @FXML private Button btnRefreshForecast;

        @FXML
        public void initialize() {

            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(new PropertyValueFactory<>("name"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
            colMinStock.setCellValueFactory(new PropertyValueFactory<>("minStock"));
            colStatus.setCellValueFactory(new PropertyValueFactory<>("qualityStatus"));


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
                    Product p = new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getInt("stock"),
                            rs.getInt("min_stock"),
                            rs.getString("quality_status"),
                            rs.getString("image_path")
                    );
                    alertList.add(p);
                }

                tableAlerts.setItems(alertList);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Failed to load alerts.");
            }
        }

        @FXML
        private void loadForecast() {
            salesChart.getData().clear();

            String sql = "SELECT strftime('%Y-%m', sale_date) as month, SUM(quantity) as total FROM sales GROUP BY month ORDER BY month";

            try (Connection conn = Database.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Sales");

                while (rs.next()) {
                    String month = rs.getString("month");
                    int total = rs.getInt("total");
                    series.getData().add(new XYChart.Data<>(month, total));
                }

                salesChart.getData().add(series);

            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Failed to load forecast data.");
            }
        }

        private void showAlert(String msg) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText(msg);
            alert.show();
        }


    public void OnDashboard(ActionEvent actionEvent) throws IOException {
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
