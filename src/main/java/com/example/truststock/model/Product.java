
package com.example.truststock.model;

import javafx.beans.property.*;

public class Product {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty name;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty stock;
    private final SimpleIntegerProperty minStock;
    private final SimpleStringProperty qualityStatus;
    private final SimpleStringProperty imagePath;

    public Product(int id, String name, double price, int stock, int minStock, String qualityStatus, String imagePath) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.minStock = new SimpleIntegerProperty(minStock);
        this.qualityStatus = new SimpleStringProperty(qualityStatus);
        this.imagePath = new SimpleStringProperty(imagePath);
    }

    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public double getPrice() { return price.get(); }
    public int getStock() { return stock.get(); }
    public int getMinStock() { return minStock.get(); }
    public String getQualityStatus() { return qualityStatus.get(); }
    public String getImagePath() { return imagePath.get(); }

    public SimpleIntegerProperty idProperty() { return id; }
    public SimpleStringProperty nameProperty() { return name; }
    public SimpleDoubleProperty priceProperty() { return price; }
    public SimpleIntegerProperty stockProperty() { return stock; }
    public SimpleIntegerProperty minStockProperty() { return minStock; }
    public SimpleStringProperty qualityStatusProperty() { return qualityStatus; }
    public SimpleStringProperty imagePathProperty() { return imagePath; }
}
