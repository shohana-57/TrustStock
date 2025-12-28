package com.example.truststock.customer;

import com.example.truststock.db.Database;
import com.example.truststock.model.Product;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;

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

        String sql = "SELECT * FROM products WHERE quality_status='GOOD'";
        if (!keyword.isEmpty()) {
            sql += " AND name LIKE '%" + keyword + "%'";
        }

        try (Connection conn = Database.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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

                productContainer.getChildren().add(createProductCard(p));
            }

        } catch (Exception e) {
            e.printStackTrace();
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



        Label name = new Label(product.getName());
        name.setStyle("-fx-font-weight:bold;");

        Label price = new Label("$ " + product.getPrice());
        Label stock = new Label (product.getStock() > 0? "IN Stock" : "Out of Stock");
        Label rating = new Label("");

        Button addToCart = new Button("Add to Cart");
        addToCart.setDisable(product.getStock() <= 0);

        card.getChildren().addAll(img, name, price, stock,rating, addToCart);
        return card;
    }

    @FXML
    private void searchProduct() {
        loadProducts(txtSearch.getText());
    }

    @FXML
    private void openCart() {
        System.out.println("Open Cart");
    }
}
