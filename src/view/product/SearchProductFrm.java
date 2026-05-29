package view.product;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ProductDAO;
import model.Category;
import model.Client;
import model.OrderLine;
import model.Product;
import model.User;
import view.user.AdminHomeFrm;
import view.user.ClientHomeFrm;

public class SearchProductFrm extends JFrame implements ActionListener {

    private User admin;
    private boolean isAdmin;

    private Client client;
    private ArrayList<OrderLine> cart;
    private ClientHomeFrm parentFrm;

    private ArrayList<Product> listProduct = new ArrayList<>();

    private JTextField txtKey;
    private JComboBox<String> cmbCategory;
    private JButton btnSearch;
    private JTable tblResult;

    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDelete;

    private JButton btnAddToCart;

    private JButton btnBack;

    private ArrayList<Category> categories;

    public SearchProductFrm(User admin, boolean isAdmin) {
        super("Cửa Hàng Thể Thao – Quản Lý Sản Phẩm");
        this.admin = admin;
        this.isAdmin = true;
        buildUI();
        loadCategories();
        doSearch();
    }

    public SearchProductFrm(Client client, boolean isAdmin,
                            ArrayList<OrderLine> cart, ClientHomeFrm parentFrm) {
        super("Cửa Hàng Thể Thao – Xem Sản Phẩm");
        this.client = client;
        this.isAdmin = false;
        this.cart = cart;
        this.parentFrm = parentFrm;
        buildUI();
        loadCategories();
        doSearch();
    }

    private void buildUI() {
        txtKey = new JTextField(14);
        btnSearch = new JButton("Tìm kiếm");
        btnBack = new JButton("← Quay lại");
        tblResult = new JTable();
        tblResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cmbCategory = new JComboBox<>();

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        pnTop.add(Box.createHorizontalStrut(10));
        pnTop.add(new JLabel("Tên:"));
        pnTop.add(txtKey);
        pnTop.add(new JLabel("Danh mục:"));
        pnTop.add(cmbCategory);
        pnTop.add(btnSearch);

        JLabel lblTitle = new JLabel(
                isAdmin ? "Quản Lý Sản Phẩm" : "Xem Sản Phẩm", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);
        pnNorth.add(pnLbl);

        JScrollPane scroll = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(true);

        tblResult.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleDoubleClick();
            }
        });

        JPanel pnSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        if (isAdmin) {
            btnAdd = new JButton("Thêm Sản Phẩm");
            btnEdit = new JButton("Sửa Lựa Chọn");
            btnDelete = new JButton("Xóa Lựa Chọn");
            pnSouth.add(btnAdd);
            pnSouth.add(btnEdit);
            pnSouth.add(btnDelete);
            btnAdd.addActionListener(this);
            btnEdit.addActionListener(this);
            btnDelete.addActionListener(this);
        } else {
            btnAddToCart = new JButton("Thêm vào giỏ hàng");
            pnSouth.add(btnAddToCart);
            btnAddToCart.addActionListener(this);
        }

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth, BorderLayout.NORTH);
        this.add(scroll,  BorderLayout.CENTER);
        this.add(pnSouth, BorderLayout.SOUTH);

        btnSearch.addActionListener(this);
        btnBack.addActionListener(this);

        this.setSize(780, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void loadCategories() {
        ProductDAO pd = new ProductDAO();
        categories = pd.getAllCategories();
        cmbCategory.addItem("Tất cả danh mục");
        for (Category cat : categories) cmbCategory.addItem(cat.getName());
    }

    private void doSearch() {
        String key = txtKey.getText().trim();
        int catIdx = cmbCategory.getSelectedIndex() - 1;
        int catId = (catIdx >= 0) ? categories.get(catIdx).getId() : 0;

        ProductDAO pd = new ProductDAO();
        listProduct = pd.searchProducts(key, catId);

        String[] cols = {"ID", "Mã SKU", "Tên", "Thương hiệu", "Danh mục", "Phân khúc", "Trạng thái"};
        String[][] data = new String[listProduct.size()][7];
        for (int i = 0; i < listProduct.size(); i++) {
            Product p = listProduct.get(i);
            data[i][0] = String.valueOf(p.getId());
            data[i][1] = p.getSku();
            data[i][2] = p.getName();
            data[i][3] = p.getBrand();
            data[i][4] = p.getCategory() != null ? p.getCategory().getName() : "";
            data[i][5] = p.getTier();
            data[i][6] = p.getStatus();
        }
        tblResult.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void handleDoubleClick() {
        int row = tblResult.getSelectedRow();
        if (row < 0 || row >= listProduct.size()) return;
        Product selected = listProduct.get(row);
        if (isAdmin) {
            new EditProductFrm(admin, selected).setVisible(true);
            this.dispose();
        } else {
            new ProductDetailFrm(client, selected, cart, parentFrm).setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            goBack();
        } else if (e.getSource() == btnSearch) {
            doSearch();
        } else if (isAdmin) {
            handleAdminAction(e);
        } else {
            handleClientAction(e);
        }
    }

    private void handleAdminAction(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            new AddProductFrm(admin).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnEdit) {
            int row = tblResult.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trước."); return; }
            new EditProductFrm(admin, listProduct.get(row)).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnDelete) {
            int row = tblResult.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trước."); return; }
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Đánh dấu sản phẩm '" + listProduct.get(row).getName() + "' là KHÔNG HOẠT ĐỘNG?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ProductDAO pd = new ProductDAO();
                if (pd.deleteProduct(listProduct.get(row).getId())) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm đã bị vô hiệu hóa.");
                    doSearch();
                } else {
                    JOptionPane.showMessageDialog(this, "Vô hiệu hóa sản phẩm thất bại.");
                }
            }
        }
    }

    private void handleClientAction(ActionEvent e) {
        if (e.getSource() == btnAddToCart) {
            int row = tblResult.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm trước."); return; }
            new ProductDetailFrm(client, listProduct.get(row), cart, parentFrm).setVisible(true);
        }
    }

    private void goBack() {
        if (isAdmin) {
            new AdminHomeFrm(admin).setVisible(true);
        } else {
            parentFrm.setVisible(true);
        }
        this.dispose();
    }
}