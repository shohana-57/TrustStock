package com.example.truststock.model;

import javafx.beans.property.*;

public class Order {
    private final SimpleIntegerProperty orderId;
    private final SimpleStringProperty phone;
    private final SimpleStringProperty address;
    private final SimpleStringProperty orderDate;
    private final SimpleDoubleProperty total;
    private final SimpleStringProperty items;

    public Order(int orderId, String phone, String address, String orderDate, double total, String items) {
        this.orderId = new SimpleIntegerProperty(orderId);
        this.phone = new SimpleStringProperty(phone);
        this.address = new SimpleStringProperty(address);
        this.orderDate = new SimpleStringProperty(orderDate);
        this.total = new SimpleDoubleProperty(total);
        this.items = new SimpleStringProperty(items);
    }

    public int getOrderId() { return orderId.get(); }
    public SimpleIntegerProperty orderIdProperty() { return orderId; }

    public String getPhone() { return phone.get(); }
    public SimpleStringProperty phoneProperty() { return phone; }

    public String getAddress() { return address.get(); }
    public SimpleStringProperty addressProperty() { return address; }

    public String getOrderDate() { return orderDate.get(); }
    public SimpleStringProperty orderDateProperty() { return orderDate; }

    public double getTotal() { return total.get(); }
    public SimpleDoubleProperty totalProperty() { return total; }

    public String getItems() { return items.get(); }
    public SimpleStringProperty itemsProperty() { return items; }
}
