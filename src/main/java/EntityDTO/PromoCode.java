package EntityDTO;

import java.sql.Date;

public class PromoCode {
    private String code;
    private String description;
    private int discountValue;
    private String discountType; // "PERCENT" hoặc "AMOUNT"
    private int minOrderValue;
    private Date validFrom;
    private Date validTo;
    private String status;

    public PromoCode() {}

    public PromoCode(String code, String description, int discountValue, String discountType, int minOrderValue) {
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.minOrderValue = minOrderValue;
    }

    public String getCode() { return code; }
    public int getDiscountValue() { return discountValue; }
    public String getDiscountType() { return discountType; }
    public int getMinOrderValue() { return minOrderValue; }

    @Override
    public String toString() {
        if (discountType.equals("PERCENT")) {
            return code + " (Giảm " + (int)discountValue + "%)";
        } else {
            return code + " (Giảm " + String.format("%,d", discountValue) + "đ)";
        }
    }
}
