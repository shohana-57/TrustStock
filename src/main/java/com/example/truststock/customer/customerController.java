package com.example.truststock.customer;

import com.example.truststock.db.Database;
import com.example.truststock.model.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class customerController {

    public TextField searchField;
    public Label lblNoResult;
    @FXML private FlowPane productContainer;
    @FXML private TextField txtSearch;

    @FXML
    public void initialize() {

        loadProducts("");
    }

    private void loadProducts(String keyword) {
        productContainer.getChildren().clear();
        lblNoResult.setVisible(false);

        String sql = "SELECT * FROM products WHERE quality_status='GOOD' and stock > 0 ";
        if (!keyword.isEmpty()) {
            sql += " AND name LIKE '%" + keyword + "%'";
        }

        boolean found = false;

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                found = true;
                Product p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"),
                        rs.getInt("min_stock"),
                        rs.getString("quality_status"),
                        rs.getString("image_path")
                );

                productContainer.getChildren().add(createProductCard(p));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!found) {
            lblNoResult.setVisible(true);
        }
    }

    private VBox createProductCard(Product product) {

        VBox card = new VBox(8);
        card.setPrefWidth(200);
        card.setStyle("""
        -fx-background-color:white;
        -fx-padding:10;
        -fx-border-radius:8;
        -fx-background-radius:8;
        -fx-border-color:#ddd;
    """);
        Image image;
        String imgPath = product.getImagePath();

        if (imgPath != null && !imgPath.isBlank()) {
            File f = new File(imgPath);
            if (f.exists()) {
                image = new Image(f.toURI().toString());
            } else {
                image = new Image(getClass().getResourceAsStream("/com/example/truststock/images/default.png"));
            }
        } else {
            image = new Image(getClass().getResourceAsStream("/com/example/truststock/images/default.png"));
        }

        ImageView img = new ImageView(image);
        img.setFitWidth(180);
        img.setFitHeight(150);
       img.setPreserveRatio(true);

        Label name = new Label(product.getName());
        name.setStyle("-fx-font-weight:bold;");

        Label price = new Label("$ " + product.getPrice());
        Label stock = new Label(
                product.getStock() > 0 ? "In Stock" : "Out of Stock"
        );

        Button addToCart = new Button("Add to Cart");
        addToCart.setDisable(product.getStock() <= 0);


        card.getChildren().addAll(img, name, price, stock, addToCart);
        addToCart.setOnAction(e -> {
            CartStore.add(product);
            new Alert(Alert.AlertType.INFORMATION, "Added to cart").show();
        });

        return card;

    }

    @FXML
    private void searchProduct() {
        String keyword = searchField.getText().trim();
        loadProducts(keyword);
    }

    @FXML
    private void openCart() {
        System.out.println("Open Cart");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/truststock/cart_page.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Your Cart");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
