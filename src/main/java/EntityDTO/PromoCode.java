package EntityDTO;

import java.time.LocalDateTime;

public class PromoCode {

    public enum codeType{
        Percent, Amount;
    }

    public enum codeStatus{
        Active, Paused, Upcoming, Expired;
    }

    private String code;
    private String description;
    private int discountValue;
    private codeType discountType; // "PERCENT" hoặc "AMOUNT"
    private int minOrderValue;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private codeStatus status;

    public PromoCode() {}

    public PromoCode(String code, String description, int discountValue, codeType discountType, int minOrderValue, LocalDateTime validFrom, LocalDateTime validTo, codeStatus status) {
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
    public codeType getDiscountType() { return discountType; }
    public int getMinOrderValue() { return minOrderValue; }
    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    public LocalDateTime getValidTo() {
        return validTo;
    }
    public codeStatus getStatus() {
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

    public void setDiscountType(codeType discountType) {
        this.discountType = discountType;
    }

    public void setMinOrderValue(int minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }

    public void setStatus(codeStatus status) {
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
