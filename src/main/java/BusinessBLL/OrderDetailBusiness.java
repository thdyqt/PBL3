//only interact with OrderBusiness together to make a complete order
//fyi: an order is made up of Order (when/id/status of the order)
//and OrderDetail (the content of the order)
package BusinessBLL;

import DataDAL.OrderData;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import DataDAL.OrderDetailData;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailBusiness {
    //create
    public static boolean saveOrderDetail_BLL(List<OrderDetail> itemList, int createdOrderID){
        //sanity check
        if (itemList == null || itemList.isEmpty()) {
            return false;
        }

        //link the order with its content
        for (OrderDetail item : itemList){
            Order linkedOrder = new Order();
            linkedOrder.setId(createdOrderID);
            item.setOrder(linkedOrder);

            //the item either:
                //doesnt exist
                //have invalid id
                //have quanity of 0 or less
            //is skipped
            if (item.getProduct() == null || item.getProduct().getProductID() <= 0 || item.getProduct().getQuantity() <= 0){
                continue;
            }

            boolean isSaved = OrderDetailData.addProduct_OrderDetail(item);
            if (!isSaved){
                return false;
            }
        }
        return true;
    }

    //read
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

    //update
    public static String updateQuanity(int updatedQuanity, int id_Order, int id_Product){
        //sanity check
        if (id_Order <= 0 || id_Product <= 0){
            return "ERROR: Invalid order id/ product id";
        }

        if (updatedQuanity < 0){
            return "ERROR: Quanity cant be less than 0";
        }

        //setting quanity = 0 ~ deleting an item from the order content
        if (updatedQuanity == 0){
            return deleteSingleItemFromOrder_BLL(id_Order, id_Product);
        }

        boolean updateSuccessfull = OrderDetailData.updateOrderDetail(updatedQuanity, id_Order, id_Product);

        if (updateSuccessfull){
            return "Successfully changed item quanity of item with id: " + id_Product + "to: " + updatedQuanity + "from order with id: " + id_Order;
        }else {
            return "ERROR: Failure to update item quanity with id: " + id_Product + "from order with id: " + id_Order;
        }
    }

    //delete
    public static boolean deleteALLItemsFromOrder_BLL(int id_Order){
        if (id_Order <= 0){
            return false;
        }

        return OrderDetailData.deleteALLItemsFromOrder(id_Order);
    }

    public static String deleteSingleItemFromOrder_BLL(int id_Order, int id_Product){
        boolean deleteSuccessful = OrderDetailData.deleteOrderDetail_1_Product(id_Order, id_Product);

        if (deleteSuccessful){
            return "Successfully deleted item with id: " + id_Product + "from order with id: " + id_Order;
        }else {
            return "ERROR: Failure to delete item with id: " + id_Product + "from order with id: " + id_Order;
        }
    }

    //update, but more extreme in case you need to add/delete items from its content
    //why?
    //i dont want to call add/update/delete a thousandfold thats why
    public static String updateEntireOrder(int id_Order, List<OrderDetail> content){
        //sanity check
        if (id_Order <= 0){
            return "ERROR: order have invalid id";
        }

        boolean wipeSucessfull = deleteALLItemsFromOrder_BLL(id_Order);
        if (!wipeSucessfull){
            return "ERROR: failure to wipe order with id: " + id_Order + "'s content";
        }

        if (content == null || content.isEmpty()){
            return "Successfully wiped content of order with id: " + id_Order;
        }

        boolean saved = saveOrderDetail_BLL(content, id_Order);
        if (saved){
            return "Successfully update order with id: " + id_Order;
        }else {
            return "ERROR: failure to update order with id: " + id_Order;
        }

    }



}
