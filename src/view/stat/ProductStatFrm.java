package view.stat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.StatDAO;
import model.ProductStat;
import model.User;

public class ProductStatFrm extends JFrame implements ActionListener {

    private final User admin;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private JTextField  txtFrom;
    private JTextField  txtTo;
    private JSpinner    spnTopN;
    private JButton     btnLoad;
    private JButton     btnBack;
    private JTable      tblStats;

    public ProductStatFrm(User admin) {
        super("Cửa Hàng Thể Thao – Sản Phẩm Bán Chạy Nhất");
        this.admin = admin;
        buildUI();
    }

    private void buildUI() {
        String today        = SDF.format(new Date());
        String firstOfMonth = today.substring(0, 8) + "01";
        txtFrom = new JTextField(firstOfMonth, 12);
        txtTo   = new JTextField(today, 12);
        spnTopN = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        btnLoad = new JButton("Tạo Báo Cáo");
        btnBack = new JButton("← Quay lại");
        tblStats = new JTable();

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        pnTop.add(Box.createHorizontalStrut(16));
        pnTop.add(new JLabel("Từ:"));
        pnTop.add(txtFrom);
        pnTop.add(new JLabel("Đến:"));
        pnTop.add(txtTo);
        pnTop.add(new JLabel("Top N:"));
        pnTop.add(spnTopN);
        pnTop.add(btnLoad);

        JLabel lblTitle = new JLabel("Sản Phẩm Bán Chạy Nhất", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnLbl);


        JScrollPane scroll = new JScrollPane(tblStats);
        tblStats.setFillsViewportHeight(true);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnBackBottom = new JButton("← Quay lại Menu Thống Kê");
        btnBackBottom.addActionListener(e -> goBack());
        pnBottom.add(btnBackBottom);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth,  BorderLayout.NORTH);
        this.add(scroll,   BorderLayout.CENTER);
        this.add(pnBottom, BorderLayout.SOUTH);

        btnBack.addActionListener(this);
        btnLoad.addActionListener(this);

        this.setSize(700, 480);
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
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Sử dụng yyyy-MM-dd.");
            return;
        }
        if (from.after(to)) {
            JOptionPane.showMessageDialog(this, "Ngày 'Từ' phải trước ngày 'Đến'.");
            return;
        }

        int topN = (Integer) spnTopN.getValue();
        StatDAO sd = new StatDAO();
        ArrayList<ProductStat> stats = sd.getTopProducts(from, to, topN);

        String[] cols = {"Hạng", "Tên Sản Phẩm", "Danh Mục", "SL Đã Bán", "Doanh Thu (VNĐ)"};
        String[][] data = new String[stats.size()][5];
        for (int i = 0; i < stats.size(); i++) {
            ProductStat ps = stats.get(i);
            data[i][0] = String.valueOf(ps.getRank());
            data[i][1] = ps.getProductName();
            data[i][2] = ps.getCategoryName();
            data[i][3] = String.valueOf(ps.getSoldQuantity());
            data[i][4] = String.format("%,.0f", ps.getRevenue());
        }
        tblStats.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        if (stats.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy đơn hàng nào đã giao trong giai đoạn này.");
        }
    }

    private void goBack() {
        new StatMenuFrm(admin).setVisible(true);
        this.dispose();
    }
}