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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            loadProducts(newVal.trim());
        });
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
        if (imgPath != null && !imgPath.isBlank() && new File(imgPath).exists()) {
            image = new Image(new File(imgPath).toURI().toString());
        } else {
            image = new Image(getClass().getResourceAsStream(
                    "/com/example/truststock/images/default.png"));
        }

        ImageView img = new ImageView(image);
        img.setFitWidth(180);
        img.setFitHeight(150);
        img.setPreserveRatio(true);

        Label name = new Label(product.getName());
        name.setStyle("-fx-font-weight:bold;");

        Label price = new Label("$ " + product.getPrice());
        Label stock = new Label(product.getStock() > 0 ? "In Stock" : "Out of Stock");


        double avgRating = getAverageRating(product.getId());

        Label ratingText = new Label(
                avgRating == 0 ? "No ratings yet"
                        : String.format("%.1f / 5", avgRating)
        );

        HBox starBox = createStarRating(avgRating);


        Button rateBtn = new Button("Rate");
        rateBtn.setOnAction(e -> {

            ChoiceDialog<Integer> dialog =
                    new ChoiceDialog<>(5, 1, 2, 3, 4, 5);

            dialog.setTitle("Rate Product");
            dialog.setHeaderText("Rate " + product.getName());
            dialog.setContentText("Stars:");

            dialog.showAndWait().ifPresent(rating -> {

                saveRating(product.getId(), rating);

                double newAvg = getAverageRating(product.getId());

                ratingText.setText(String.format("%.1f / 5", newAvg));

                starBox.getChildren().setAll(
                        createStarRating(newAvg).getChildren()
                );
            });
        });

        Button addToCart = new Button("Add to Cart");
        addToCart.setDisable(product.getStock() <= 0);
        addToCart.setOnAction(e -> askQuantityAndAddToCart(product));

        card.getChildren().addAll(
                img, name, price, stock, ratingText, rateBtn, addToCart
        );

        return card;
    }


    private void askQuantityAndAddToCart(Product product) {
        if (product.getStock() <= 0) {
            new Alert(Alert.AlertType.WARNING, "Product is out of stock").show();
            return;
        }

        TextInputDialog quantityDialog = new TextInputDialog("1");
        quantityDialog.setTitle("Select Quantity");
        quantityDialog.setHeaderText("How many " + product.getName() + " would you like to add?");
        quantityDialog.setContentText("Quantity:");

        quantityDialog.showAndWait().ifPresent(qtyStr -> {
            try {
                int qty = Integer.parseInt(qtyStr);

                if (qty <= 0) {
                    new Alert(Alert.AlertType.WARNING, "Quantity must be at least 1").show();
                } else if (qty > product.getStock()) {
                    new Alert(Alert.AlertType.WARNING, "Only " + product.getStock() + " item(s) available in stock").show();
                } else {

                    CartStore.add(product, qty);
                    new Alert(Alert.AlertType.INFORMATION, qty + " item(s) added to cart").show();
                }
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Please enter a valid number").show();
            }
        });
    }


    private double getAverageRating(int productId) {
        String sql = "SELECT AVG(rating) FROM product_ratings WHERE product_id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void saveRating(int productId, int rating) {
        String sql = "INSERT INTO product_ratings(product_id, rating, rating_date) VALUES(?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setInt(2, rating);
            ps.setString(3, java.time.LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createStarRating(double rating) {

        HBox stars = new HBox(2);

        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;

        for (int i = 1; i <= 5; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-font-size:16;");

            if (i <= fullStars) {
                star.setStyle("-fx-font-size:16; -fx-text-fill:gold;");
            } else if (i == fullStars + 1 && halfStar) {
                star.setStyle("-fx-font-size:16; -fx-text-fill:orange;");
            } else {
                star.setStyle("-fx-font-size:16; -fx-text-fill:#ccc;");
            }

            stars.getChildren().add(star);
        }

        return stars;
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

    private void saveComment(int productId, String comment) {
        String sql = "INSERT INTO product_comments(product_id, comment_text, comment_date) VALUES(?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, comment);
            ps.setString(3, java.time.LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to save comment").show();
        }
    }

}
