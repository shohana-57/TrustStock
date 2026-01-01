package com.example.truststock.staff;

import javafx.beans.property.*;

public class CommentItem {
    private IntegerProperty id;
    private StringProperty productName;
    private StringProperty comment;
    private StringProperty date;

    public CommentItem(int id, String productName, String comment, String date) {
        this.id = new SimpleIntegerProperty(id);
        this.productName = new SimpleStringProperty(productName);
        this.comment = new SimpleStringProperty(comment);
        this.date = new SimpleStringProperty(date);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getProductName() { return productName.get(); }
    public StringProperty productNameProperty() { return productName; }

    public String getComment() { return comment.get(); }
    public StringProperty commentProperty() { return comment; }

    public String getDate() { return date.get(); }
    public StringProperty dateProperty() { return date; }
}
