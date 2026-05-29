package model;

import java.io.Serializable;
import java.util.Date;

public class Voucher implements Serializable {
    private int id;
    private String voucherCode;
    private float discountValue;
    private float minOrderAmount;
    private int maxUsage;
    private int usageCount;
    private Date expiryDate;

    public Voucher() { super(); }

    public Voucher(String voucherCode, float discountValue, float minOrderAmount,
                   int maxUsage, Date expiryDate) {
        this.voucherCode = voucherCode;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.maxUsage = maxUsage;
        this.usageCount = 0;
        this.expiryDate = expiryDate;
    }

    public boolean isValid(float orderAmount) {
        Date today = new Date();
        return usageCount < maxUsage
            && orderAmount >= minOrderAmount
            && today.before(expiryDate);
    }

    public float applyDiscount(float orderAmount) {
        return orderAmount * (1 - discountValue / 100f);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }

    public float getDiscountValue() { return discountValue; }
    public void setDiscountValue(float discountValue) { this.discountValue = discountValue; }

    public float getMinOrderAmount() { return minOrderAmount; }
    public void setMinOrderAmount(float minOrderAmount) { this.minOrderAmount = minOrderAmount; }

    public int getMaxUsage() { return maxUsage; }
    public void setMaxUsage(int maxUsage) { this.maxUsage = maxUsage; }

    public int getUsageCount() { return usageCount; }
    public void setUsageCount(int usageCount) { this.usageCount = usageCount; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    @Override
    public String toString() {
        return voucherCode + " (" + discountValue + "% off, min " + minOrderAmount + ")";
    }
}
