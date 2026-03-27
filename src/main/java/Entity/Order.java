package Entity;

import java.time.LocalDateTime;

public class Order {
    //attributes
    private int id;
    private LocalDateTime process_time;
    private Staff staff;
    private Customer customer;
    private OrderDetail orderDetail;

    //constructors
    public Order(){};

    public Order(int id, LocalDateTime process_time, Staff staff, Customer customer, OrderDetail detail){
        this.id = id;
        this.process_time = process_time;
        this.staff = staff;
        this.customer = customer;
        this.orderDetail = detail;
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

    public OrderDetail getOrderDetail(){return orderDetail;}
    public void setOrderDetail(OrderDetail orderDetail){
        this.orderDetail = orderDetail;
    }
}
