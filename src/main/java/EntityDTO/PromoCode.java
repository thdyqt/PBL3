package EntityDTO;

import java.sql.Date;

public class PromoCode {
    private String code;
    private String description;
    private double discountValue;
    private String discountType; // "PERCENT" hoặc "AMOUNT"
    private double minOrderValue;
    private Date validFrom;
    private Date validTo;
    private String status;

    public PromoCode() {}

    public PromoCode(String code, String description, double discountValue, String discountType, double minOrderValue) {
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.minOrderValue = minOrderValue;
    }

    public String getCode() { return code; }
    public double getDiscountValue() { return discountValue; }
    public String getDiscountType() { return discountType; }
    public double getMinOrderValue() { return minOrderValue; }

    @Override
    public String toString() {
        if (discountType.equals("PERCENT")) {
            return code + " (Giảm " + (int)discountValue + "%)";
        } else {
            return code + " (Giảm " + String.format("%,.0f", discountValue) + "đ)";
        }
    }
}
