package BusinessBLL;

import DataDAL.DeliveryInfoData;
import EntityDTO.DeliveryInfo;

public class DeliveryInfoBusiness {
    public static DeliveryInfo getDeliveryInfo(int orderId) {
        return DeliveryInfoData.getDeliveryInfoByOrderId(orderId);
    }
}
