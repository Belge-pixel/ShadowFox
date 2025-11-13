import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class Student extends JFrame {

    private JTextField txtId, txtName, txtAge, txtMajor;
    private JTable table;
    private DefaultTableModel model;
    private Color primaryColor = new Color(66, 103, 178);  // Facebook blue
    private Color lightGray = new Color(245, 246, 250);

    public Student() {
        setTitle("Student Information System");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(lightGray);

        JLabel title = new JLabel("Student Information System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(primaryColor);
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);


        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        mainPanel.setBackground(lightGray);
        add(mainPanel, BorderLayout.CENTER);
     
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel formTitle = new JLabel("Register Student");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        formTitle.setForeground(primaryColor);
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(formTitle);
        formPanel.add(Box.createVerticalStrut(20));

        txtId = createInputField(formPanel, "Student ID");
        txtName = createInputField(formPanel, "Full Name");
        txtAge = createInputField(formPanel, "Age");
        txtMajor = createInputField(formPanel, "Major");

        formPanel.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Add");
        JButton btnUpdate = createButton("Update");
        JButton btnDelete = createButton("Delete");
        JButton btnClear = createButton("Clear");

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        formPanel.add(btnPanel);
        mainPanel.add(formPanel, BorderLayout.WEST);

 
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        String[] columns = {"ID", "Name", "Age", "Major"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String age = txtAge.getText().trim();
            String major = txtMajor.getText().trim();

            if (id.isEmpty() || name.isEmpty() || age.isEmpty() || major.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            model.addRow(new Object[]{id, name, age, major});
            clearFields();
        });
      
        btnUpdate.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a record to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            model.setValueAt(txtId.getText(), row, 0);
            model.setValueAt(txtName.getText(), row, 1);
            model.setValueAt(txtAge.getText(), row, 2);
            model.setValueAt(txtMajor.getText(), row, 3);
            clearFields();
        });
   
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a record to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            model.removeRow(row);
            clearFields();
        });

    
        btnClear.addActionListener(e -> clearFields());

   
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtId.setText(model.getValueAt(row, 0).toString());
                txtName.setText(model.getValueAt(row, 1).toString());
                txtAge.setText(model.getValueAt(row, 2).toString());
                txtMajor.setText(model.getValueAt(row, 3).toString());
            }
        });
    }


    private JTextField createInputField(JPanel formPanel, String placeholder) {
        JLabel label = new JLabel(placeholder + ":");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(label);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(field);
        formPanel.add(Box.createVerticalStrut(15));
        return field;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(primaryColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 10, 10, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(57, 89, 160));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(primaryColor);
            }
        });
        return button;
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
        txtMajor.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Student().setVisible(true));
    }
}
