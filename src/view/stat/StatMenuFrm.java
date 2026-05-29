package view.stat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import model.User;
import view.user.AdminHomeFrm;

public class StatMenuFrm extends JFrame implements ActionListener {

    private final User admin;
    private JButton btnRevenue;
    private JButton btnProducts;
    private JButton btnClients;
    private JButton btnBack;

    public StatMenuFrm(User admin) {
        super("Cửa Hàng Thể Thao – Thống Kê & Báo Cáo");
        this.admin = admin;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblTitle = new JLabel("Thống Kê & Báo Cáo");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(22.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        btnRevenue  = createBtn("📈  Thống Kê Doanh Thu");
        btnProducts = createBtn("🏆  Sản Phẩm Bán Chạy Nhất");
        btnClients  = createBtn("👥  Thống Kê Khách Hàng");
        btnBack     = createBtn("← Quay Lại Bảng Điều Khiển");

        for (JButton b : new JButton[]{btnRevenue, btnProducts, btnClients, btnBack}) {
            pnMain.add(b);
            pnMain.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        this.setContentPane(pnMain);
        this.setSize(380, 280);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private JButton createBtn(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(260, 34));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRevenue) {
            new RevenueStatFrm(admin).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnProducts) {
            new ProductStatFrm(admin).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnClients) {
            new ClientStatFrm(admin).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnBack) {
            new AdminHomeFrm(admin).setVisible(true);
            this.dispose();
        }
    }
}