//unlike OrderDetailBusiness, this one is used by the GUI directly
//hence why the return type are all String so the one who actually use this can know what is the exact problem
package BusinessBLL;

import DataDAL.OrderDetailData;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import DataDAL.OrderData;
import java.time.LocalDateTime;
import java.util.List;

public class OrderBusiness {
    public static String addOrder_BLL(Order order, List<OrderDetail> orderDetailList){
        //error checking
        if (orderDetailList == null || orderDetailList.isEmpty()) {
            return "ERROR: List doesn't exist/ list is empty";
        }

        if (order.getStaff() == null || order.getStaff().getId() <= 0){
            return "ERROR: Staff doesn't exist/ staff with invalid Id";
        }

        order.setProcess_time(LocalDateTime.now());
        order.setStatus(EntityDTO.Order.orderStatus.Created);

        int createdOrderID = OrderData.addOrder(new Order());
        if (createdOrderID <= 0){
            return "ERROR: Order created with invalid ID";
        }

        boolean itemSaved = OrderDetailBusiness.saveOrderDetail_BLL(orderDetailList, createdOrderID);
        if (!itemSaved){
            return "ERROR: Save failed";
        }

        return "OrderBusiness success";
    }

    public static List<Order> getAllOrder_BLL(){

    }

    public static List<Order> searchOrder_BLL(){

    }

    public static String updateOrder_BLL(Order order){

    }

    public static String deleteOrder_BLL(int OrderID){
        Order foundOrder = OrderData.searchOrder_ByID(OrderID);

        if (foundOrder == null){
            return "ERROR: Order doesnt exist";
        }

        boolean orderDetailDeleted = OrderDetailBusiness.deleteALLItemsFromOrder_BLL(OrderID);
        if (!orderDetailDeleted){
            return "ERROR: OrderDetail deletion failed";
        }

        boolean orderDeleted = OrderData.deleteOrder(OrderID);
        if (!orderDeleted){
            return "ERROR: Order deletion failed";
        }
        else{
            return "Sucessfully deleted order with id: " + OrderID;
        }
    }

}
