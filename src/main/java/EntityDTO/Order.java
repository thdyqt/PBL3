package EntityDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Order {

    public enum orderStatus{
        Created, Waiting_for_validation, Processing, Delivering, Finished, Cancelled;
    }

    public enum orderType{
        Online, Offline;
    }

    public enum orderPayment{
        Cash, Card;
    }

    //offline -> status = {Processing, Finished}
    //online -> status = {Created, Waiting_for_validation, Processing, Delivering, Finished, Cancelled}

    //attributes
    private int id;
    private LocalDateTime process_time;
    private Staff staff;
    private Customer customer;
    private List<OrderDetail> orderDetail;
    private orderStatus status;
    private orderType type;
    private orderPayment payment;
    private int subTotal;
    private String appliedCode;
    private int discountAmount;
    private int finalAmount;


    //constructors
    public Order(){this.orderDetail = new ArrayList<>();};

    public Order(int id, LocalDateTime process_time, Staff staff, Customer customer, OrderDetail detail){
        this.id = id;
        this.process_time = process_time;
        this.staff = staff;
        this.customer = customer;
        this.orderDetail = orderDetail;
        this.status = orderStatus.Waiting_for_validation;
    }

    //get-set
    public int getId(){return id;}
    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getProcess_time(){return process_time;}
    public void setProcess_time(LocalDateTime process_time) {
        this.process_time = process_time;
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

    public orderStatus getStatus(){return status;}
    public void setStatus(orderStatus status){this.status = status;}

    public orderType getType(){return type;}
    public void setType(orderType type){this.type = type;}

    public orderPayment getPayment(){return payment;}
    public void setPayment(orderPayment payment){this.payment = payment;}

    public int getSubTotal(){return subTotal;}
    public void setSubTotal(int subTotal){this.subTotal = subTotal;}

    public int getDiscountAmount(){return discountAmount;}
    public void setDiscountAmount(int discountAmount){this.discountAmount = discountAmount;}

    public String getAppliedCode(){return appliedCode;}
    public void setAppliedCode(String appliedCode){this.appliedCode = appliedCode;}

    public int getFinalAmount(){return finalAmount;}
    public void setFinalAmount(int finalAmount){this.finalAmount = finalAmount;}
}
