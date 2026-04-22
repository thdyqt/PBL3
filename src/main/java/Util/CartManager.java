package Util;

import DataDAL.CartData;
import EntityDTO.Customer;
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
    private Customer currentCustomer = null;
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

    public void setCustomerCart(ObservableList<OrderDetail> customerCart) {
        this.customerCart = customerCart;
    }

    public IntegerProperty customerTotalCountProperty() {
        return customerTotalCount;
    }

    public void loadCartOnLogin(int customerID) {
        customerCart.clear();
        if (customerID > 0) {
            customerCart.addAll(CartData.loadCustomerCart(customerID));
        }
        updateCustomerTotal();
    }

    public boolean addToCustomerCart(int customerID, Product product, int quantityToAdd) {
        for (OrderDetail item : customerCart) {
            if (item.getProduct() != null && item.getProduct().getProductID() == product.getProductID()) {
                int newQuantity = item.getQuantity() + quantityToAdd;
                if (newQuantity > product.getQuantity()) {
                    return false;
                }
                item.setQuantity(newQuantity);
                updateCustomerTotal();

                if (customerID > 0) {
                    DataDAL.CartData.saveCartItem(customerID, product.getProductID(), newQuantity);
                }
                return true;
            }
        }

        OrderDetail newItem = new OrderDetail();
        newItem.setProduct(product);
        newItem.setQuantity(quantityToAdd);
        newItem.setPrice(product.getProductPrice());

        customerCart.add(newItem);
        updateCustomerTotal();

        if (customerID > 0) {
            DataDAL.CartData.saveCartItem(customerID, product.getProductID(), quantityToAdd);
        }
        return true;
    }

    public void removeCustomerCartItem(int customerID, Product product) {
        customerCart.removeIf(item -> item.getProduct().getProductID() == product.getProductID());
        updateCustomerTotal();

        if (customerID > 0) {
            DataDAL.CartData.removeCartItem(customerID, product.getProductID());
        }
    }

    public void clearCustomerCart(int customerID) {
        customerCart.clear();
        updateCustomerTotal();

        if (customerID > 0) {
            DataDAL.CartData.clearCart(customerID);
        }
    }

    public void clearCartOnLogout() {
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
    public Customer getCurrentCustomer() { return currentCustomer; }

    public void setCurrentCustomer(Customer currentCustomer) { this.currentCustomer = currentCustomer; }

    public ObservableList<OrderDetail> getPosCart() {
        return posCart;
    }

    public void clearPosCart() {
        posCart.clear();
        CartManager.getInstance().setCurrentCustomer(null);
    }
}