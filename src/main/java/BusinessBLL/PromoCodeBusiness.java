package BusinessBLL;

import DataDAL.PromoCodeData;
import EntityDTO.PromoCode;

import java.util.List;

public class PromoCodeBusiness {
    public static List<PromoCode> getAllPromoCodes() {
        return PromoCodeData.getAllPromoCodes();
    }
}
