//package com.example.truststock.model;
//
//public class Product {
//        private int id;
//        private String name;
//        private double price;
//        private int stock;
//        private int minStock;
//        private String qualityStatus;
//    private String imagePath;
//
//
//    public Product(int id, String name, double price, int stock, int minStock, String qualityStatus, String imagePath) {
//            this.id = id;
//            this.name = name;
//            this.price = price;
//            this.stock = stock;
//            this.minStock = minStock;
//            this.qualityStatus = qualityStatus;
//        this.imagePath = imagePath;
//    }
//
//
//        public int getId() { return id; }
//        public void setId(int id) { this.id = id; }
//
//        public String getName() { return name; }
//        public void setName(String name) { this.name = name; }
//
//        public double getPrice() { return price; }
//        public void setPrice(double price) { this.price = price; }
//
//        public int getStock() { return stock; }
//        public void setStock(int stock) { this.stock = stock; }
//
//        public int getMinStock() { return minStock; }
//        public void setMinStock(int minStock) { this.minStock = minStock; }
//
//        public String getQualityStatus() { return qualityStatus; }
//        public void setQualityStatus(String qualityStatus) { this.qualityStatus = qualityStatus; }
//
//      public String getImagePath() {
//    return imagePath;
//         }
//
//        public void setImagePath(String imagePath) {
//            this.imagePath = imagePath;
//        }
//
//    }
//
//
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
