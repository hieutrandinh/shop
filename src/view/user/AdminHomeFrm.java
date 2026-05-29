package view.user;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.User;
import view.order.ManageOrderFrm;
import view.product.SearchProductFrm;
import view.stat.StatMenuFrm;
import view.user.ManageClientFrm;

public class AdminHomeFrm extends JFrame implements ActionListener {

    private final User user;
    private JButton btnProducts;
    private JButton btnOrders;
    private JButton btnClients;
    private JButton btnStats;
    private JButton btnLogout;

    public AdminHomeFrm(User user) {
        super("Cửa Hàng Thể Thao – Bảng Điều Khiển Quản Trị");
        this.user = user;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel pnTop = new JPanel();
        pnTop.setLayout(new BoxLayout(pnTop, BoxLayout.LINE_AXIS));
        pnTop.add(Box.createHorizontalGlue());
        JLabel lblUser = new JLabel("Đăng nhập với vai trò: " + user.getFullName() + "  ");
        pnTop.add(lblUser);
        pnMain.add(pnTop);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Bảng Điều Khiển Quản Trị");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(26.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 25)));

        btnProducts = createNavButton("🏷  Quản Lý Sản Phẩm");
        btnOrders   = createNavButton("📦  Quản Lý Đơn Hàng");
        btnClients  = createNavButton("👥  Quản Lý Khách Hàng");
        btnStats    = createNavButton("📊  Thống Kê & Báo Cáo");
        btnLogout   = createNavButton("🔒  Đăng Xuất");

        for (JButton btn : new JButton[]{btnProducts, btnOrders, btnClients, btnStats, btnLogout}) {
            pnMain.add(btn);
            pnMain.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        this.setContentPane(pnMain);
        this.setSize(480, 360);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(280, 34));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnProducts) {
            new SearchProductFrm(user, true).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnOrders) {
            new ManageOrderFrm(user).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnClients) {
            new ManageClientFrm(user).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnStats) {
            new StatMenuFrm(user).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất không?", "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrm().setVisible(true);
                this.dispose();
            }
        }
    }
}