package com.example.truststock.model;

public class Product {
        private int id;
        private String name;
        private double price;
        private int stock;
        private int minStock;
        private String qualityStatus;
    private String imagePath;


    public Product(int id, String name, double price, int stock, int minStock, String qualityStatus, String imagePath) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.minStock = minStock;
            this.qualityStatus = qualityStatus;
        this.imagePath = imagePath;
    }


        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }

        public int getMinStock() { return minStock; }
        public void setMinStock(int minStock) { this.minStock = minStock; }

        public String getQualityStatus() { return qualityStatus; }
        public void setQualityStatus(String qualityStatus) { this.qualityStatus = qualityStatus; }

      public String getImagePath() {
    return imagePath;
         }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

    }


