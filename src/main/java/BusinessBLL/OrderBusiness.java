//unlike OrderDetailBusiness, this one is used by the GUI directly
//hence why the return type are all String so the one who actually use this can know what is the exact problem
package BusinessBLL;

import DataDAL.OrderData;
import EntityDTO.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrderBusiness {
    public static int createOrder(Order order, DeliveryInfo deliveryInfo) {
        if (order == null || order.getOrderDetail() == null || order.getOrderDetail().isEmpty()) {
            return -1;
        }

        int newOrderId = OrderData.addOrder(order, deliveryInfo);

        if (newOrderId > 0 && order.getType() == Order.OrderType.Offline) {
            String creatorName = order.getStaff().getName();
            LogBusiness.saveLog("Tài khoản [" + creatorName + "] đã tạo một đơn hàng tại quầy mới (Mã: #" + newOrderId + ")");
        }

        return newOrderId;
    }

    public static List<Order> getAllOrder(){
        return OrderData.getAllOrders();
    }

    public static List<Order> getFilteredOrders(Order.OrderType orderType) {
        ArrayList<Order> list = new ArrayList<>();
        for (Order o : getAllOrder()) {
            if (o.getType() == orderType) {
                list.add(o);
            }
        }
        return list;
    }

    public static boolean isValidStatus(Order order, Order.OrderStatus status){
        String type = String.valueOf(order.getType());

        if ("Offline".equalsIgnoreCase(type)) {
            return status == Order.OrderStatus.Finished;
        }
        else if ("Online".equalsIgnoreCase(type)) {
            List<Order.OrderStatus> validOnlineStates = Arrays.asList(
                    Order.OrderStatus.Waiting_for_validation, Order.OrderStatus.Processing,
                    Order.OrderStatus.Delivering, Order.OrderStatus.Finished, Order.OrderStatus.Cancelled
            );
            return validOnlineStates.contains(status);
        }

        return false;
    }

    public static String updateOrder(Order order, Order.OrderStatus status){
        if (order == null || order.getId() <= 0){
            return "Đơn hàng không tồn tại.";
        }

        if (!isValidStatus(order, status)) {
            return "Trạng thái không hợp lệ cho đơn hàng.";
        }

        Order orderToUpdate = OrderData.searchOrder_ByID(order.getId());
        if (orderToUpdate == null){
            return "Đơn hàng không tồn tại.";
        }

        return ValidTransition(order,status);
    }

    public static String ValidTransition(Order order, Order.OrderStatus status) {
        switch (order.getStatus().name()) {
            case "Processing":
                if (status == Order.OrderStatus.Waiting_for_validation)
                    return "Không thể chuyển trạng thái đơn hàng.";
                break;

            case "Delivering":
                if (status == Order.OrderStatus.Waiting_for_validation || status == Order.OrderStatus.Processing)
                    return "Không thể chuyển trạng thái đơn hàng.";
                break;

            case "Finished":
                if (status == Order.OrderStatus.Waiting_for_validation || status == Order.OrderStatus.Processing
                        || status == Order.OrderStatus.Delivering)
                    return "Không thể chuyển trạng thái đơn hàng.";
                break;

            case "Cancelled":
                return "Không thể chuyển trạng thái đơn hàng đã hủy";
        }
        order.setStatus(status);

        boolean isUpdated = DataDAL.OrderData.updateOrder(order);

        if (isUpdated) {
            if (order.getStatus() == Order.OrderStatus.Finished && order.getCustomer() != null && order.getCustomer().getId() > 0) {
                int pointsEarned = order.getFinalAmount() / 1000;
                if (pointsEarned > 0) {
                    CustomerBusiness.addRewardPoints_BLL(order.getCustomer().getId(), pointsEarned);
                }
            }

            return "Đã cập nhật trạng thái đơn hàng thành công.";
        }

        return "Không thể cập nhật trạng thái đơn hàng.";
    }

    public static String cancelOnlineOrder(Order order, String reason) {
        if (order == null || order.getId() <= 0) {
            return "Không tìm thấy đơn hàng.";
        }

        if (order.getStatus() != Order.OrderStatus.Waiting_for_validation) {
            return "Đơn hàng đã được xử lý, đang giao hoặc đã hoàn thành, không thể hủy!";
        }

        order.setStatus(Order.OrderStatus.Cancelled);
        order.setCancelReason(reason);

        boolean isUpdated = OrderData.updateOrder(order);

        if (isUpdated) {
            List<OrderDetail> details = OrderDetailBusiness.getDetailsByOrderId_BLL(order.getId());

            if (details != null && !details.isEmpty()) {
                for (OrderDetail detail : details) {
                    int productId = detail.getProduct().getProductID();
                    int quantityToRefund = detail.getQuantity();

                    ProductBusiness.addStock(productId, quantityToRefund);
                }
            }

            return "Hủy đơn hàng thành công!";
        } else {
            return "Không thể hủy đơn hàng do lỗi kết nối cơ sở dữ liệu.";
        }
    }

    public static int getDiscountAmount(int subtotal, Customer customer, PromoCode code) {
        int customerDiscountPercent = 0;
        if (customer != null) {
            customerDiscountPercent = CustomerBusiness.getDiscountPercent(customer);
        }

        int discountFromCustomer = (int) (subtotal * (customerDiscountPercent / 100.0));
        int remainingSubtotal = subtotal - discountFromCustomer;

        int discountFromPromo = 0;

        if (code != null) {
            if (code.getDiscountType() == PromoCode.CodeType.Percent) {
                discountFromPromo = (int) (remainingSubtotal * (code.getDiscountValue() / 100.0));
            }
            else if (code.getDiscountType() == PromoCode.CodeType.Amount) {
                discountFromPromo = code.getDiscountValue();
            }

            if (discountFromPromo > remainingSubtotal) {
                discountFromPromo = remainingSubtotal;
            }
        }

        return discountFromCustomer + discountFromPromo;
    }

    public static int getFinalTotal(int subtotal, int discountAmount) {
        return subtotal - discountAmount;
    }
}
