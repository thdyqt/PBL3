package Util;

import EntityDTO.OrderDetail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CartManager {
    private static CartManager instance;

    private ObservableList<OrderDetail> cartItems;

    private CartManager() {
        cartItems = FXCollections.observableArrayList();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public ObservableList<OrderDetail> getCartItems() {
        return cartItems;
    }

    public void clearCart() {
        cartItems.clear();
    }
}