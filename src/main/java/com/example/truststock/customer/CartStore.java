package com.example.truststock.customer;

import com.example.truststock.model.CartItem;
import com.example.truststock.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CartStore {

    private static final ObservableList<CartItem> cart =
            FXCollections.observableArrayList();

    public static ObservableList<CartItem> getCartItems() {
        return cart;
    }

    public static void add(Product product) {
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
               // item.increaseQty();
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cart.add(new CartItem(product, 1));
    }

    public static void clear() {
        cart.clear();
    }
}
