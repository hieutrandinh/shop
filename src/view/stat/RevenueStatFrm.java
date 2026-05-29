package view.stat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.StatDAO;
import model.RevenueStat;
import model.User;

public class RevenueStatFrm extends JFrame implements ActionListener {

    private final User   admin;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private JTextField txtFrom;
    private JTextField txtTo;
    private JButton    btnLoad;
    private JButton    btnBack;

    private JLabel lblPeriod;
    private JLabel lblTotalOrders;
    private JLabel lblTotalRevenue;

    private JTable tblByCategory;

    public RevenueStatFrm(User admin) {
        super("Cửa Hàng Thể Thao – Thống Kê Doanh Thu");
        this.admin = admin;
        buildUI();
    }

    private void buildUI() {
        String today = SDF.format(new Date());
        String firstOfMonth = today.substring(0, 8) + "01";
        txtFrom = new JTextField(firstOfMonth, 12);
        txtTo   = new JTextField(today, 12);
        btnLoad = new JButton("Tạo Báo Cáo");
        btnBack = new JButton("← Quay lại");
        lblPeriod       = new JLabel("—");
        lblTotalOrders  = new JLabel("—");
        lblTotalRevenue = new JLabel("—");
        tblByCategory   = new JTable();

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        pnTop.add(Box.createHorizontalStrut(16));
        pnTop.add(new JLabel("Từ (yyyy-MM-dd):"));
        pnTop.add(txtFrom);
        pnTop.add(new JLabel("Đến:"));
        pnTop.add(txtTo);
        pnTop.add(btnLoad);

        JLabel lblTitle = new JLabel("Thống Kê Doanh Thu", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnLbl);

        JPanel pnSummary = new JPanel(new GridLayout(3, 2, 6, 4));
        pnSummary.setBorder(BorderFactory.createTitledBorder("Tóm tắt"));
        pnSummary.add(new JLabel("Giai đoạn:"));          pnSummary.add(lblPeriod);
        pnSummary.add(new JLabel("Tổng số đơn hàng:"));    pnSummary.add(lblTotalOrders);
        pnSummary.add(new JLabel("Tổng doanh thu:"));   pnSummary.add(lblTotalRevenue);

        JScrollPane scrollCat = new JScrollPane(tblByCategory);
        scrollCat.setPreferredSize(new Dimension(600, 220));
        JPanel pnCat = new JPanel(new BorderLayout());
        pnCat.setBorder(BorderFactory.createTitledBorder("Doanh Thu Theo Danh Mục"));
        pnCat.add(scrollCat, BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnBackBottom = new JButton("← Quay lại Menu Thống Kê");
        btnBackBottom.addActionListener(e -> goBack());
        pnBottom.add(btnBackBottom);

        JPanel pnCenter = new JPanel();
        pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.PAGE_AXIS));
        pnCenter.add(pnSummary);
        pnCenter.add(Box.createRigidArea(new Dimension(0, 8)));
        pnCenter.add(pnCat);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth,  BorderLayout.NORTH);
        this.add(pnCenter, BorderLayout.CENTER);
        this.add(pnBottom, BorderLayout.SOUTH);

        btnBack.addActionListener(this);
        btnLoad.addActionListener(this);

        this.setSize(680, 560);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            goBack();
        } else if (e.getSource() == btnLoad) {
            loadStats();
        }
    }

    private void loadStats() {
        Date from, to;
        try {
            from = SDF.parse(txtFrom.getText().trim());
            to   = SDF.parse(txtTo.getText().trim());
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Định dạng ngày không hợp lệ. Sử dụng yyyy-MM-dd (VD: 2025-01-01).");
            return;
        }
        if (from.after(to)) {
            JOptionPane.showMessageDialog(this, "Ngày 'Từ' phải trước ngày 'Đến'.");
            return;
        }

        StatDAO sd = new StatDAO();

        RevenueStat summary = sd.getRevenueStat(from, to);
        if (summary != null) {
            lblPeriod.setText(summary.getPeriod());
            lblTotalOrders.setText(String.valueOf(summary.getTotalOrders()));
            lblTotalRevenue.setText(String.format("%,.0f VNĐ", summary.getTotalRevenue()));
        } else {
            lblPeriod.setText("—"); lblTotalOrders.setText("0"); lblTotalRevenue.setText("0 VNĐ");
        }

        ArrayList<RevenueStat> catStats = sd.getRevenueByCategory(from, to);
        String[] cols = {"Danh Mục", "Doanh Thu (VNĐ)"};
        String[][] data = new String[catStats.size()][2];
        for (int i = 0; i < catStats.size(); i++) {
            data[i][0] = catStats.get(i).getPeriod();
            data[i][1] = String.format("%,.0f", catStats.get(i).getRevenueByCategory());
        }
        tblByCategory.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void goBack() {
        new StatMenuFrm(admin).setVisible(true);
        this.dispose();
    }
}