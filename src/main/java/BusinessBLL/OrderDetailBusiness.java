//only interact with OrderBusiness together to make a complete order
//fyi: an order is made up of Order (when/id/status of the order)
//and OrderDetail (the content of the order)
package BusinessBLL;

import DataDAL.OrderDetailData;
import EntityDTO.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailBusiness {
    public static List<OrderDetail> getDetailsByOrderId_BLL(int id_Order) {
        if (id_Order <= 0) {
            return new ArrayList<>();
        }
        return OrderDetailData.searchOrderDetail_ById_Order(id_Order);
    }

    public static List<OrderDetail> getDetailsByProductId_BLL(int id_Product) {
        if (id_Product <= 0) {
            return new ArrayList<>();
        }
        return OrderDetailData.searchOrderDetail_ById_Product(id_Product);
    }
}
