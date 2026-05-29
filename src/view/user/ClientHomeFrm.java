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
import model.Client;
import view.order.CartFrm;
import view.order.MyOrdersFrm;
import view.product.SearchProductFrm;

public class ClientHomeFrm extends JFrame implements ActionListener {

    private final Client client;
    private JButton btnBrowse;
    private JButton btnCart;
    private JButton btnOrders;
    private JButton btnChangePass;
    private JButton btnLogout;

    private java.util.ArrayList<model.OrderLine> cart = new java.util.ArrayList<>();

    public ClientHomeFrm(Client client) {
        super("Cửa Hàng Thể Thao");
        this.client = client;

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel pnTop = new JPanel();
        pnTop.setLayout(new BoxLayout(pnTop, BoxLayout.LINE_AXIS));
        pnTop.add(Box.createHorizontalGlue());
        JLabel lblUser = new JLabel("Xin chào, " + client.getFullName() + "  ");
        pnTop.add(lblUser);
        pnMain.add(pnTop);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel lblTitle = new JLabel("Cửa Hàng Thể Thao");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(26.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 25)));

        btnBrowse     = createNavButton("Xem Sản Phẩm");
        btnCart       = createNavButton("Giỏ Hàng");
        btnOrders     = createNavButton("Đơn Hàng Của Tôi");
        btnChangePass = createNavButton("Đổi Mật Khẩu");
        btnLogout     = createNavButton("Đăng Xuất");

        for (JButton btn : new JButton[]{btnBrowse, btnCart, btnOrders, btnChangePass, btnLogout}) {
            pnMain.add(btn);
            pnMain.add(Box.createRigidArea(new Dimension(0, 8)));
        }

        this.setContentPane(pnMain);
        this.setSize(420, 340);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(260, 34));
        btn.addActionListener(this);
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBrowse) {
            new SearchProductFrm(client, false, cart, this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == btnCart) {
            new CartFrm(client, cart, this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == btnOrders) {
            new MyOrdersFrm(client).setVisible(true);
        } else if (e.getSource() == btnChangePass) {
            new ChangePasswordFrm(client.getId()).setVisible(true);
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

    public java.util.ArrayList<model.OrderLine> getCart() {
        return cart;
    }
}