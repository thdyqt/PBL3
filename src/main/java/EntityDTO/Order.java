package EntityDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Order {
    public void rs() {

    }

    public enum orderStatus{
        Waiting_for_validation, Processing, Delivering, Finished, Cancelled;
    }

    //attributes
    private int id;
    private LocalDateTime process_time;
    private Staff staff;
    private Customer customer;
    private List<OrderDetail> orderDetail;
    private orderStatus status;

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
}
