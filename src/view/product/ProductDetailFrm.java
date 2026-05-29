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
import model.Client;
import model.OrderLine;
import model.Product;
import model.ProductVariant;
import view.user.ClientHomeFrm;

public class ProductDetailFrm extends JFrame implements ActionListener {

    private final Client client;
    private final ArrayList<OrderLine> cart;
    private final ClientHomeFrm parentFrm;
    private Product product;
    private ArrayList<ProductVariant> variants;

    private JTable tblVariants;
    private JSpinner spnQty;
    private JButton btnAddToCart;
    private JButton btnBack;

    public ProductDetailFrm(Client client, Product partialProduct,
                            ArrayList<OrderLine> cart, ClientHomeFrm parentFrm) {
        super("Cửa Hàng Thể Thao – Chi Tiết Sản Phẩm");
        this.client = client;
        this.cart = cart;
        this.parentFrm = parentFrm;

        ProductDAO pd = new ProductDAO();
        this.product = pd.getProductById(partialProduct.getId());
        if (this.product == null) this.product = partialProduct;
        this.variants = (this.product.getVariants() != null)
                ? this.product.getVariants()
                : new ArrayList<>();

        buildUI();
    }

    private void buildUI() {
        tblVariants = new JTable();
        tblVariants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spnQty = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        btnAddToCart = new JButton("Thêm vào giỏ hàng");
        btnBack = new JButton("← Quay lại tìm kiếm");

        JPanel pnInfo = new JPanel(new GridLayout(0, 2, 6, 4));
        pnInfo.setBorder(BorderFactory.createTitledBorder("Thông Tin Sản Phẩm"));
        pnInfo.add(new JLabel("Tên:"));         pnInfo.add(new JLabel(product.getName()));
        pnInfo.add(new JLabel("Thương hiệu:"));        pnInfo.add(new JLabel(product.getBrand()));
        pnInfo.add(new JLabel("Danh mục:"));
        pnInfo.add(new JLabel(product.getCategory() != null ? product.getCategory().getName() : "—"));
        pnInfo.add(new JLabel("Phân khúc:"));         pnInfo.add(new JLabel(product.getTier()));
        pnInfo.add(new JLabel("Loại đinh:"));    pnInfo.add(new JLabel(product.getStudType()));
        pnInfo.add(new JLabel("Mô tả:"));
        pnInfo.add(new JLabel(product.getDescription() != null ? product.getDescription() : "—"));

        String[] cols = {"ID", "Kích cỡ", "Màu sắc", "Giá (VNĐ)", "Tồn kho"};
        String[][] data = new String[variants.size()][5];
        for (int i = 0; i < variants.size(); i++) {
            ProductVariant v = variants.get(i);
            data[i][0] = String.valueOf(v.getId());
            data[i][1] = v.getSize();
            data[i][2] = v.getColor();
            data[i][3] = String.format("%.0f", v.getPrice());
            data[i][4] = (v.getStock() > 0) ? String.valueOf(v.getStock()) : "Hết hàng";
        }
        tblVariants.setModel(new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        JScrollPane scrollV = new JScrollPane(tblVariants);
        scrollV.setPreferredSize(new Dimension(620, 160));

        JPanel pnVariants = new JPanel(new BorderLayout());
        pnVariants.setBorder(BorderFactory.createTitledBorder("Chọn một phiên bản"));
        pnVariants.add(scrollV, BorderLayout.CENTER);

        JPanel pnCart = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        pnCart.add(new JLabel("Số lượng:"));
        pnCart.add(spnQty);
        pnCart.add(btnAddToCart);

        JPanel pnTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnTop.add(btnBack);
        JLabel lblTitle = new JLabel("Chi Tiết Sản Phẩm", JLabel.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(20.0f));
        JPanel pnLbl = new JPanel(); pnLbl.add(lblTitle);

        JPanel pnNorth = new JPanel();
        pnNorth.setLayout(new BoxLayout(pnNorth, BoxLayout.PAGE_AXIS));
        pnNorth.add(pnTop);
        pnNorth.add(pnLbl);

        JPanel pnCenter = new JPanel();
        pnCenter.setLayout(new BoxLayout(pnCenter, BoxLayout.PAGE_AXIS));
        pnCenter.add(pnInfo);
        pnCenter.add(Box.createRigidArea(new Dimension(0, 8)));
        pnCenter.add(pnVariants);

        this.setLayout(new BorderLayout(0, 4));
        this.add(pnNorth,  BorderLayout.NORTH);
        this.add(pnCenter, BorderLayout.CENTER);
        this.add(pnCart,   BorderLayout.SOUTH);

        btnBack.addActionListener(this);
        btnAddToCart.addActionListener(this);

        this.setSize(680, 480);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
            this.dispose();
        } else if (e.getSource() == btnAddToCart) {
            addToCart();
        }
    }

    private void addToCart() {
        int row = tblVariants.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một kích cỡ/màu sắc."); return; }

        ProductVariant selected = variants.get(row);
        if (selected.getStock() <= 0) {
            JOptionPane.showMessageDialog(this, "Phiên bản này đã hết hàng.");
            return;
        }
        int qty = (Integer) spnQty.getValue();
        if (qty > selected.getStock()) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ còn " + selected.getStock() + " sản phẩm cho phiên bản này.");
            return;
        }

        for (OrderLine line : cart) {
            if (line.getVariant() != null && line.getVariant().getId() == selected.getId()) {
                int newQty = line.getQuantity() + qty;
                if (newQty > selected.getStock()) {
                    JOptionPane.showMessageDialog(this,
                            "Tổng số lượng sẽ vượt quá tồn kho (" + selected.getStock() + ").");
                    return;
                }
                line.setQuantity(newQty);
                line.setSubTotal(line.getPriceAtPurchase() * newQty);
                JOptionPane.showMessageDialog(this,
                        "Đã cập nhật giỏ hàng: " + product.getName() + " (" + selected.getSize() + " / "
                                + selected.getColor() + ") × " + newQty);
                return;
            }
        }

        OrderLine line = new OrderLine();
        line.setVariant(selected);
        line.setQuantity(qty);
        line.setPriceAtPurchase(selected.getPrice());
        line.setSubTotal(selected.getPrice() * qty);
        cart.add(line);

        JOptionPane.showMessageDialog(this,
                product.getName() + " (" + selected.getSize() + " / " + selected.getColor()
                        + ") × " + qty + " đã được thêm vào giỏ hàng.");
    }
}