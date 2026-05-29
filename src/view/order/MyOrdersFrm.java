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
import model.Client;
import model.Order;

public class MyOrdersFrm extends JFrame implements ActionListener {

    private final Client client;
    private ArrayList<Order> orders = new ArrayList<>();

    private JTable  tblOrders;
    private JButton btnRefresh;
    private JButton btnDetail;
    private JButton btnClose;

    public MyOrdersFrm(Client client) {
        super("Cửa Hàng Thể Thao – Đơn Hàng Của Tôi");
        this.client = client;
        buildUI();
        loadOrders();
    }

    private void buildUI() {
        tblOrders  = new JTable();
        tblOrders.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        btnRefresh = new JButton("Làm Mới");
        btnDetail  = new JButton("Xem Chi Tiết");
        btnClose   = new JButton("Đóng");

        JLabel lblTitle = new JLabel("Đơn Hàng Của Tôi", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnTop = new JPanel(); pnTop.add(lblTitle);

        JScrollPane scroll = new JScrollPane(tblOrders);
        tblOrders.setFillsViewportHeight(true);

        tblOrders.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openDetail();
            }
        });

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        pnBottom.add(btnRefresh);
        pnBottom.add(btnDetail);
        pnBottom.add(btnClose);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnTop,    BorderLayout.NORTH);
        this.add(scroll,   BorderLayout.CENTER);
        this.add(pnBottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(this);
        btnDetail.addActionListener(this);
        btnClose.addActionListener(this);

        this.setSize(740, 420);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadOrders() {
        OrderDAO od = new OrderDAO();
        orders = od.getOrdersByClient(client.getId());

        String[] cols = {"Mã Đơn Hàng", "Ngày", "Người Nhận", "Thanh Toán", "Trạng Thái", "Tổng (VNĐ)"};
        String[][] data = new String[orders.size()][6];
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            data[i][0] = o.getOrderCode();
            data[i][1] = o.getOrderDate() != null ? o.getOrderDate().toString() : "—";
            data[i][2] = o.getReceiverName();
            data[i][3] = o.getPaymentMethod();
            data[i][4] = o.getOrderStatus();
            data[i][5] = String.format("%.0f", o.getTotalAmount());
        }
        tblOrders.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void openDetail() {
        int row = tblOrders.getSelectedRow();
        if (row < 0 || row >= orders.size()) return;
        new OrderDetailFrm(orders.get(row).getId(), false).setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRefresh) {
            loadOrders();
        } else if (e.getSource() == btnDetail) {
            openDetail();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        }
    }
}