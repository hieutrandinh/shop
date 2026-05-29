package view.product;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import dao.ProductDAO;
import model.Category;
import model.Product;
import model.ProductVariant;
import model.User;
import view.user.AdminHomeFrm;

public class AddProductFrm extends JFrame implements ActionListener {

    private final User admin;
    private ArrayList<ProductVariant> variants = new ArrayList<>();

    private JTextField  txtSku, txtName, txtBrand, txtDescription, txtImages;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbTier;
    private JComboBox<String> cmbStudType;
    private JComboBox<String> cmbStatus;

    private JTextField  txtVSize, txtVColor, txtVPrice, txtVStock, txtVThreshold, txtVImage;
    private JButton     btnAddVariant;

    private JTable      tblVariants;
    private JButton     btnRemoveVariant;

    private JButton btnSave;
    private JButton btnCancel;

    private ArrayList<Category> categories;

    public AddProductFrm(User admin) {
        super("Cửa Hàng Thể Thao – Thêm Sản Phẩm");
        this.admin = admin;
        buildUI();
    }

    private void buildUI() {
        txtSku         = new JTextField(14);
        txtName        = new JTextField(14);
        txtBrand       = new JTextField(14);
        txtDescription = new JTextField(20);
        txtImages      = new JTextField(20);
        cmbCategory    = new JComboBox<>();
        cmbTier        = new JComboBox<>(new String[]{"BEGINNER", "INTERMEDIATE", "PROFESSIONAL"});
        cmbStudType    = new JComboBox<>(new String[]{"FIRM_GROUND", "SOFT_GROUND", "ARTIFICIAL_GROUND", "INDOOR", "NONE"});
        cmbStatus      = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});

        txtVSize      = new JTextField(6);
        txtVColor     = new JTextField(8);
        txtVPrice     = new JTextField(8);
        txtVStock     = new JTextField(6);
        txtVThreshold = new JTextField(6);
        txtVImage     = new JTextField(10);
        btnAddVariant    = new JButton("Thêm Phiên Bản");
        btnRemoveVariant = new JButton("Xóa Lựa Chọn");
        btnSave          = new JButton("Lưu Sản Phẩm");
        btnCancel        = new JButton("Hủy");
        tblVariants      = new JTable();
        tblVariants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ProductDAO pd = new ProductDAO();
        categories = pd.getAllCategories();
        for (Category cat : categories) cmbCategory.addItem(cat.getName());

        JPanel pnInfo = new JPanel(new GridLayout(0, 4, 6, 6));
        pnInfo.setBorder(BorderFactory.createTitledBorder("Thông Tin Sản Phẩm"));
        pnInfo.add(new JLabel("Mã SKU:"));           pnInfo.add(txtSku);
        pnInfo.add(new JLabel("Tên:"));          pnInfo.add(txtName);
        pnInfo.add(new JLabel("Thương hiệu:"));         pnInfo.add(txtBrand);
        pnInfo.add(new JLabel("Danh mục:"));      pnInfo.add(cmbCategory);
        pnInfo.add(new JLabel("Phân khúc:"));          pnInfo.add(cmbTier);
        pnInfo.add(new JLabel("Loại đinh:"));     pnInfo.add(cmbStudType);
        pnInfo.add(new JLabel("Trạng thái:"));        pnInfo.add(cmbStatus);
        pnInfo.add(new JLabel("Mô tả:"));   pnInfo.add(txtDescription);
        pnInfo.add(new JLabel("Ảnh (URL):"));  pnInfo.add(txtImages);

        JPanel pnVInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pnVInput.setBorder(BorderFactory.createTitledBorder("Thêm Phiên Bản"));
        pnVInput.add(new JLabel("Kích cỡ:")); pnVInput.add(txtVSize);
        pnVInput.add(new JLabel("Màu:")); pnVInput.add(txtVColor);
        pnVInput.add(new JLabel("Giá:")); pnVInput.add(txtVPrice);
        pnVInput.add(new JLabel("Tồn kho:")); pnVInput.add(txtVStock);
        pnVInput.add(new JLabel("Ngưỡng AT:")); pnVInput.add(txtVThreshold);
        pnVInput.add(new JLabel("Ảnh:")); pnVInput.add(txtVImage);
        pnVInput.add(btnAddVariant);

        JScrollPane scrollV = new JScrollPane(tblVariants);
        scrollV.setPreferredSize(new Dimension(680, 140));
        refreshVariantTable();

        JPanel pnVBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnVBottom.add(btnRemoveVariant);

        JPanel pnVariants = new JPanel(new BorderLayout(0, 2));
        pnVariants.setBorder(BorderFactory.createTitledBorder("Danh Sách Phiên Bản"));
        pnVariants.add(scrollV,   BorderLayout.CENTER);
        pnVariants.add(pnVBottom, BorderLayout.SOUTH);

        JPanel pnActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        pnActions.add(btnSave);
        pnActions.add(btnCancel);

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));

        JLabel lblTitle = new JLabel("Thêm Sản Phẩm Mới", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 8)));
        pnMain.add(pnInfo);
        pnMain.add(pnVInput);
        pnMain.add(pnVariants);
        pnMain.add(pnActions);

        btnAddVariant.addActionListener(this);
        btnRemoveVariant.addActionListener(this);
        btnSave.addActionListener(this);
        btnCancel.addActionListener(this);

        JScrollPane mainScroll = new JScrollPane(pnMain);
        this.setContentPane(mainScroll);
        this.setSize(760, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void refreshVariantTable() {
        String[] cols = {"Kích cỡ", "Màu sắc", "Giá", "Tồn kho", "Ngưỡng An Toàn", "Ảnh"};
        String[][] data = new String[variants.size()][6];
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant v = variants.get(i);
            data[i][0] = v.getSize();
            data[i][1] = v.getColor();
            data[i][2] = String.valueOf(v.getPrice());
            data[i][3] = String.valueOf(v.getStock());
            data[i][4] = String.valueOf(v.getSafetyThreshold());
            data[i][5] = v.getImage();
        }
        tblVariants.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCancel) {
            goBack();
        } else if (e.getSource() == btnAddVariant) {
            addVariantRow();
        } else if (e.getSource() == btnRemoveVariant) {
            removeVariantRow();
        } else if (e.getSource() == btnSave) {
            saveProduct();
        }
    }

    private void addVariantRow() {
        try {
            String size  = txtVSize.getText().trim();
            String color = txtVColor.getText().trim();
            if (size.isEmpty() || color.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập kích cỡ và màu sắc.");
                return;
            }
            float price = Float.parseFloat(txtVPrice.getText().trim());
            int stock   = Integer.parseInt(txtVStock.getText().trim());
            int safety  = Integer.parseInt(txtVThreshold.getText().trim());
            String img  = txtVImage.getText().trim();

            ProductVariant v = new ProductVariant();
            v.setSize(size); v.setColor(color); v.setPrice(price);
            v.setStock(stock); v.setSafetyThreshold(safety); v.setImage(img);
            variants.add(v);
            refreshVariantTable();

            for (JTextField f : new JTextField[]{txtVSize, txtVColor, txtVPrice,
                    txtVStock, txtVThreshold, txtVImage})
                f.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá, Tồn kho và Ngưỡng An Toàn phải là số.");
        }
    }

    private void removeVariantRow() {
        int row = tblVariants.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiên bản."); return; }
        variants.remove(row);
        refreshVariantTable();
    }

    private void saveProduct() {
        String sku  = txtSku.getText().trim();
        String name = txtName.getText().trim();
        String brand = txtBrand.getText().trim();
        if (sku.isEmpty() || name.isEmpty() || brand.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã SKU, Tên và Thương hiệu không được để trống.");
            return;
        }
        if (variants.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất một phiên bản.");
            return;
        }

        int catIdx = cmbCategory.getSelectedIndex();
        Category cat = categories.get(catIdx);

        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setBrand(brand);
        product.setTier((String) cmbTier.getSelectedItem());
        product.setStudType((String) cmbStudType.getSelectedItem());
        product.setDescription(txtDescription.getText().trim());
        product.setImages(txtImages.getText().trim());
        product.setStatus((String) cmbStatus.getSelectedItem());
        product.setCategory(cat);
        product.setVariants(variants);

        ProductDAO pd = new ProductDAO();
        if (pd.addProduct(product)) {
            JOptionPane.showMessageDialog(this, "Đã thêm sản phẩm thành công!");
            goBack();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại. Vui lòng kiểm tra lại dữ liệu.");
        }
    }

    private void goBack() {
        new view.product.SearchProductFrm(admin, true).setVisible(true);
        this.dispose();
    }
}