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
import dao.UserDAO;

public class ChangePasswordFrm extends JFrame implements ActionListener {

    private final int userId;
    private JPasswordField txtOld;
    private JPasswordField txtNew;
    private JPasswordField txtConfirm;
    private JButton        btnSave;
    private JButton        btnCancel;

    public ChangePasswordFrm(int userId) {
        super("Đổi Mật Khẩu");
        this.userId = userId;

        txtOld     = new JPasswordField(16);  txtOld.setEchoChar('*');
        txtNew     = new JPasswordField(16);  txtNew.setEchoChar('*');
        txtConfirm = new JPasswordField(16);  txtConfirm.setEchoChar('*');
        btnSave    = new JButton("Lưu");
        btnCancel  = new JButton("Hủy");

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblTitle = new JLabel("Đổi Mật Khẩu");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(lblTitle.getFont().deriveFont(18.0f));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel grid = new JPanel(new GridLayout(3, 2, 6, 8));
        grid.add(new JLabel("Mật khẩu hiện tại:"));  grid.add(txtOld);
        grid.add(new JLabel("Mật khẩu mới:"));       grid.add(txtNew);
        grid.add(new JLabel("Xác nhận mật khẩu mới:"));        grid.add(txtConfirm);
        pnMain.add(grid);
        pnMain.add(Box.createRigidArea(new Dimension(0, 14)));

        JPanel pnBtn = new JPanel();
        pnBtn.add(btnSave);
        pnBtn.add(btnCancel);
        pnMain.add(pnBtn);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        btnSave.addActionListener(this);
        btnCancel.addActionListener(this);

        this.setContentPane(pnMain);
        this.setSize(360, 220);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            this.dispose();
        } else if (e.getSource() == btnSave) {
            handleSave();
        }
    }

    private void handleSave() {
        String oldPass  = new String(txtOld.getPassword());
        String newPass  = new String(txtNew.getPassword());
        String confirm  = new String(txtConfirm.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin.");
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới không khớp.");
            return;
        }
        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu phải có ít nhất 6 ký tự.");
            return;
        }

        UserDAO ud = new UserDAO();
        if (ud.changePassword(userId, oldPass, newPass)) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công.");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu hiện tại không đúng. Vui lòng thử lại.");
        }
    }
}