//only interact with OrderBusiness together to make a complete order
//fyi: an order is made up of Order (when/id/status of the order)
//and OrderDetail (the content of the order)
package BusinessBLL;

import EntityDTO.Order;
import EntityDTO.OrderDetail;
import DataDAL.OrderDetailData;
import java.util.List;

public class OrderDetailBusiness {
    public static boolean saveOrderDetail(List<OrderDetail> itemList, int createdOrderID){
        //link the order with its content
        for (OrderDetail item : itemList){
            Order linkedOrder = new Order();
            linkedOrder.setId(createdOrderID);
            item.setOrder(linkedOrder);

            boolean isSaved = OrderDetailData.addProduct_OrderDetail(item);

            if (!isSaved){
                return false;
            }
        }
        return true;
    }
}
