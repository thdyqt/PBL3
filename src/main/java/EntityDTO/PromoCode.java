package EntityDTO;

import java.time.LocalDateTime;

public class PromoCode {
    //attributes
    private String code;
    private LocalDateTime expirationDate;
    private double priceModifier;

    //constructors
    PromoCode(){}
    PromoCode(String code, LocalDateTime expirationDate, double priceModifier) {
        this.code = code;
        this.expirationDate = expirationDate;
        this.priceModifier = priceModifier;
    }

    //get-set
    public String getCode() {return this.code;}
    public void setCode(String code){this.code = code;}

    public LocalDateTime getExpirationDate(){return this.expirationDate;}
    public void setExpirationDate(LocalDateTime expirationDate) {this.expirationDate = expirationDate;}

    public double getPriceModifier(){return this.priceModifier;}
    public void setPriceModifier(double priceModifier){
        if (priceModifier <= 0 || priceModifier > 1){
            throw new IllegalArgumentException("Price modifier can only be larger 0 lower 1.");
        }
        this.priceModifier = priceModifier;
    }

}
