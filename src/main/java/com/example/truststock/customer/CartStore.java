package com.example.truststock.customer;

import com.example.truststock.model.CartItem;
import com.example.truststock.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CartStore {

    private static final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    public static ObservableList<CartItem> getCartItems() {
        return cartItems;
    }

    public static void add(Product product, int quantity) {

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        cartItems.add(new CartItem(product, quantity));
    }

    public static void clear() {
        cartItems.clear();
    }
}
