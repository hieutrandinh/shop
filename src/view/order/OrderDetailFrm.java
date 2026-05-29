package view.order;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.OrderDAO;
import model.Order;
import model.OrderLine;

public class OrderDetailFrm extends JFrame {

    public OrderDetailFrm(int orderId, boolean isAdmin) {
        super("Chi Tiết Đơn Hàng");

        OrderDAO od = new OrderDAO();
        Order order = od.getOrderDetail(orderId);

        if (order == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy đơn hàng #" + orderId);
            this.dispose();
            return;
        }

        JPanel pnHeader = new JPanel(new GridLayout(0, 2, 6, 4));
        pnHeader.setBorder(BorderFactory.createTitledBorder("Thông Tin Đơn Hàng"));
        addRow(pnHeader, "Mã đơn hàng:",   order.getOrderCode());
        addRow(pnHeader, "Ngày:",         order.getOrderDate() != null ? order.getOrderDate().toString() : "—");
        addRow(pnHeader, "Người nhận:",     order.getReceiverName());
        addRow(pnHeader, "Địa chỉ:",      order.getShippingAddress());
        addRow(pnHeader, "Thanh toán:",      order.getPaymentMethod());
        addRow(pnHeader, "Trạng thái:",       order.getOrderStatus());
        addRow(pnHeader, "Vận đơn:",     order.getTrackingCode() != null ? order.getTrackingCode() : "—");
        addRow(pnHeader, "Tổng (VNĐ):",  String.format("%.0f", order.getTotalAmount()));

        ArrayList<OrderLine> lines = order.getOrderLines();
        String[] cols = {"ID Phiên Bản", "Kích cỡ", "Màu sắc", "Đơn giá", "Số lượng", "Thành tiền"};
        String[][] data = new String[lines.size()][6];
        for (int i = 0; i < lines.size(); i++) {
            OrderLine line = lines.get(i);
            model.ProductVariant v = line.getVariant();
            data[i][0] = (v != null) ? String.valueOf(v.getId()) : "—";
            data[i][1] = (v != null) ? v.getSize()  : "—";
            data[i][2] = (v != null) ? v.getColor() : "—";
            data[i][3] = String.format("%.0f", line.getPriceAtPurchase());
            data[i][4] = String.valueOf(line.getQuantity());
            data[i][5] = String.format("%.0f", line.getSubTotal());
        }
        JTable tblLines = new JTable(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
        JScrollPane scroll = new JScrollPane(tblLines);
        scroll.setPreferredSize(new Dimension(600, 180));

        JPanel pnLines = new JPanel(new BorderLayout());
        pnLines.setBorder(BorderFactory.createTitledBorder("Chi Tiết Sản Phẩm"));
        pnLines.add(scroll, BorderLayout.CENTER);

        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(ev -> this.dispose());
        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        if (isAdmin) pnBottom.add(new JLabel("Sử dụng Quản Lý Đơn Hàng để cập nhật trạng thái.  "));
        pnBottom.add(btnClose);

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(pnHeader);
        pnMain.add(Box.createRigidArea(new Dimension(0, 6)));
        pnMain.add(pnLines);
        pnMain.add(pnBottom);

        this.setContentPane(new JScrollPane(pnMain));
        this.setSize(660, 520);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void addRow(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        panel.add(new JLabel(value));
    }
}