package view.order;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.OrderDAO;
import model.Order;
import model.User;
import view.user.AdminHomeFrm;

public class ManageOrderFrm extends JFrame implements ActionListener {

    private final User admin;
    private ArrayList<Order> orders = new ArrayList<>();

    private JComboBox<String> cmbStatusFilter;
    private JButton    btnLoad;
    private JTable     tblOrders;
    private JComboBox<String> cmbNewStatus;
    private JTextField txtTracking;
    private JButton    btnUpdateStatus;
    private JButton    btnViewDetail;
    private JButton    btnBack;

    private static final String[] STATUSES = {
            "PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"
    };

    public ManageOrderFrm(User admin) {
        super("Cửa Hàng Thể Thao – Quản Lý Đơn Hàng");
        this.admin = admin;
        buildUI();
        loadOrders();
    }

    private void buildUI() {
        cmbStatusFilter = new JComboBox<>(STATUSES);
        btnLoad         = new JButton("Tải");
        tblOrders       = new JTable();
        tblOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cmbNewStatus    = new JComboBox<>(STATUSES);
        txtTracking     = new JTextField(14);
        btnUpdateStatus = new JButton("Cập Nhật Trạng Thái");
        btnViewDetail   = new JButton("Xem Chi Tiết");
        btnBack         = new JButton("← Quay lại");

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        pnTop.add(Box.createHorizontalStrut(20));
        pnTop.add(new JLabel("Lọc theo trạng thái:"));
        pnTop.add(cmbStatusFilter);
        pnTop.add(btnLoad);

        JLabel lblTitle = new JLabel("Quản Lý Đơn Hàng", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnLbl);

        JScrollPane scroll = new JScrollPane(tblOrders);
        tblOrders.setFillsViewportHeight(true);

        tblOrders.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblOrders.getSelectedRow();
                    if (row >= 0 && row < orders.size())
                        new OrderDetailFrm(orders.get(row).getId(), true).setVisible(true);
                }
            }
        });

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        pnBottom.add(new JLabel("Trạng thái mới:"));
        pnBottom.add(cmbNewStatus);
        pnBottom.add(new JLabel("Mã vận đơn:"));
        pnBottom.add(txtTracking);
        pnBottom.add(btnUpdateStatus);
        pnBottom.add(Box.createHorizontalStrut(20));
        pnBottom.add(btnViewDetail);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth, BorderLayout.NORTH);
        this.add(scroll,  BorderLayout.CENTER);
        this.add(pnBottom, BorderLayout.SOUTH);

        btnBack.addActionListener(this);
        btnLoad.addActionListener(this);
        btnUpdateStatus.addActionListener(this);
        btnViewDetail.addActionListener(this);

        this.setSize(820, 520);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadOrders() {
        String status = (String) cmbStatusFilter.getSelectedItem();
        OrderDAO od   = new OrderDAO();
        orders        = od.getOrdersByStatus(status);

        String[] cols = {"ID", "Mã Đơn", "Ngày", "Khách Hàng", "Người nhận", "Thanh toán", "Tổng (VNĐ)", "Trạng thái"};
        String[][] data = new String[orders.size()][8];
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            data[i][0] = String.valueOf(o.getId());
            data[i][1] = o.getOrderCode();
            data[i][2] = o.getOrderDate() != null ? o.getOrderDate().toString() : "—";
            data[i][3] = (o.getClient() != null) ? o.getClient().getFullName() : "—";
            data[i][4] = o.getReceiverName();
            data[i][5] = o.getPaymentMethod();
            data[i][6] = String.format("%.0f", o.getTotalAmount());
            data[i][7] = o.getOrderStatus();
        }
        tblOrders.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            new AdminHomeFrm(admin).setVisible(true);
            this.dispose();

        } else if (e.getSource() == btnLoad) {
            loadOrders();

        } else if (e.getSource() == btnViewDetail) {
            int row = tblOrders.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng."); return; }
            new OrderDetailFrm(orders.get(row).getId(), true).setVisible(true);

        } else if (e.getSource() == btnUpdateStatus) {
            int row = tblOrders.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn hàng."); return; }

            Order selected    = orders.get(row);
            String newStatus  = (String) cmbNewStatus.getSelectedItem();
            String tracking   = txtTracking.getText().trim();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Chuyển trạng thái đơn hàng '" + selected.getOrderCode() + "' thành " + newStatus + "?",
                    "Xác nhận cập nhật trạng thái", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            OrderDAO od = new OrderDAO();
            if (od.updateOrderStatus(selected.getId(), newStatus, tracking)) {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái đơn hàng thành công.");
                loadOrders();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái đơn hàng thất bại.");
            }
        }
    }
}