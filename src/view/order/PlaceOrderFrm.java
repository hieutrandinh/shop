package view.order;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import javax.swing.*;
import dao.OrderDAO;
import model.Client;
import model.Order;
import model.OrderLine;
import model.Voucher;
import view.user.ClientHomeFrm;


public class PlaceOrderFrm extends JFrame implements ActionListener {

    private final Client          client;
    private final ArrayList<OrderLine> cart;
    private final Voucher         voucher;
    private final CartFrm         cartFrm;

    private JTextField txtReceiver;
    private JTextField txtAddress;
    private JTextField txtPhone;
    private JComboBox<String> cmbPayment;
    private JTextArea  txaOrderSummary;
    private JButton    btnConfirm;
    private JButton    btnBack;

    public PlaceOrderFrm(Client client, ArrayList<OrderLine> cart,
                         Voucher voucher, CartFrm cartFrm) {
        super("Cửa Hàng Thể Thao – Thanh Toán");
        this.client  = client;
        this.cart    = cart;
        this.voucher = voucher;
        this.cartFrm = cartFrm;
        buildUI();
        buildSummary();
    }

    private void buildUI() {
        txtReceiver = new JTextField(20);
        txtAddress  = new JTextField(24);
        txtPhone    = new JTextField(14);
        cmbPayment  = new JComboBox<>(new String[]{"COD", "BANK_TRANSFER", "MOMO", "VNPAY"});
        txaOrderSummary = new JTextArea(10, 40);
        txaOrderSummary.setEditable(false);
        txaOrderSummary.setFont(new java.awt.Font("Monospaced", 0, 12));
        btnConfirm = new JButton("Xác Nhận Đơn Hàng");
        btnBack    = new JButton("← Quay lại Giỏ Hàng");

        txtReceiver.setText(client.getFullName());
        txtAddress.setText(client.getAddress());
        txtPhone.setText(client.getTel());

        JPanel pnShipping = new JPanel(new GridLayout(0, 2, 6, 6));
        pnShipping.setBorder(BorderFactory.createTitledBorder("Thông Tin Giao Hàng"));
        pnShipping.add(new JLabel("Tên người nhận:"));  pnShipping.add(txtReceiver);
        pnShipping.add(new JLabel("Địa chỉ giao hàng:")); pnShipping.add(txtAddress);
        pnShipping.add(new JLabel("Số điện thoại:"));            pnShipping.add(txtPhone);
        pnShipping.add(new JLabel("Phương thức thanh toán:"));   pnShipping.add(cmbPayment);

        JPanel pnSummary = new JPanel(new BorderLayout());
        pnSummary.setBorder(BorderFactory.createTitledBorder("Tóm Tắt Đơn Hàng"));
        pnSummary.add(new JScrollPane(txaOrderSummary), BorderLayout.CENTER);

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        JLabel lblTitle = new JLabel("Thanh Toán", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnLbl);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnBottom.add(btnConfirm);

        JPanel pnCenter = new JPanel();
        pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.PAGE_AXIS));
        pnCenter.add(pnShipping);
        pnCenter.add(Box.createRigidArea(new Dimension(0, 8)));
        pnCenter.add(pnSummary);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth,  BorderLayout.NORTH);
        this.add(pnCenter, BorderLayout.CENTER);
        this.add(pnBottom, BorderLayout.SOUTH);

        btnBack.addActionListener(this);
        btnConfirm.addActionListener(this);

        this.setSize(680, 560);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void buildSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-30s %6s  %10s  %12s%n",
                "Sản phẩm (Kích cỡ/Màu)", "SL", "Đơn giá", "Thành tiền"));
        sb.append("─".repeat(64)).append("\n");

        float sub = 0;
        for (OrderLine line : cart) {
            model.ProductVariant v = line.getVariant();
            String desc = (v != null)
                    ? "PB #" + v.getId() + " (" + v.getSize() + "/" + v.getColor() + ")"
                    : "Unknown";
            sb.append(String.format("%-30s %6d  %10.0f  %12.0f%n",
                    desc, line.getQuantity(),
                    line.getPriceAtPurchase(), line.getSubTotal()));
            sub += line.getSubTotal();
        }
        sb.append("─".repeat(64)).append("\n");
        sb.append(String.format("Tạm tính:   %,.0f VNĐ%n", sub));

        float total = sub;
        float discountAmount = 0;
        if (voucher != null && sub >= voucher.getMinOrderAmount()) {
            total = voucher.applyDiscount(sub);
            discountAmount = sub - total;
            sb.append(String.format("Mã giảm giá (%s - %.0f%%): -%.0f VNĐ%n",
                    voucher.getVoucherCode(), voucher.getDiscountValue(), discountAmount));
        }
        sb.append(String.format("TỔNG THANH TOÁN: %,.0f VNĐ", total));
        txaOrderSummary.setText(sb.toString());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            cartFrm.setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnConfirm) {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        String receiver = txtReceiver.getText().trim();
        String address  = txtAddress.getText().trim();
        if (receiver.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên người nhận và địa chỉ không được để trống.");
            return;
        }

        float sub = 0;
        for (OrderLine line : cart) sub += line.getSubTotal();

        float total = sub;
        if (voucher != null && sub >= voucher.getMinOrderAmount()) {
            total = voucher.applyDiscount(sub);
        }

        Order order = new Order();
        order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setOrderDate(new Date());
        order.setReceiverName(receiver);
        order.setShippingAddress(address);
        order.setPaymentMethod((String) cmbPayment.getSelectedItem());
        order.setOrderStatus("PENDING");
        order.setTotalAmount(total);
        order.setClient(client);
        order.setVoucher(voucher);
        order.setOrderLines(new ArrayList<>(cart));

        OrderDAO od = new OrderDAO();
        if (od.addOrder(order)) {
            cart.clear();
            JOptionPane.showMessageDialog(this,
                    "Đặt hàng thành công!\nMã đơn: " + order.getOrderCode()
                            + "\nTổng: " + String.format("%.0f VNĐ", total));
            new view.user.ClientHomeFrm(client).setVisible(true);
            cartFrm.dispose();
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đặt hàng thất bại. Vài sản phẩm có thể đã hết hàng.\nVui lòng kiểm tra lại giỏ hàng.");
        }
    }
}