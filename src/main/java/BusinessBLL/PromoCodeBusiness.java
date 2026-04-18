package BusinessBLL;

import DataDAL.PromoCodeData;
import EntityDTO.PromoCode;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PromoCodeBusiness {
    public static List<PromoCode> getAllPromoCodes() {
        PromoCodeData.refreshAllPromoStatuses();
        return PromoCodeData.getAllPromoCodes();
    }

    public static List<PromoCode> getAllActivePromoCodes() {
        PromoCodeData.refreshAllPromoStatuses();
        List<PromoCode> activeList = new ArrayList<>();
        for (PromoCode code : PromoCodeData.getAllPromoCodes()) {
            if (code.getStatus().equals("Active")) {
                activeList.add(code);
            }
        }
        return activeList;
    }

    public static int addPromoCode(String code, String description, int value, String type, int minOrder, Date fromDate, Date toDate) {
        if (PromoCodeData.isPromoCodeExist(code)) return -1;

        String status = "Active";

        if (fromDate != null) {
            LocalDate localFromDate = fromDate.toLocalDate();
            LocalDate today = LocalDate.now();

            if (localFromDate.isAfter(today)) {
                status = "Upcoming";
            } else if (toDate != null && toDate.toLocalDate().isBefore(today)) {
                status = "Expired";
            } else {
                status = "Active";
            }
        }

        if (PromoCodeData.addPromoCode(new PromoCode(code, description, value, type, minOrder, fromDate, toDate, status))) {
            LogBusiness.saveLog("Thêm mã giảm giá " + code + " vào hệ thống");
            return 1;
        }
        return 0;
    }

    public static int updatePromoCode(String code, String description, int value, String type, int minOrder, Date fromDate, Date toDate) {
        String status = "Active";

        if (fromDate != null) {
            LocalDate localFromDate = fromDate.toLocalDate();
            LocalDate today = LocalDate.now();

            if (localFromDate.isAfter(today)) {
                status = "Upcoming";
            } else if (toDate != null && toDate.toLocalDate().isBefore(today)) {
                status = "Expired";
            } else {
                status = "Active";
            }
        }

        if (PromoCodeData.updatePromoCode(new PromoCode(code, description, value, type, minOrder, fromDate, toDate, status))) {
            LogBusiness.saveLog("Cập nhật mã giảm giá " + code + " trong hệ thống");
            return 1;
        }
        return 0;
    }

    public static boolean updatePromoStatus(String code, String status) {
        if (PromoCodeData.updatePromoStatus(code, status)) {
            LogBusiness.saveLog("Thay đổi trạng thái của mã giảm giá " + code + " thành " + status);
            return true;
        }
        return false;
    }
}