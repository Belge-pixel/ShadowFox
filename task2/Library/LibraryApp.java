import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.List;

public class LibraryApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                DB.init();
                new LoginFrame();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fatal error: " + e.getMessage());
            }
        });
    }

    static class DB {
        private static final String DB_URL = "jdbc:sqlite:library.db";

        static void init() throws SQLException {
            try (Connection conn = connect()) {
                String usersSQL = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password TEXT NOT NULL);";
                String booksSQL = "CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, author TEXT, genre TEXT, isbn TEXT, description TEXT);";
                String ratingsSQL = "CREATE TABLE IF NOT EXISTS ratings (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, book_id INTEGER NOT NULL, rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5), UNIQUE(user_id, book_id), FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(book_id) REFERENCES books(id));";

                try (Statement st = conn.createStatement()) {
                    st.execute(usersSQL);
                    st.execute(booksSQL);
                    st.execute(ratingsSQL);
                }
            }
        }

        static Connection connect() throws SQLException { return DriverManager.getConnection(DB_URL); }

        static boolean registerUser(String username, String password) throws SQLException {
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
                p.setString(1, username);
                p.setString(2, password);
                p.executeUpdate();
                return true;
            } catch (SQLException ex) {
                return ex.getMessage().contains("UNIQUE") ? false : throwEx(ex);
            }
        }

        static Optional<User> login(String username, String password) throws SQLException {
            String sql = "SELECT id, username FROM users WHERE username = ? AND password = ?";
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
                p.setString(1, username); p.setString(2, password);
                try (ResultSet rs = p.executeQuery()) {
                    return rs.next() ? Optional.of(new User(rs.getInt("id"), rs.getString("username"))) : Optional.empty();
                }
            }
        }

        static int addBook(Book book) throws SQLException {
            String sql = "INSERT INTO books (title, author, genre, isbn, description) VALUES (?, ?, ?, ?, ?)";
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, book.title); p.setString(2, book.author); p.setString(3, book.genre);
                p.setString(4, book.isbn); p.setString(5, book.description);
                p.executeUpdate();
                try (ResultSet gk = p.getGeneratedKeys()) { return gk.next() ? gk.getInt(1) : -1; }
            }
        }

        static void updateBook(Book book) throws SQLException {
            String sql = "UPDATE books SET title=?, author=?, genre=?, isbn=?, description=? WHERE id=?";
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) {
                p.setString(1, book.title); p.setString(2, book.author); p.setString(3, book.genre);
                p.setString(4, book.isbn); p.setString(5, book.description); p.setInt(6, book.id); p.executeUpdate();
            }
        }

        static void deleteBook(int bookId) throws SQLException {
            try (Connection c = connect()) {
                try (PreparedStatement pr = c.prepareStatement("DELETE FROM ratings WHERE book_id = ?")) { pr.setInt(1, bookId); pr.executeUpdate(); }
                try (PreparedStatement pb = c.prepareStatement("DELETE FROM books WHERE id = ?")) { pb.setInt(1, bookId); pb.executeUpdate(); }
            }
        }

        static List<Book> listBooks() throws SQLException {
            String sql = "SELECT id, title, author, genre, isbn, description FROM books ORDER BY title";
            List<Book> list = new ArrayList<>();
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql); ResultSet rs = p.executeQuery()) {
                while (rs.next()) list.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("genre"), rs.getString("isbn"), rs.getString("description")));
            }
            return list;
        }

        static void setRating(int userId, int bookId, int rating) throws SQLException {
            String sql = "INSERT INTO ratings (user_id, book_id, rating) VALUES (?, ?, ?) ON CONFLICT(user_id, book_id) DO UPDATE SET rating = excluded.rating;";
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) { p.setInt(1, userId); p.setInt(2, bookId); p.setInt(3, rating); p.executeUpdate(); }
        }

        static Map<Integer, Integer> getUserRatings(int userId) throws SQLException {
            String sql = "SELECT book_id, rating FROM ratings WHERE user_id = ?";
            Map<Integer, Integer> m = new HashMap<>();
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql)) { p.setInt(1, userId); try (ResultSet rs = p.executeQuery()) { while (rs.next()) m.put(rs.getInt("book_id"), rs.getInt("rating")); } }
            return m;
        }

        static Map<Integer, Double> getAverageRatings() throws SQLException {
            String sql = "SELECT book_id, AVG(rating) as avgRating FROM ratings GROUP BY book_id";
            Map<Integer, Double> m = new HashMap<>();
            try (Connection c = connect(); PreparedStatement p = c.prepareStatement(sql); ResultSet rs = p.executeQuery()) { while (rs.next()) m.put(rs.getInt("book_id"), rs.getDouble("avgRating")); }
            return m;
        }

        private static boolean throwEx(SQLException ex) throws SQLException { throw ex; }
    }


    static class User { int id; String username; User(int id, String username){ this.id=id; this.username=username;} public String toString(){return username;} }
    static class Book { int id; String title, author, genre, isbn, description; Book(int id,String title,String author,String genre,String isbn,String description){this.id=id;this.title=title;this.author=author;this.genre=genre;this.isbn=isbn;this.description=description;} }

    static class ExternalBookAPI {
        static Map<String,String> fetchByISBN(String isbn) throws Exception {
            Map<String,String> out = new HashMap<>();
            String urlStr = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(4000);
            conn.setReadTimeout(6000);
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) return out;

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append("\n");
            br.close();

            String rsp = sb.toString();
            if (!rsp.contains("\"items\"")) return out;
  
            int tPos = rsp.indexOf("\"title\"");
            if (tPos >= 0) {
                int start = rsp.indexOf("\"", tPos + 7) + 1;
                int end = rsp.indexOf("\"", start);
                if (start > 0 && end > start) out.put("title", rsp.substring(start, end));
            }

            int aPos = rsp.indexOf("\"authors\"");
            if (aPos >= 0) {
                int start = rsp.indexOf("\"", aPos + 9) + 1;
                int end = rsp.indexOf("\"", start);
                if (start > 0 && end > start) out.put("authors", rsp.substring(start, end));
            }

            int dPos = rsp.indexOf("\"description\"");
            if (dPos >= 0) {
                int start = rsp.indexOf("\"", dPos + 13) + 1;
                int end = rsp.indexOf("\"", start);
                if (start > 0 && end > start) out.put("description", rsp.substring(start, end));
            }

            return out;
        }
    }

    static class LoginFrame extends JFrame {
        JTextField txtUser; JPasswordField txtPass;

        LoginFrame() {
            setTitle("Library System — Login");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(420, 280);
            setLocationRelativeTo(null);
            setUndecorated(true);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(new Color(18, 18, 18));
            root.setBorder(new EmptyBorder(20, 20, 20, 20));
            setContentPane(root);

            JLabel lblTitle = new JLabel("📚 Library Management", SwingConstants.CENTER);
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
            lblTitle.setForeground(Color.WHITE);
            root.add(lblTitle, BorderLayout.NORTH);

            JPanel form = new JPanel(new GridLayout(4, 1, 10, 10));
            form.setBackground(root.getBackground());

            txtUser = new JTextField(); txtPass = new JPasswordField();
            form.add(labeled("Username", txtUser));
            form.add(labeled("Password", txtPass));

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            btns.setBackground(root.getBackground());
            JButton btnLogin = styleButton("Login");
            JButton btnRegister = styleButton("Register");
            btns.add(btnLogin); btns.add(btnRegister);
            form.add(btns);

            root.add(form, BorderLayout.CENTER);

            btnLogin.addActionListener(e -> handleLogin());
            btnRegister.addActionListener(e -> handleRegister());

            setVisible(true);
        }

        JPanel labeled(String label, JComponent comp) {
            JPanel row = new JPanel(new BorderLayout(6,6));
            row.setBackground(new Color(18,18,18));
            JLabel l = new JLabel(label); l.setForeground(Color.WHITE);
            row.add(l, BorderLayout.WEST); row.add(comp, BorderLayout.CENTER);
            comp.setBackground(Color.DARK_GRAY); comp.setForeground(Color.WHITE);
            return row;
        }

        JButton styleButton(String text) {
            JButton btn = new JButton(text);
            btn.setBackground(new Color(229, 9, 20));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("SansSerif", Font.BOLD, 16));
            btn.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));
            return btn;
        }

        void handleLogin() {
            String u = txtUser.getText().trim(); String p = new String(txtPass.getPassword());
            if(u.isEmpty()||p.isEmpty()){JOptionPane.showMessageDialog(this,"Enter username & password");return;}
            try {Optional<User> opt = DB.login(u,p); if(opt.isPresent()){new MainFrame(opt.get()); dispose();}else{JOptionPane.showMessageDialog(this,"Invalid credentials");}}
            catch(SQLException ex){ex.printStackTrace();JOptionPane.showMessageDialog(this,"DB error: "+ex.getMessage());}
        }

        void handleRegister() {
            String u = txtUser.getText().trim(); String p = new String(txtPass.getPassword());
            if(u.isEmpty()||p.isEmpty()){JOptionPane.showMessageDialog(this,"Enter username & password");return;}
            try {boolean ok = DB.registerUser(u,p); JOptionPane.showMessageDialog(this, ok?"Registered! You can log in now.":"Username already exists.");}
            catch(SQLException ex){ex.printStackTrace();JOptionPane.showMessageDialog(this,"DB error: "+ex.getMessage());}
        }
    }


    static class MainFrame extends JFrame {
        private final User currentUser;
        private final JPanel cardPanel;
        private final JScrollPane scrollPane;

        MainFrame(User user) {
            this.currentUser = user;
            setTitle("Library — " + user.username);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(1200, 700);
            setLocationRelativeTo(null);

            JPanel root = new JPanel(new BorderLayout(10,10));
            root.setBackground(new Color(18,18,18));
            root.setBorder(new EmptyBorder(10,10,10,10));
            setContentPane(root);

            JPanel top = new JPanel(new BorderLayout());
            top.setBackground(root.getBackground());
            JLabel lblWelcome = new JLabel("Welcome, " + user.username);
            lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 22));
            lblWelcome.setForeground(Color.WHITE);
            JButton btnLogout = new JButton("Logout");
            btnLogout.setBackground(new Color(229,9,20));
            btnLogout.setForeground(Color.WHITE);
            btnLogout.setFocusPainted(false);
            btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
            btnLogout.setBorder(BorderFactory.createEmptyBorder(8,15,8,15));
            btnLogout.addActionListener(e->{dispose(); new LoginFrame();});
            top.add(lblWelcome, BorderLayout.WEST); top.add(btnLogout, BorderLayout.EAST);
            root.add(top, BorderLayout.NORTH);

            cardPanel = new JPanel();
            cardPanel.setBackground(root.getBackground());
            cardPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

            scrollPane = new JScrollPane(cardPanel);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.setBackground(root.getBackground());
            root.add(scrollPane, BorderLayout.CENTER);

            loadBooks();
            setVisible(true);
        }

        void loadBooks() {
            cardPanel.removeAll();
            try {
                List<Book> books = DB.listBooks();
                Map<Integer, Double> avgRatings = DB.getAverageRatings();
                for(Book b: books) {
                    JPanel card = createBookCard(b, avgRatings.getOrDefault(b.id, 0.0));
                    cardPanel.add(card);
                }
            } catch(SQLException e) { e.printStackTrace(); }
            cardPanel.revalidate();
            cardPanel.repaint();
        }

        JPanel createBookCard(Book b, double avg) {
            JPanel card = new JPanel();
            card.setPreferredSize(new Dimension(200, 280));
            card.setBackground(new Color(25,25,25));
            card.setBorder(BorderFactory.createLineBorder(new Color(60,60,60), 2));
            card.setLayout(new BorderLayout());

            JLabel lblTitle = new JLabel("<html><b>" + b.title + "</b></html>", SwingConstants.CENTER);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

            JTextArea desc = new JTextArea("Author: "+b.author+"\nGenre: "+b.genre+"\nRating: "+String.format("%.1f",avg));
            desc.setEditable(false);
            desc.setWrapStyleWord(true);
            desc.setLineWrap(true);
            desc.setBackground(card.getBackground());
            desc.setForeground(Color.LIGHT_GRAY);

            card.add(lblTitle, BorderLayout.NORTH);
            card.add(desc, BorderLayout.CENTER);

            card.addMouseListener(new java.awt.event.MouseAdapter(){
                public void mouseEntered(java.awt.event.MouseEvent evt){ card.setBorder(BorderFactory.createLineBorder(new Color(229,9,20), 3)); }
                public void mouseExited(java.awt.event.MouseEvent evt){ card.setBorder(BorderFactory.createLineBorder(new Color(60,60,60), 2)); }
            });

            return card;
        }
    }
}
