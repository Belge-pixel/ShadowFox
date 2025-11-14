import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InventorySystem extends JFrame {

    private JTextField txtId, txtName, txtQuantity, txtPrice;
    private DefaultTableModel model;
    private JTable table;

    private final Color FB_BLUE = new Color(66, 103, 178);
    private final Color FB_LIGHT = new Color(237, 240, 245);
    private final Color FB_WHITE = Color.white;

    public InventorySystem() {
        setTitle("Inventory Manager");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FB_BLUE);
        header.setPreferredSize(new Dimension(1000, 60));

        JLabel title = new JLabel("  Inventory Management System");
        title.setForeground(Color.white);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

 
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(FB_LIGHT);

        JPanel formCard = new JPanel();
        formCard.setBackground(FB_WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(25, 25, 25, 25)
        ));
        formCard.setLayout(new GridLayout(10, 1, 10, 10));

        JLabel formTitle = new JLabel("Item Details");
        formTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        formTitle.setForeground(FB_BLUE);

        txtId = buildTextField("Item ID");
        txtName = buildTextField("Item Name");
        txtQuantity = buildTextField("Quantity");
        txtPrice = buildTextField("Price");

        JButton btnAdd = buildButton("Add Item", new Color(24, 119, 242));
        JButton btnUpdate = buildButton("Update Item", new Color(0, 200, 83));
        JButton btnDelete = buildButton("Delete Item", new Color(225, 0, 0));
        JButton btnClear = buildButton("Clear Fields", FB_BLUE);

        formCard.add(formTitle);
        formCard.add(txtId);
        formCard.add(txtName);
        formCard.add(txtQuantity);
        formCard.add(txtPrice);
        formCard.add(btnAdd);
        formCard.add(btnUpdate);
        formCard.add(btnDelete);
        formCard.add(btnClear);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(FB_WHITE);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel tableTitle = new JLabel("Inventory List");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setForeground(FB_BLUE);

        model = new DefaultTableModel(new String[]{"Item ID", "Name", "Qty", "Price"}, 0);
        table = new JTable(model);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);

        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(scroll, BorderLayout.CENTER);

        mainPanel.add(formCard);
        mainPanel.add(tableCard);

        add(mainPanel, BorderLayout.CENTER);
  
        btnAdd.addActionListener(e -> addItem());
        btnUpdate.addActionListener(e -> updateItem());
        btnDelete.addActionListener(e -> deleteItem());
        btnClear.addActionListener(e -> clearFields());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadSelectedRow();
            }
        });
    }

    private JTextField buildTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setBorder(BorderFactory.createTitledBorder(placeholder));
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return field;
    }

    private JButton buildButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.white);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(120, 35));
        return btn;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(28);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(FB_BLUE);
        header.setForeground(Color.white);
    }


    private void addItem() {
        if (!validateFields()) return;

        model.addRow(new Object[]{
                txtId.getText(),
                txtName.getText(),
                txtQuantity.getText(),
                txtPrice.getText()
        });

        JOptionPane.showMessageDialog(this, "Item added!");
        clearFields();
    }

    private void updateItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select item to update!");
            return;
        }
        if (!validateFields()) return;

        model.setValueAt(txtId.getText(), row, 0);
        model.setValueAt(txtName.getText(), row, 1);
        model.setValueAt(txtQuantity.getText(), row, 2);
        model.setValueAt(txtPrice.getText(), row, 3);

        JOptionPane.showMessageDialog(this, "Updated!");
    }

    private void deleteItem() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select item to delete!");
            return;
        }

        model.removeRow(row);
        JOptionPane.showMessageDialog(this, "Deleted!");
        clearFields();
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtQuantity.setText("");
        txtPrice.setText("");
    }

    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        txtId.setText(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());
        txtQuantity.setText(model.getValueAt(row, 2).toString());
        txtPrice.setText(model.getValueAt(row, 3).toString());
    }

    private boolean validateFields() {
        return !(txtId.getText().isEmpty() ||
                txtName.getText().isEmpty() ||
                txtQuantity.getText().isEmpty() ||
                txtPrice.getText().isEmpty());
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InventorySystem().setVisible(true));
    }
}
