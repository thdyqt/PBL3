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

    public PromoCode(String code, String description, int discountValue, String discountType, int minOrderValue, Date validFrom, Date validTo, String status) {
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.minOrderValue = minOrderValue;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getDescription() {
        return description;
    }
    public int getDiscountValue() { return discountValue; }
    public String getDiscountType() { return discountType; }
    public int getMinOrderValue() { return minOrderValue; }
    public Date getValidFrom() {
        return validFrom;
    }
    public Date getValidTo() {
        return validTo;
    }
    public String getStatus() {
        return status;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiscountValue(int discountValue) {
        this.discountValue = discountValue;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public void setMinOrderValue(int minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        if (discountType.equals("PERCENT")) {
            return code + " (Giảm " + (int)discountValue + "%)";
        } else {
            return code + " (Giảm " + String.format("%,d", discountValue) + "đ)";
        }
    }
}
