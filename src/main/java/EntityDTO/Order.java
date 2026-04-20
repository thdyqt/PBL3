package EntityDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Order {

    public enum OrderStatus {
        Waiting_for_validation, Processing, Delivering, Finished, Cancelled;
    }

    public enum OrderType {
        Online, Offline;
    }

    public enum OrderPayment {
        Cash, Card;
    }

    //offline -> status = {Finished}
    //online -> status = {Waiting_for_validation, Processing, Delivering, Finished, Cancelled}

    //attributes
    private int id;
    private LocalDateTime orderTime;
    private Staff staff;
    private Customer customer;
    private List<OrderDetail> orderDetail;
    private OrderStatus status;
    private OrderType type;
    private OrderPayment payment;
    private int subTotal;
    private String appliedCode;
    private int discountAmount;
    private int finalAmount;
    private String address;
    private String cancelReason;

    //constructors
    public Order(){this.orderDetail = new ArrayList<>();}

    public Order(int id, LocalDateTime orderTime, Staff staff, Customer customer, List<OrderDetail> orderDetailList){
        this.id = id;
        this.orderTime = orderTime;
        this.staff = staff;
        this.customer = customer;
        this.orderDetail = orderDetailList;
        this.status = OrderStatus.Waiting_for_validation;
    }

    //get-set
    public int getId(){return id;}
    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getOrderTime(){return orderTime;}
    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public Staff getStaff(){return staff;}
    public void setStaff(Staff staff){
        this.staff = staff;
    }

    public Customer getCustomer(){return customer;}
    public void setCustomer(Customer customer){
        this.customer = customer;
    }

    public List<OrderDetail> getOrderDetail(){return orderDetail;}
    public void setOrderDetail(List<OrderDetail> orderDetail){
        this.orderDetail = orderDetail;
    }

    public OrderStatus getStatus(){return status;}
    public void setStatus(OrderStatus status){this.status = status;}

    public OrderType getType(){return type;}
    public void setType(OrderType type){this.type = type;}

    public OrderPayment getPayment(){return payment;}
    public void setPayment(OrderPayment payment){this.payment = payment;}

    public int getSubTotal(){return subTotal;}
    public void setSubTotal(int subTotal){this.subTotal = subTotal;}

    public int getDiscountAmount(){return discountAmount;}
    public void setDiscountAmount(int discountAmount){this.discountAmount = discountAmount;}

    public String getAppliedCode(){return appliedCode;}
    public void setAppliedCode(String appliedCode){this.appliedCode = appliedCode;}

    public int getFinalAmount(){return finalAmount;}
    public void setFinalAmount(int finalAmount){this.finalAmount = finalAmount;}

    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}

    public String getCancelReason(){return cancelReason;}
    public void setCancelReason(String cancelReason){this.cancelReason = cancelReason;}
}
