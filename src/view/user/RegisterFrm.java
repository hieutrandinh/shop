package view.user;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
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
import model.Client;

public class RegisterFrm extends JFrame implements ActionListener {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JTextField     txtFullName;
    private JTextField     txtTel;
    private JTextField     txtAddress;
    private JTextField     txtEmail;
    private JButton        btnRegister;
    private JButton        btnCancel;

    public RegisterFrm() {
        super("Cửa Hàng Thể Thao – Đăng Ký");

        txtUsername = new JTextField(18);
        txtPassword = new JPasswordField(18);
        txtPassword.setEchoChar('*');
        txtConfirm  = new JPasswordField(18);
        txtConfirm.setEchoChar('*');
        txtFullName = new JTextField(18);
        txtTel      = new JTextField(18);
        txtAddress  = new JTextField(18);
        txtEmail    = new JTextField(18);
        btnRegister = new JButton("Đăng ký");
        btnCancel   = new JButton("Hủy");

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblTitle = new JLabel("Tạo Tài Khoản Mới");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel grid = new JPanel(new GridLayout(7, 2, 6, 6));
        grid.add(new JLabel("Tên đăng nhập:"));       grid.add(txtUsername);
        grid.add(new JLabel("Mật khẩu:"));       grid.add(txtPassword);
        grid.add(new JLabel("Xác nhận mật khẩu:")); grid.add(txtConfirm);
        grid.add(new JLabel("Họ tên:"));      grid.add(txtFullName);
        grid.add(new JLabel("Số điện thoại:"));          grid.add(txtTel);
        grid.add(new JLabel("Địa chỉ:"));        grid.add(txtAddress);
        grid.add(new JLabel("Email:"));          grid.add(txtEmail);
        pnMain.add(grid);
        pnMain.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel pnBtn = new JPanel();
        pnBtn.add(btnRegister);
        pnBtn.add(btnCancel);
        pnMain.add(pnBtn);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        btnRegister.addActionListener(this);
        btnCancel.addActionListener(this);

        this.setContentPane(pnMain);
        this.setSize(420, 340);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            this.dispose();
            return;
        }
        if (e.getSource() == btnRegister) {
            handleRegister();
        }
    }

    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm  = new String(txtConfirm.getPassword());
        String fullName = txtFullName.getText().trim();
        String tel      = txtTel.getText().trim();
        String address  = txtAddress.getText().trim();
        String email    = txtEmail.getText().trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()
                || tel.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập, mật khẩu, họ tên, số điện thoại và địa chỉ.");
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không khớp.");
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        UserDAO ud = new UserDAO();
        if (ud.isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập '" + username + "' đã được sử dụng. Vui lòng chọn tên khác.");
            return;
        }

        Client client = new Client();
        client.setUsername(username);
        client.setPassword(password);
        client.setFullName(fullName);
        client.setTel(tel);
        client.setAddress(address);
        client.setEmail(email);

        if (ud.registerClient(client)) {
            JOptionPane.showMessageDialog(this,
                    "Tạo tài khoản thành công! Bạn có thể đăng nhập ngay bây giờ.");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Đăng ký thất bại. Vui lòng thử lại.");
        }
    }
}