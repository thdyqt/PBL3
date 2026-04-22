package BusinessBLL;

import DataDAL.PromoCodeData;
import EntityDTO.PromoCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PromoCodeBusiness {
    public static List<PromoCode> getAllPromoCodes() {
        PromoCodeData.refreshAllPromoStatuses();
        return PromoCodeData.getAllPromoCodes();
    }

    public static List<PromoCode> getAllActivePromoCodes(PromoCode.Type type) {
        PromoCodeData.refreshAllPromoStatuses();
        List<PromoCode> activeList = new ArrayList<>();
        for (PromoCode code : PromoCodeData.getAllPromoCodes()) {
            if (code.getStatus() == PromoCode.CodeStatus.Active && (code.getType() == type || code.getType() == PromoCode.Type.All)) {
                activeList.add(code);
            }
        }
        return activeList;
    }

    public static int addPromoCode(String code, String description, int value, PromoCode.CodeType discountType, PromoCode.Type type, int minOrder, LocalDateTime fromDate, LocalDateTime toDate) {
        if (PromoCodeData.isPromoCodeExist(code)) return -1;

        PromoCode.CodeStatus status = PromoCode.CodeStatus.Active;

        if (fromDate != null) {
            LocalDateTime now = LocalDateTime.now();

            if (fromDate.isAfter(now)) {
                status = PromoCode.CodeStatus.Upcoming;
            } else if (toDate != null && toDate.isBefore(now)) {
                status = PromoCode.CodeStatus.Expired;
            } else {
                status = PromoCode.CodeStatus.Active;
            }
        }

        if (PromoCodeData.addPromoCode(new PromoCode(code, description, value, discountType, type, minOrder, fromDate, toDate, status))) {
            LogBusiness.saveLog("Thêm mã giảm giá " + code + " vào hệ thống");
            return 1;
        }
        return 0;
    }

    public static int updatePromoCode(String code, String description, int value, PromoCode.CodeType discountType, PromoCode.Type type, int minOrder, LocalDateTime fromDate, LocalDateTime toDate) {
        PromoCode.CodeStatus status = PromoCode.CodeStatus.Active;

        if (fromDate != null) {
            LocalDateTime now = LocalDateTime.now();

            if (fromDate.isAfter(now)) {
                status = PromoCode.CodeStatus.Upcoming;
            } else if (toDate != null && toDate.isBefore(now)) {
                status = PromoCode.CodeStatus.Expired;
            } else {
                status = PromoCode.CodeStatus.Active;
            }
        }

        if (PromoCodeData.updatePromoCode(new PromoCode(code, description, value, discountType, type, minOrder, fromDate, toDate, status))) {
            LogBusiness.saveLog("Cập nhật mã giảm giá " + code + " trong hệ thống");
            return 1;
        }
        return 0;
    }

    public static boolean updatePromoStatus(String code, PromoCode.CodeStatus status) {
        if (PromoCodeData.updatePromoStatus(code, status.name())) {
            LogBusiness.saveLog("Thay đổi trạng thái của mã giảm giá " + code + " thành " + status.name());
            return true;
        }
        return false;
    }

    public static boolean updatePromoStatusAndStartDate(String code, PromoCode.CodeStatus status, LocalDateTime validFrom) {
        if (PromoCodeData.updatePromoStatusAndStartDate(code, status.name(), validFrom)) {
            LogBusiness.saveLog("Kích hoạt sớm mã giảm giá: " + code);
            return true;
        }
        return false;
    }
}