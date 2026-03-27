package Entity;

import java.time.LocalDateTime;

public class Order {
    //attributes
    private int id;
    private LocalDateTime process_time;
    private Staff staff;
    private Customer customer;
    private OrderDetail detail;

    //constructors
    public Order(){};

    public Order(int id, LocalDateTime process_time, Staff staff, Customer customer, OrderDetail detail){
        this.id = id;
        this.process_time = process_time;
        this.staff = staff;
        this.customer = customer;
        this.detail = detail;
    }



}
