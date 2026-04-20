//unlike OrderDetailBusiness, this one is used by the GUI directly
//hence why the return type are all String so the one who actually use this can know what is the exact problem
package BusinessBLL;

import DataDAL.OrderDetailData;
import EntityDTO.Order;
import EntityDTO.OrderDetail;
import DataDAL.OrderData;
import EntityDTO.PromoCode;
import org.w3c.dom.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

        order.setOrderDetail(orderDetailList);
        String mathStatus = calculateMoney(order);

        int createdOrderID = OrderData.addOrder(order);
        if (createdOrderID <= 0){
            return "ERROR: Order created with invalid ID";
        }

        boolean itemSaved = OrderDetailBusiness.saveOrderDetail_BLL(orderDetailList, createdOrderID);
        if (!itemSaved){
            return "ERROR: Save failed";
        }

        return "OrderBusiness success " + mathStatus;
    }

    public static List<Order> getAllOrder_BLL(){
        return OrderData.getAllOrders();
    }

    //status contraints as said in Order DTO
    public static boolean isValidStatus(Order order, String status){
        String type = String.valueOf(order.getType());

        if ("Offline".equalsIgnoreCase(type)) {
            return status.equals("Processing") || status.equals("Finished");
        }
        else if ("Online".equalsIgnoreCase(type)) {
            List<String> validOnlineStates = Arrays.asList(
                    "Created", "Waiting_for_validation", "Processing", "Delivering", "Finished", "Cancelled"
            );
            return validOnlineStates.contains(status);
        }

        return false;
    }

    public static List<Order> getOnlineOrders_BLL() {
        // 1. Lấy danh sách nguyên bản từ DB (không bị lỗi mất thông tin)
        List<Order> allOrders = OrderData.getAllOrders();

        if (allOrders != null) {
            // 2. Lọc ra các đơn Online
            List<Order> onlineOrders = allOrders.stream()
                    .filter(o -> o.getType() == Order.orderType.Online)
                    .collect(Collectors.toList());

            // 3. TÍNH TỔNG TIỀN CHO TỪNG ĐƠN HÀNG
            for (Order order : onlineOrders) {
                // Gọi BLL của Detail để lấy danh sách món ăn của hóa đơn này
                List<OrderDetail> details = OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

                int total = 0;
                if (details != null) {
                    for (OrderDetail detail : details) {
                        total += detail.getTotalPrice(); // Cộng dồn tiền từng món
                    }
                }

                // Lưu tổng tiền vào hóa đơn để mang lên GUI hiển thị
                order.setFinalAmount(total);
            }

            return onlineOrders;
        }
        return null;
    }

    public static String updateOrder_BLL(Order order, String status){
        //so the id does exist and is valid
        if (order == null || order.getId() <= 0){
            return "Order doesnt exist/ have invalid ID";
        }

        if (!isValidStatus(order, status)) {
            return "LỖI: Trạng thái '" + status + "' không hợp lệ cho đơn hàng " + order.getType();
        }

        //but does the order with that id actually exist in the database?
        Order orderToUpdate = OrderData.searchOrder_ByID(order.getId());
        if (orderToUpdate == null){
            return "The order doesnt exist";
        }

        order.setStatus(Order.orderStatus.valueOf(status));

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

    public static String cancelOnlineOrder_BLL(Order order, String reason) {
        if (order == null || order.getId() <= 0) {
            return "Lỗi: Không tìm thấy đơn hàng.";
        }

        // Luật kinh doanh BR-22: Chỉ cho phép hủy khi đơn ở trạng thái Chờ xác nhận hoặc Mới tạo
        if (order.getStatus() != Order.orderStatus.Waiting_for_validation && order.getStatus() != Order.orderStatus.Created) {
            return "Lỗi: Đơn hàng đã được xử lý hoặc đang giao, không thể hủy!";
        }

        // 1. Thay đổi trạng thái thành Hủy
        order.setStatus(Order.orderStatus.Cancelled);

        order.setCancel_reason(reason);

        // 2. Gọi hàm DAL để lưu trạng thái mới xuống DB
        boolean isUpdated = OrderData.updateOrder(order);

        if (isUpdated) {
            // 3. Nghiệp vụ bắt buộc: Hoàn trả số lượng tồn kho cho các sản phẩm trong đơn
            List<OrderDetail> details = order.getOrderDetail();
            if (details != null) {
                for (OrderDetail detail : details) {
                    int productId = detail.getProduct().getProductID();
                    int quantityToRefund = detail.getQuantity();

                    // Gọi sang ProductBusiness để cộng lại số lượng vào kho
                    // Giả định bạn có hàm updateStock(id, số_lượng_cộng_thêm)
                    // ProductBusiness.updateStock_BLL(productId, quantityToRefund);
                }
            }

            // Tùy chọn: Bạn có thể lưu 'reason' (lý do hủy) vào bảng Log hoặc bảng Order nếu CSDL có cột này

            return "Đã hủy đơn hàng và hoàn trả tồn kho thành công!";
        } else {
            return "Lỗi: Không thể hủy đơn hàng do lỗi hệ thống CSDL.";
        }
    }

    //all things money related of order
    public static String calculateMoney(Order order){
        //calculate subtotal
        int calSubTotal = 0;
        if (order.getOrderDetail() != null){
            for (EntityDTO.OrderDetail item : order.getOrderDetail()){
                calSubTotal += item.getTotalPrice();
            }
        }
        order.setSubTotal(calSubTotal);

        //discount stack multiplicatively
        //calculate discount
        //discount from rank
        int rankDiscount = 0;
        String rankMessage = "";
        if (order.getCustomer() != null){
            int rankPercent = CustomerBusiness.getDiscountPercent(order.getCustomer());
            if (rankPercent > 0){
                rankDiscount = (int) (calSubTotal * (rankPercent / 100.0));
                rankMessage = "Hạng " + order.getCustomer().getCustomer_rank().name() + " (-" + rankPercent + "%). ";
            }
        }

        //multiplicative discount post-rank but pre-promo
        int discoutedTotal_Post = calSubTotal - rankDiscount;

        //discount from code
        int promoDiscount = 0;
        String code = order.getAppliedCode();
        String promoMessage = "Tính tiền thành công.";

        //code is not empty
        if (code != null && !code.trim().isEmpty()){
            code = code.trim();
            //fetch
            EntityDTO.PromoCode promoCode = DataDAL.PromoCodeData.getPromoCode(code);

            //code doesnt exist/ doesnt match
            if (promoCode == null || !promoCode.getCode().equals(code)){
                order.setAppliedCode(null);
                promoMessage = "ERROR: code doesnt exist/ doesnt match.";
            }else {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();

                //code exist but is inactive
                if (promoCode.getStatus() != EntityDTO.PromoCode.codeStatus.Active){
                    order.setAppliedCode(null);
                    promoMessage = "ERROR: code is inactive.";
                //before the code can even be used/ after the code has been expired
                } else if (now.isBefore(promoCode.getValidFrom()) || now.isAfter(promoCode.getValidTo())){
                    order.setAppliedCode(null);
                    promoMessage = "ERROR: code is not used within the active timespan.";
                //value of order is lower than min threshold
                } else if (calSubTotal < promoCode.getMinOrderValue()) {
                    order.setAppliedCode(null);
                    promoMessage = "ERROR: order has lower value than min threshold of code.";
                //valid code, calculate its discount value
                } else{
                    if (promoCode.getDiscountType() == EntityDTO.PromoCode.codeType.Percent){
                        promoDiscount = (int) (discoutedTotal_Post * (promoCode.getDiscountValue() / 100.0));
                    } else if (promoCode.getDiscountType() == EntityDTO.PromoCode.codeType.Amount){
                        promoDiscount = promoCode.getDiscountValue();
                    }
                }
            }
        }

        //calculate final
        int totalDiscount = rankDiscount + promoDiscount;
        int finalTotal = calSubTotal - totalDiscount;
        if (finalTotal < 0){
            finalTotal = 0;
        }

        order.setDiscountAmount(totalDiscount);
        order.setFinalAmount(finalTotal);

        //display message
        if (rankMessage.isEmpty() && promoMessage.isEmpty()){
            return "Tính tiền thành công.";
        } else if (!rankMessage.isEmpty() && promoMessage.isEmpty() || promoMessage.startsWith("ERROR")) {
            //rank sucess, promo failed
            return rankMessage + promoMessage;
        } else {
            return rankMessage + promoMessage;
        }
    }
}
