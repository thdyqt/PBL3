//unlike OrderDetailBusiness, this one is used by the GUI directly
//hence why the return type are all String so the one who actually use this can know what is the exact problem
package BusinessBLL;

import EntityDTO.Order;
import EntityDTO.OrderDetail;
import DataDAL.OrderData;
import java.time.LocalDateTime;
import java.util.List;

public class OrderBusiness {
    public static String addOrder(Order order, List<OrderDetail> orderDetailList){
        //error checking
        if (orderDetailList == null || orderDetailList.isEmpty()) {
            return "List doesn't exist/ list is empty";
        }

        if (order.getStaff() == null || order.getStaff().getId() <= 0){
            return "Staff doesn't exist/ staff with invalid Id";
        }

        order.setProcess_time(LocalDateTime.now());
        order.setStatus(EntityDTO.Order.orderStatus.Created);

        int createdOrderID = OrderData.addOrder(new Order());
        if (createdOrderID <= 0){
            return "Order created with invalid ID";
        }

        boolean itemSaved = OrderDetailBusiness.saveOrderDetail(orderDetailList, createdOrderID);
        if (!itemSaved){
            return "Save failed";
        }

        return "OrderBusiness success";
    }
}
