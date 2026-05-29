package view.user;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import dao.UserDAO;
import model.User;

public class LoginFrm extends JFrame implements ActionListener {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin;
    private JButton        btnRegister;

    public LoginFrm() {
        super("Cửa Hàng Thể Thao – Đăng Nhập");

        txtUsername = new JTextField(18);
        txtPassword = new JPasswordField(18);
        txtPassword.setEchoChar('*');
        btnLogin    = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký tài khoản mới");

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblTitle = new JLabel("Cửa Hàng Thể Thao");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(24.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lblSub = new JLabel("Vui lòng đăng nhập để tiếp tục");
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(lblSub);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel pnUser = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnUser.add(new JLabel("Tên đăng nhập:"));
        pnUser.add(txtUsername);
        pnMain.add(pnUser);

        JPanel pnPass = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnPass.add(new JLabel("Mật khẩu: "));
        pnPass.add(txtPassword);
        pnMain.add(pnPass);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel pnBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnBtn.add(btnLogin);
        pnBtn.add(btnRegister);
        pnMain.add(pnBtn);
        pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);

        this.setContentPane(pnMain);
        this.setSize(420, 230);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            handleLogin();
        } else if (e.getSource() == btnRegister) {
            new RegisterFrm().setVisible(true);
        }
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu.");
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        UserDAO ud = new UserDAO();
        if (ud.checkLogin(user)) {
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                new AdminHomeFrm(user).setVisible(true);
            } else if ("CLIENT".equalsIgnoreCase(user.getRole())) {
                model.Client client = ud.getClientById(user.getId());
                if (client == null) {
                    JOptionPane.showMessageDialog(this, "Không thể tải hồ sơ khách hàng.");
                    return;
                }
                new ClientHomeFrm(client).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Vai trò '" + user.getRole() + "' chưa được hỗ trợ.");
                return;
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Sai tên đăng nhập / mật khẩu, hoặc tài khoản đã bị khóa.");
        }
    }

    public static void main(String[] args) {
        new LoginFrm().setVisible(true);
    }
}