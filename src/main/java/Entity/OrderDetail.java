package Entity;

import java.util.List;

public class OrderDetail {
    //attributes
    private List<Product> product_list;

    //constructors
    public OrderDetail(){};
    public OrderDetail(List<Product> product_list){
        this.product_list = product_list;
    }

    //get-set
    public List<Product> getProduct_list() {return product_list;}
    public void setProduct_list(List<Product> product_list){
        this.product_list = product_list;
    }
}
