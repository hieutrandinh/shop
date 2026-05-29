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

public class EditProductFrm extends JFrame implements ActionListener {

    private final User    admin;
    private Product       product;
    private ArrayList<ProductVariant> variants;

    private JTextField  txtId, txtSku, txtName, txtBrand, txtDescription, txtImages;
    private JComboBox<String> cmbCategory;
    private JComboBox<String> cmbTier;
    private JComboBox<String> cmbStudType;
    private JComboBox<String> cmbStatus;

    private JTextField  txtVSize, txtVColor, txtVPrice, txtVStock, txtVThreshold, txtVImage;
    private JButton     btnAddVariant;
    private JTable      tblVariants;
    private JButton     btnUpdateVariant;
    private JButton     btnRemoveVariant;

    private JButton btnUpdate;
    private JButton btnReset;
    private JButton btnBack;

    private ArrayList<Category> categories;

    public EditProductFrm(User admin, Product partialProduct) {
        super("Cửa Hàng Thể Thao – Sửa Sản Phẩm");
        this.admin = admin;

        ProductDAO pd = new ProductDAO();
        this.product  = pd.getProductById(partialProduct.getId());
        if (this.product == null) this.product = partialProduct;
        this.variants = (this.product.getVariants() != null)
                ? this.product.getVariants()
                : new ArrayList<>();

        buildUI();
        initForm();
    }

    private void buildUI() {
        txtId          = new JTextField(6);   txtId.setEditable(false);
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
        btnAddVariant    = new JButton("Thêm Phiên Bản Mới");
        btnUpdateVariant = new JButton("Cập Nhật Lựa Chọn");
        btnRemoveVariant = new JButton("Xóa Lựa Chọn");
        btnUpdate        = new JButton("Cập Nhật Sản Phẩm");
        btnReset         = new JButton("Đặt Lại");
        btnBack          = new JButton("← Quay lại");
        tblVariants      = new JTable();
        tblVariants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ProductDAO pd = new ProductDAO();
        categories = pd.getAllCategories();
        for (Category cat : categories) cmbCategory.addItem(cat.getName());

        JPanel pnInfo = new JPanel(new GridLayout(0, 4, 6, 6));
        pnInfo.setBorder(BorderFactory.createTitledBorder("Thông Tin Sản Phẩm"));
        pnInfo.add(new JLabel("ID:"));            pnInfo.add(txtId);
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
        pnVInput.setBorder(BorderFactory.createTitledBorder("Trình Chỉnh Sửa Phiên Bản (chọn dòng để tải)"));
        pnVInput.add(new JLabel("Kích cỡ:")); pnVInput.add(txtVSize);
        pnVInput.add(new JLabel("Màu:")); pnVInput.add(txtVColor);
        pnVInput.add(new JLabel("Giá:")); pnVInput.add(txtVPrice);
        pnVInput.add(new JLabel("Tồn kho:")); pnVInput.add(txtVStock);
        pnVInput.add(new JLabel("Ngưỡng AT:")); pnVInput.add(txtVThreshold);
        pnVInput.add(new JLabel("Ảnh:")); pnVInput.add(txtVImage);
        pnVInput.add(btnUpdateVariant);
        pnVInput.add(btnAddVariant);

        JScrollPane scrollV = new JScrollPane(tblVariants);
        scrollV.setPreferredSize(new Dimension(680, 120));

        JPanel pnVBottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnVBottom.add(btnRemoveVariant);

        JPanel pnVariants = new JPanel(new BorderLayout(0, 2));
        pnVariants.setBorder(BorderFactory.createTitledBorder("Các Phiên Bản"));
        pnVariants.add(scrollV,   BorderLayout.CENTER);
        pnVariants.add(pnVBottom, BorderLayout.SOUTH);

        JPanel pnActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnActions.add(btnBack);
        pnActions.add(Box.createHorizontalStrut(40));
        pnActions.add(btnReset);
        pnActions.add(btnUpdate);

        JPanel pnMain = new JPanel();
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.PAGE_AXIS));
        JLabel lblTitle = new JLabel("Sửa Sản Phẩm", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnMain.add(Box.createRigidArea(new Dimension(0, 8)));
        pnMain.add(lblTitle);
        pnMain.add(Box.createRigidArea(new Dimension(0, 6)));
        pnMain.add(pnInfo);
        pnMain.add(pnVInput);
        pnMain.add(pnVariants);
        pnMain.add(pnActions);

        tblVariants.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) loadVariantRow();
        });

        btnAddVariant.addActionListener(this);
        btnUpdateVariant.addActionListener(this);
        btnRemoveVariant.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnReset.addActionListener(this);
        btnBack.addActionListener(this);

        JScrollPane mainScroll = new JScrollPane(pnMain);
        this.setContentPane(mainScroll);
        this.setSize(780, 620);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void initForm() {
        if (product == null) return;
        txtId.setText(String.valueOf(product.getId()));
        txtSku.setText(product.getSku());
        txtName.setText(product.getName());
        txtBrand.setText(product.getBrand());
        txtDescription.setText(product.getDescription());
        txtImages.setText(product.getImages());

        setCombo(cmbTier,     product.getTier());
        setCombo(cmbStudType, product.getStudType());
        setCombo(cmbStatus,   product.getStatus());

        if (product.getCategory() != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == product.getCategory().getId()) {
                    cmbCategory.setSelectedIndex(i); break;
                }
            }
        }
        refreshVariantTable();
    }

    private void setCombo(JComboBox<String> cmb, String value) {
        if (value == null) return;
        for (int i = 0; i < cmb.getItemCount(); i++) {
            if (cmb.getItemAt(i).equalsIgnoreCase(value)) { cmb.setSelectedIndex(i); return; }
        }
    }

    private void refreshVariantTable() {
        String[] cols = {"ID", "Kích cỡ", "Màu sắc", "Giá", "Tồn kho", "Ngưỡng AT", "Ảnh"};
        String[][] data = new String[variants.size()][7];
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant v = variants.get(i);
            data[i][0] = String.valueOf(v.getId());
            data[i][1] = v.getSize();
            data[i][2] = v.getColor();
            data[i][3] = String.valueOf(v.getPrice());
            data[i][4] = String.valueOf(v.getStock());
            data[i][5] = String.valueOf(v.getSafetyThreshold());
            data[i][6] = v.getImage();
        }
        tblVariants.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });
    }

    private void loadVariantRow() {
        int row = tblVariants.getSelectedRow();
        if (row < 0 || row >= variants.size()) return;
        ProductVariant v = variants.get(row);
        txtVSize.setText(v.getSize());
        txtVColor.setText(v.getColor());
        txtVPrice.setText(String.valueOf(v.getPrice()));
        txtVStock.setText(String.valueOf(v.getStock()));
        txtVThreshold.setText(String.valueOf(v.getSafetyThreshold()));
        txtVImage.setText(v.getImage());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            new SearchProductFrm(admin, true).setVisible(true);
            this.dispose();
        } else if (e.getSource() == btnReset) {
            initForm();
        } else if (e.getSource() == btnUpdate) {
            updateProduct();
        } else if (e.getSource() == btnAddVariant) {
            addVariant();
        } else if (e.getSource() == btnUpdateVariant) {
            updateVariant();
        } else if (e.getSource() == btnRemoveVariant) {
            removeVariant();
        }
    }

    private void updateProduct() {
        product.setSku(txtSku.getText().trim());
        product.setName(txtName.getText().trim());
        product.setBrand(txtBrand.getText().trim());
        product.setDescription(txtDescription.getText().trim());
        product.setImages(txtImages.getText().trim());
        product.setTier((String) cmbTier.getSelectedItem());
        product.setStudType((String) cmbStudType.getSelectedItem());
        product.setStatus((String) cmbStatus.getSelectedItem());
        product.setCategory(categories.get(cmbCategory.getSelectedIndex()));

        ProductDAO pd = new ProductDAO();
        if (pd.updateProduct(product)) {
            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thất bại.");
        }
    }

    private void addVariant() {
        try {
            ProductVariant v = buildVariantFromInput();
            if (v == null) return;
            v.setProductId(product.getId());
            ProductDAO pd = new ProductDAO();
            if (pd.addVariant(v)) {
                variants.add(v);
                refreshVariantTable();
                clearVariantInput();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm phiên bản thất bại.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá, Tồn kho và Ngưỡng An Toàn phải là số.");
        }
    }

    private void updateVariant() {
        int row = tblVariants.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiên bản."); return; }
        try {
            ProductVariant v = variants.get(row);
            applyVariantInput(v);
            if (v == null) return;
            ProductDAO pd = new ProductDAO();
            if (pd.updateVariant(v)) {
                refreshVariantTable();
                JOptionPane.showMessageDialog(this, "Đã cập nhật phiên bản.");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật phiên bản thất bại.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá, Tồn kho và Ngưỡng An Toàn phải là số.");
        }
    }

    private void removeVariant() {
        int row = tblVariants.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một phiên bản."); return; }
        JOptionPane.showMessageDialog(this,
                "Để xóa hoàn toàn phiên bản, hãy đặt tồn kho bằng 0 hoặc vô hiệu hóa sản phẩm.\n" +
                        "Phiên bản đã được ẩn khỏi danh sách trong phiên làm việc này.");
        variants.remove(row);
        refreshVariantTable();
    }

    private ProductVariant buildVariantFromInput() {
        if (txtVSize.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập kích cỡ.");
            return null;
        }
        ProductVariant v = new ProductVariant();
        applyVariantInput(v);
        return v;
    }

    private void applyVariantInput(ProductVariant v) {
        v.setSize(txtVSize.getText().trim());
        v.setColor(txtVColor.getText().trim());
        v.setPrice(Float.parseFloat(txtVPrice.getText().trim()));
        v.setStock(Integer.parseInt(txtVStock.getText().trim()));
        v.setSafetyThreshold(Integer.parseInt(txtVThreshold.getText().trim()));
        v.setImage(txtVImage.getText().trim());
    }

    private void clearVariantInput() {
        for (JTextField f : new JTextField[]{txtVSize, txtVColor, txtVPrice,
                txtVStock, txtVThreshold, txtVImage})
            f.setText("");
    }
}