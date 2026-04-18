package BusinessBLL;

import DataDAL.PromoCodeData;
import EntityDTO.PromoCode;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PromoCodeBusiness {
    public static List<PromoCode> getAllPromoCodes() {
        PromoCodeData.refreshAllPromoStatuses();
        return PromoCodeData.getAllPromoCodes();
    }

    public static int addPromoCode(String code, String description, int value, PromoCode.codeType type, int minOrder, LocalDateTime fromDate, LocalDateTime toDate) {
        if (PromoCodeData.isPromoCodeExist(code)) return -1;

        PromoCode.codeStatus status = PromoCode.codeStatus.Active;

        if (fromDate != null) {
            LocalDateTime now = LocalDateTime.now();

            if (fromDate.isAfter(now)) {
                status = PromoCode.codeStatus.Upcoming;
            } else if (toDate != null && toDate.isBefore(now)) {
                status = PromoCode.codeStatus.Expired;
            } else {
                status = PromoCode.codeStatus.Active;
            }
        }

        if (PromoCodeData.addPromoCode(new PromoCode(code, description, value, type, minOrder, fromDate, toDate, status))) {
            LogBusiness.saveLog("Thêm mã giảm giá " + code + " vào hệ thống");
            return 1;
        }
        return 0;
    }

    public static int updatePromoCode(String code, String description, int value, PromoCode.codeType type, int minOrder, LocalDateTime fromDate, LocalDateTime toDate) {
        PromoCode.codeStatus status = PromoCode.codeStatus.Active;

        if (fromDate != null) {
            LocalDateTime now = LocalDateTime.now();

            if (fromDate.isAfter(now)) {
                status = PromoCode.codeStatus.Upcoming;
            } else if (toDate != null && toDate.isBefore(now)) {
                status = PromoCode.codeStatus.Expired;
            } else {
                status = PromoCode.codeStatus.Active;
            }
        }

        if (PromoCodeData.updatePromoCode(new PromoCode(code, description, value, type, minOrder, fromDate, toDate, status))) {
            LogBusiness.saveLog("Cập nhật mã giảm giá " + code + " trong hệ thống");
            return 1;
        }
        return 0;
    }

    public static boolean updatePromoStatus(String code, PromoCode.codeStatus status) {
        if (PromoCodeData.updatePromoStatus(code, status.name())) {
            LogBusiness.saveLog("Thay đổi trạng thái của mã giảm giá " + code + " thành " + status.name());
            return true;
        }
        return false;
    }
}