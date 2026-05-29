package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Order implements Serializable {
    private int id;
    private String orderCode;
    private Date orderDate;
    private String receiverName;
    private String shippingAddress;
    private String paymentMethod;   // "COD", "BANK_TRANSFER", "MOMO", "ZALOPAY"
    private String orderStatus;     // "PENDING", "CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED"
    private String trackingCode;
    private float totalAmount;
    private Admin confirmedBy;
    private Voucher voucher;
    private Client client;
    private ArrayList<OrderLine> orderLines;

    public Order() {
        super();
        this.orderLines = new ArrayList<>();
    }

    public Order(String orderCode, Date orderDate, String receiverName,
                 String shippingAddress, String paymentMethod, Client client) {
        this.orderCode = orderCode;
        this.orderDate = orderDate;
        this.receiverName = receiverName;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.orderStatus = "PENDING";
        this.client = client;
        this.orderLines = new ArrayList<>();
    }

    /** Recalculates totalAmount from order lines, then applies voucher if present. */
    public void calculateTotal() {
        float subtotal = 0;
        for (OrderLine line : orderLines) {
            subtotal += line.getSubTotal();
        }
        if (voucher != null && voucher.isValid(subtotal)) {
            this.totalAmount = voucher.applyDiscount(subtotal);
        } else {
            this.totalAmount = subtotal;
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }

    public float getTotalAmount() { return totalAmount; }
    public void setTotalAmount(float totalAmount) { this.totalAmount = totalAmount; }

    public Admin getConfirmedBy() { return confirmedBy; }
    public void setConfirmedBy(Admin confirmedBy) { this.confirmedBy = confirmedBy; }

    public Voucher getVoucher() { return voucher; }
    public void setVoucher(Voucher voucher) { this.voucher = voucher; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public ArrayList<OrderLine> getOrderLines() { return orderLines; }
    public void setOrderLines(ArrayList<OrderLine> orderLines) { this.orderLines = orderLines; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", code='" + orderCode + "', status='" + orderStatus + "', total=" + totalAmount + "}";
    }
}
