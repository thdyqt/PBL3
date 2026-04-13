//unlike OrderDetailBusiness, this one is used by the GUI directly
//hence why the return type are all String so the one who actually use this can know what is the exact problem
package BusinessBLL;

import DataDAL.OrderDetailData;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import DataDAL.OrderData;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

        int createdOrderID = OrderData.addOrder(order);
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
        return OrderData.getAllOrders();
    }

    public static List<Order> searchOrder_BLL(String keyword, String searchOption){
        //return everything by default if option is empty
        if (keyword == null || keyword.trim().isEmpty()){
            return getAllOrder_BLL();
        }

        switch (searchOption){
            case "Tìm kiếm theo ID order":
                //the whole bullshittery because searchOrder_ByID only return a singular Order
                try{
                    int intKeyword = Integer.parseInt(keyword);
                    Order foundOrder = OrderData.searchOrder_ByID(intKeyword);

                    List<Order> result = new ArrayList<>();

                    if (foundOrder != null){
                        result.add(foundOrder);
                    }

                    return result;

                //empty list
                }catch (NumberFormatException e){
                    return new ArrayList<>();
                }

            case "Tìm kiếm theo ID nhân viên thực hiện":
                //less bullshittery because it actually return a list
                try{
                    int intKeyword = Integer.parseInt(keyword);
                    return OrderData.searchOrder_ByStaffID(intKeyword);
                }catch (NumberFormatException e){
                    return new ArrayList<>();
                }

            case "Tìm kiếm theo SĐT khách hàng":
                //the method below was practically made made for this BLL method
                //so its short like that
                return OrderData.searchOrder_ByCustomerPhone(keyword);

            //if it doesnt fit any of the above
            //just return everything
            default:
                return getAllOrder_BLL();
        }
    }

    public static String updateOrder_BLL(Order order){
        //so the id does exist and is valid
        if (order == null || order.getId() <= 0){
            return "Order doesnt exist/ have invalid ID";
        }

        //but does the order with that id actually exist in the database?
        Order orderToUpdate = OrderData.searchOrder_ByID(order.getId());
        if (orderToUpdate == null){
            return "The order doesnt exist";
        }

        boolean isUpdated = OrderData.updateOrder(order);

        if (isUpdated){
            return "Order updated successfully";
        }else {
            return "Order failed to update";
        }
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
