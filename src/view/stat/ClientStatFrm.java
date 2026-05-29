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
import model.ClientStat;
import model.User;

public class ClientStatFrm extends JFrame implements ActionListener {

    private final User admin;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    private JTextField  txtFrom;
    private JTextField  txtTo;
    private JSpinner    spnTopN;
    private JButton     btnLoad;
    private JButton     btnBack;

    private JLabel lblPeriod;
    private JLabel lblNewReg;
    private JLabel lblActiveClients;

    private JTable tblTopClients;

    public ClientStatFrm(User admin) {
        super("Cửa Hàng Thể Thao – Thống Kê Khách Hàng");
        this.admin = admin;
        buildUI();
    }

    private void buildUI() {
        String today        = SDF.format(new Date());
        String firstOfMonth = today.substring(0, 8) + "01";
        txtFrom = new JTextField(firstOfMonth, 12);
        txtTo   = new JTextField(today, 12);
        spnTopN = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        btnLoad = new JButton("Tạo Báo Cáo");
        btnBack = new JButton("← Quay lại");

        lblPeriod        = new JLabel("—");
        lblNewReg        = new JLabel("—");
        lblActiveClients = new JLabel("—");
        tblTopClients    = new JTable();

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

        JLabel lblTitle = new JLabel("Thống Kê Khách Hàng", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnLbl);

        JPanel pnSummary = new JPanel(new GridLayout(3, 2, 6, 4));
        pnSummary.setBorder(BorderFactory.createTitledBorder("Tóm tắt"));
        pnSummary.add(new JLabel("Giai đoạn:"));               pnSummary.add(lblPeriod);
        pnSummary.add(new JLabel("Đăng ký mới:"));    pnSummary.add(lblNewReg);
        pnSummary.add(new JLabel("Khách hàng hoạt động:"));        pnSummary.add(lblActiveClients);

        JScrollPane scrollClients = new JScrollPane(tblTopClients);
        scrollClients.setPreferredSize(new Dimension(600, 200));
        JPanel pnClients = new JPanel(new BorderLayout());
        pnClients.setBorder(BorderFactory.createTitledBorder("Khách Hàng Chi Tiêu Cao Nhất"));
        pnClients.add(scrollClients, BorderLayout.CENTER);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnBackBottom = new JButton("← Quay lại Menu Thống Kê");
        btnBackBottom.addActionListener(e -> goBack());
        pnBottom.add(btnBackBottom);

        JPanel pnCenter = new JPanel();
        pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.PAGE_AXIS));
        pnCenter.add(pnSummary);
        pnCenter.add(Box.createRigidArea(new Dimension(0, 8)));
        pnCenter.add(pnClients);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth,  BorderLayout.NORTH);
        this.add(pnCenter, BorderLayout.CENTER);
        this.add(pnBottom, BorderLayout.SOUTH);

        btnBack.addActionListener(this);
        btnLoad.addActionListener(this);

        this.setSize(680, 540);
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
        ClientStat summary = sd.getClientStat(from, to);
        if (summary != null) {
            lblPeriod.setText(summary.getPeriod());
            lblNewReg.setText(String.valueOf(summary.getNewRegistrations()));
            lblActiveClients.setText(String.valueOf(summary.getActiveCustomers()));
        }
        ArrayList<ClientStat> topClients = sd.getTopClients(from, to, topN);
        String[] cols = {"Hạng", "Tên Khách Hàng", "Tổng Chi Tiêu (VNĐ)"};
        String[][] data = new String[topClients.size()][3];
        for (int i = 0; i < topClients.size(); i++) {
            ClientStat cs = topClients.get(i);
            data[i][0] = String.valueOf(i + 1);
            data[i][1] = cs.getPeriod();
            data[i][2] = String.format("%,.0f", (float) cs.getActiveCustomers());
        }
        tblTopClients.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void goBack() {
        new StatMenuFrm(admin).setVisible(true);
        this.dispose();
    }
}