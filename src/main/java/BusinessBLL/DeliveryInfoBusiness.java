package BusinessBLL;

import DataDAL.DeliveryInfoData;

public class DeliveryInfoBusiness {
    public static EntityDTO.DeliveryInfo getDeliveryInfo(int orderId) {
        return DeliveryInfoData.getDeliveryInfoByOrderId(orderId);
    }
}
