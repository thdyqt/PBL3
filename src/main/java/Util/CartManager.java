package Util;

import EntityDTO.OrderDetail;
import EntityDTO.Product;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CartManager {
    private static CartManager instance;

    private ObservableList<OrderDetail> customerCart;
    private IntegerProperty customerTotalCount = new SimpleIntegerProperty(0);

    private ObservableList<OrderDetail> posCart;

    private CartManager() {
        customerCart = FXCollections.observableArrayList();
        posCart = FXCollections.observableArrayList();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // PHẦN XỬ LÝ CHO KHÁCH HÀNG (CUSTOMER)
    public ObservableList<OrderDetail> getCustomerCart() {
        return customerCart;
    }

    public IntegerProperty customerTotalCountProperty() {
        return customerTotalCount;
    }

    public boolean addToCustomerCart(Product product, int quantityToAdd) {
        for (OrderDetail item : customerCart) {
            if (item.getProduct() != null && item.getProduct().getProductID() == product.getProductID()) {
                int newQuantity = item.getQuantity() + quantityToAdd;
                if (newQuantity > product.getQuantity()) {
                    return false;
                }
                item.setQuantity(newQuantity);
                updateCustomerTotal();
                return true;
            }
        }

        OrderDetail newItem = new OrderDetail();
        newItem.setProduct(product);
        newItem.setQuantity(quantityToAdd);
        newItem.setPrice(product.getProductPrice());

        customerCart.add(newItem);
        updateCustomerTotal();
        return true;
    }

    public void clearCustomerCart() {
        customerCart.clear();
        updateCustomerTotal();
    }

    private void updateCustomerTotal() {
        int count = 0;
        for (OrderDetail item : customerCart) {
            count += item.getQuantity();
        }
        customerTotalCount.set(count);
    }

    // PHẦN XỬ LÝ CHO NHÂN VIÊN (POS STAFF)
    public ObservableList<OrderDetail> getPosCart() {
        return posCart;
    }

    public void clearPosCart() {
        posCart.clear();
    }
}