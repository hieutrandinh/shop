package view.order;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.VoucherDAO;
import model.Client;
import model.OrderLine;
import model.Voucher;
import view.user.ClientHomeFrm;

public class CartFrm extends JFrame implements ActionListener {

    private final Client          client;
    private final ArrayList<OrderLine> cart;
    private final ClientHomeFrm   parentFrm;

    private JTable  tblCart;
    private JLabel  lblSubtotal;
    private JLabel  lblDiscount;
    private JLabel  lblTotal;

    private JTextField txtVoucher;
    private JButton    btnApplyVoucher;
    private JButton    btnRemoveLine;
    private JButton    btnCheckout;
    private JButton    btnBack;

    private Voucher appliedVoucher = null;

    public CartFrm(Client client, ArrayList<OrderLine> cart, ClientHomeFrm parentFrm) {
        super("Cửa Hàng Thể Thao – Giỏ Hàng");
        this.client    = client;
        this.cart      = cart;
        this.parentFrm = parentFrm;
        buildUI();
        refreshCart();
    }

    private void buildUI() {
        tblCart         = new JTable();
        tblCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lblSubtotal     = new JLabel("Tạm tính: 0 VNĐ");
        lblDiscount     = new JLabel("Giảm giá: 0 VNĐ");
        lblTotal        = new JLabel("Tổng cộng: 0 VNĐ");
        lblTotal.setFont(lblTotal.getFont().deriveFont(14.0f));
        txtVoucher      = new JTextField(12);
        btnApplyVoucher = new JButton("Áp dụng mã");
        btnRemoveLine   = new JButton("Xóa Dòng");
        btnCheckout     = new JButton("Tiến hành Thanh toán →");
        btnBack         = new JButton("← Tiếp tục mua sắm");

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        JLabel lblTitle = new JLabel("Giỏ Hàng Của Tôi", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnLbl);

        JScrollPane scroll = new JScrollPane(tblCart);
        scroll.setPreferredSize(new Dimension(720, 200));

        JPanel pnVoucher = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnVoucher.add(new JLabel("Mã giảm giá:"));
        pnVoucher.add(txtVoucher);
        pnVoucher.add(btnApplyVoucher);
        pnVoucher.add(btnRemoveLine);

        JPanel pnTotals = new JPanel(new GridLayout(3, 1, 2, 2));
        pnTotals.add(lblSubtotal);
        pnTotals.add(lblDiscount);
        pnTotals.add(lblTotal);

        JPanel pnCheckout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnCheckout.add(btnCheckout);

        JPanel pnSouth = new JPanel();
        pnSouth.setLayout(new BoxLayout(pnSouth, BoxLayout.PAGE_AXIS));
        pnSouth.add(pnVoucher);
        pnSouth.add(pnTotals);
        pnSouth.add(pnCheckout);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth, BorderLayout.NORTH);
        this.add(scroll,  BorderLayout.CENTER);
        this.add(pnSouth, BorderLayout.SOUTH);

        btnBack.addActionListener(this);
        btnApplyVoucher.addActionListener(this);
        btnRemoveLine.addActionListener(this);
        btnCheckout.addActionListener(this);

        this.setSize(760, 460);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    void refreshCart() {
        String[] cols = {"ID Phiên Bản", "Kích cỡ", "Màu sắc", "Giá", "SL", "Thành tiền"};
        String[][] data = new String[cart.size()][6];
        float sub = 0;
        for (int i = 0; i < cart.size(); i++) {
            OrderLine line = cart.get(i);
            model.ProductVariant v = line.getVariant();
            data[i][0] = (v != null) ? String.valueOf(v.getId()) : "—";
            data[i][1] = (v != null) ? v.getSize()  : "—";
            data[i][2] = (v != null) ? v.getColor() : "—";
            data[i][3] = String.format("%.0f", line.getPriceAtPurchase());
            data[i][4] = String.valueOf(line.getQuantity());
            data[i][5] = String.format("%.0f", line.getSubTotal());
            sub += line.getSubTotal();
        }
        tblCart.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        float total = sub;
        float discountAmount = 0;

        if (appliedVoucher != null && sub >= appliedVoucher.getMinOrderAmount()) {
            total = appliedVoucher.applyDiscount(sub);
            discountAmount = sub - total;
        }

        lblSubtotal.setText(String.format("Tạm tính: %.0f VNĐ", sub));
        lblDiscount.setText(String.format("Giảm giá: -%.0f VNĐ%s", discountAmount,
                appliedVoucher != null ? "  (mã: " + appliedVoucher.getVoucherCode() + ", giảm " + appliedVoucher.getDiscountValue() + "%)" : ""));
        lblTotal.setText(String.format("Tổng thanh toán: %.0f VNĐ", total));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            parentFrm.setVisible(true);
            this.dispose();

        } else if (e.getSource() == btnApplyVoucher) {
            applyVoucher();

        } else if (e.getSource() == btnRemoveLine) {
            int row = tblCart.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa."); return; }
            cart.remove(row);
            refreshCart();

        } else if (e.getSource() == btnCheckout) {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng của bạn đang trống.");
                return;
            }
            new PlaceOrderFrm(client, cart, appliedVoucher, this).setVisible(true);
            this.setVisible(false);
        }
    }

    private void applyVoucher() {
        String code = txtVoucher.getText().trim();
        if (code.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập mã giảm giá."); return; }

        VoucherDAO vd = new VoucherDAO();
        Voucher v = vd.findByCode(code);
        if (v == null) {
            JOptionPane.showMessageDialog(this,
                    "Mã '" + code + "' không hợp lệ, đã hết hạn, hoặc đã sử dụng hết.");
            return;
        }
        appliedVoucher = v;
        JOptionPane.showMessageDialog(this,
                "Đã áp dụng mã! Giảm giá: " + v.getDiscountValue() + "%"
                        + "\n(Đơn tối thiểu: " + v.getMinOrderAmount() + " VNĐ)");
        refreshCart();
    }

    public Voucher getAppliedVoucher() { return appliedVoucher; }
}