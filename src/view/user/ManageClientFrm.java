package view.user;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.UserDAO;
import model.Client;
import model.User;

public class ManageClientFrm extends JFrame implements ActionListener {

    private final User admin;
    private ArrayList<Client> listClient = new ArrayList<>();

    private JTextField txtSearch;
    private JButton    btnSearch;
    private JTable     tblClients;
    private JButton    btnActivate;
    private JButton    btnDeactivate;
    private JButton    btnBan;
    private JButton    btnBack;

    public ManageClientFrm(User admin) {
        super("Cửa Hàng Thể Thao – Quản Lý Khách Hàng");
        this.admin = admin;

        txtSearch    = new JTextField(16);
        btnSearch    = new JButton("Tìm kiếm");
        btnActivate  = new JButton("Kích hoạt");
        btnDeactivate = new JButton("Vô hiệu hóa");
        btnBan       = new JButton("Cấm");
        btnBack      = new JButton("← Quay lại");
        tblClients   = new JTable();

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        pnTop.add(Box.createHorizontalStrut(20));
        pnTop.add(new JLabel("Tìm kiếm theo tên:"));
        pnTop.add(txtSearch);
        pnTop.add(btnSearch);

        JScrollPane scroll = new JScrollPane(tblClients);
        tblClients.setFillsViewportHeight(true);
        tblClients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel pnBottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        pnBottom.add(new JLabel("Tài khoản đã chọn:"));
        pnBottom.add(btnActivate);
        pnBottom.add(btnDeactivate);
        pnBottom.add(btnBan);

        JLabel lblTitle = new JLabel("Quản Lý Khách Hàng", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnTitle = new JPanel();
        pnTitle.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnTitle);
        this.getContentPane().removeAll();
        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth,  BorderLayout.NORTH);
        this.add(scroll,   BorderLayout.CENTER);
        this.add(pnBottom, BorderLayout.SOUTH);

        btnSearch.addActionListener(this);
        btnActivate.addActionListener(this);
        btnDeactivate.addActionListener(this);
        btnBan.addActionListener(this);
        btnBack.addActionListener(this);

        this.setSize(720, 460);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        loadClients("%");
    }

    private void loadClients(String key) {
        UserDAO ud = new UserDAO();
        listClient  = ud.searchClients(key);
        refreshTable();
    }

    private void refreshTable() {
        String[] cols = {"ID", "Tên đăng nhập", "Họ tên", "Số điện thoại", "Email", "Tổng chi tiêu", "Trạng thái"};
        String[][] data = new String[listClient.size()][7];
        for (int i = 0; i < listClient.size(); i++) {
            Client c = listClient.get(i);
            data[i][0] = String.valueOf(c.getId());
            data[i][1] = c.getUsername();
            data[i][2] = c.getFullName();
            data[i][3] = c.getTel();
            data[i][4] = c.getEmail();
            data[i][5] = String.format("%.0f", c.getTotalSpent());
            data[i][6] = c.getStatus();
        }
        tblClients.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            new AdminHomeFrm(admin).setVisible(true);
            this.dispose();

        } else if (e.getSource() == btnSearch) {
            String key = txtSearch.getText().trim();
            loadClients(key.isEmpty() ? "%" : key);

        } else {
            int row = tblClients.getSelectedRow();
            if (row < 0 || row >= listClient.size()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng.");
                return;
            }
            Client selected = listClient.get(row);
            String newStatus;
            if (e.getSource() == btnActivate)   newStatus = "ACTIVE";
            else if (e.getSource() == btnDeactivate) newStatus = "INACTIVE";
            else newStatus = "BANNED";

            UserDAO ud = new UserDAO();
            if (ud.updateStatus(selected.getId(), newStatus)) {
                selected.setStatus(newStatus);
                refreshTable();
                JOptionPane.showMessageDialog(this,
                        "Tài khoản của " + selected.getFullName() + " hiện đã được chuyển sang " + newStatus + ".");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật trạng thái thất bại.");
            }
        }
    }
}