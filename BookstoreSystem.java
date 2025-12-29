
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class BookstoreSystem {

    // --- Book Class ---
    class Book {
        private String title;
        private String author;
        private double price;
        private int stock;

        public Book(String title, String author, double price, int stock) {
            this.title = title;
            this.author = author;
            this.price = price;
            this.stock = stock;
        }

        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }

        @Override
        public String toString() {
            return title + " by " + author + " - ₹" + price + " (" + stock + " in stock)";
        }
    }

    // --- Cart Class ---
    class Cart {
        private ArrayList<Book> cartItems = new ArrayList<>();

        public void addBook(Book book) {
            cartItems.add(book);
        }

        public ArrayList<Book> getItems() {
            return cartItems;
        }

        public double getTotal() {
            return cartItems.stream().mapToDouble(Book::getPrice).sum();
        }

        public void clear() {
            cartItems.clear();
        }
    }

    // --- User Class ---
    class User {
        private String name;
        private Cart cart;

        public User(String name) {
            this.name = name;
            this.cart = new Cart();
        }

        public String getName() { return name; }
        public Cart getCart() { return cart; }
    }

    // --- Inventory Class ---
    class Inventory {
        private ArrayList<Book> books = new ArrayList<>();

        public void addBook(Book book) {
            books.add(book);
        }

        public ArrayList<Book> getBooks() {
            return books;
        }

        public synchronized boolean purchaseBook(Book book) {
            if (book.getStock() > 0) {
                book.setStock(book.getStock() - 1);
                return true;
            }
            return false;
        }
    }

    // --- Welcome Screen ---
    class WelcomeScreen {
        private JFrame welcomeFrame;

        public WelcomeScreen(Inventory inventory, User user) {
            welcomeFrame = new JFrame("Welcome to ReadNest");
            welcomeFrame.setSize(500, 300);
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setLayout(new BorderLayout());
            welcomeFrame.getContentPane().setBackground(new Color(240, 248, 255)); // AliceBlue background

            // Main panel with border and padding
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            mainPanel.setBackground(new Color(240, 248, 255));

            // Logo/Title
            JLabel titleLabel = new JLabel("Welcome to ReadNest", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Georgia", Font.BOLD, 28));
            titleLabel.setForeground(new Color(70, 130, 180)); // SteelBlue
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Subtitle
            JLabel subtitleLabel = new JLabel("Your Online Bookstore", SwingConstants.CENTER);
            subtitleLabel.setFont(new Font("Georgia", Font.PLAIN, 16));
            subtitleLabel.setForeground(new Color(105, 105, 105)); // DimGray
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Image icon (using book emoji as placeholder)
            JLabel iconLabel = new JLabel("📚", SwingConstants.CENTER);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Button panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(240, 248, 255));
            JButton enterButton = new JButton("Enter Store");
            enterButton.setFont(new Font("Arial", Font.BOLD, 14));
            enterButton.setBackground(new Color(70, 130, 180)); // SteelBlue
            enterButton.setForeground(Color.WHITE);
            enterButton.setFocusPainted(false);
            enterButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
            enterButton.addActionListener(e -> {
                welcomeFrame.dispose();
                new BookStoreGUI(inventory, user);
            });

            buttonPanel.add(enterButton);

            // Add components to main panel
            mainPanel.add(Box.createVerticalGlue());
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            mainPanel.add(subtitleLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            mainPanel.add(iconLabel);
            mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
            mainPanel.add(buttonPanel);
            mainPanel.add(Box.createVerticalGlue());

            welcomeFrame.add(mainPanel, BorderLayout.CENTER);
            welcomeFrame.setLocationRelativeTo(null); // Center on screen
            welcomeFrame.setVisible(true);
        }
    }

    // --- BookStore GUI ---
    class BookStoreGUI {
        private JFrame frame;
        private Inventory inventory;
        private User user;
        private DefaultListModel<Book> model;

        public BookStoreGUI(Inventory inventory, User user) {
            this.inventory = inventory;
            this.user = user;
            initGUI();
        }

        public void initGUI() {
            frame = new JFrame("ReadNest - Online Bookstore");
            frame.setSize(600, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.getContentPane().setBackground(new Color(240, 248, 255));

            // Create header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(new Color(70, 130, 180));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            JLabel headerLabel = new JLabel("Available Books");
            headerLabel.setFont(new Font("Georgia", Font.BOLD, 18));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel, BorderLayout.WEST);

            JLabel userLabel = new JLabel("User: " + user.getName());
            userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            userLabel.setForeground(Color.WHITE);
            headerPanel.add(userLabel, BorderLayout.EAST);

            frame.add(headerPanel, BorderLayout.NORTH);

            // Create book list
            model = new DefaultListModel<>();
            for (Book book : inventory.getBooks()) {
                model.addElement(book);
            }
            JList<Book> bookList = new JList<>(model);
            bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            bookList.setCellRenderer(new BookCellRenderer());
            JScrollPane scrollPane = new JScrollPane(bookList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            frame.add(scrollPane, BorderLayout.CENTER);

            // Create button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
            buttonPanel.setBackground(new Color(240, 248, 255));

            JButton addButton = createStyledButton("Add to Cart", new Color(34, 139, 34)); // ForestGreen
            JButton viewCartButton = createStyledButton("View Cart", new Color(70, 130, 180)); // SteelBlue
            JButton checkoutButton = createStyledButton("Checkout", new Color(178, 34, 34)); // FireBrick

            addButton.addActionListener(e -> {
                Book selected = bookList.getSelectedValue();
                if (selected != null) {
                    if (inventory.purchaseBook(selected)) {
                        user.getCart().addBook(selected);
                        model.setElementAt(selected, bookList.getSelectedIndex());
                        showMessage("Book Added", selected.getTitle() + " has been added to your cart!", false);
                        bookList.repaint();
                    } else {
                        showMessage("Out of Stock", "Sorry, " + selected.getTitle() + " is out of stock!", true);
                    }
                } else {
                    showMessage("No Selection", "Please select a book first!", true);
                }
            });

            viewCartButton.addActionListener(e -> showCart());

            checkoutButton.addActionListener(e -> {
                if (user.getCart().getItems().isEmpty()) {
                    showMessage("Empty Cart", "Your cart is empty. Add some books first!", true);
                } else {
                    checkout();
                }
            });

            buttonPanel.add(addButton);
            buttonPanel.add(viewCartButton);
            buttonPanel.add(checkoutButton);
            frame.add(buttonPanel, BorderLayout.SOUTH);

            frame.setLocationRelativeTo(null); // Center on screen
            frame.setVisible(true);
        }

        private JButton createStyledButton(String text, Color bgColor) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            return button;
        }

        private void showMessage(String title, String message, boolean isError) {
            JOptionPane.showMessageDialog(frame, 
                "<html><div style='width:200px;'>" + message + "</div></html>", 
                title, 
                isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }

        private void showCart() {
            if (user.getCart().getItems().isEmpty()) {
                showMessage("Your Cart", "Your cart is currently empty.", false);
                return;
            }

            JDialog cartDialog = new JDialog(frame, "Your Shopping Cart", true);
            cartDialog.setSize(450, 400);
            cartDialog.setLayout(new BorderLayout());
            cartDialog.getContentPane().setBackground(new Color(240, 248, 255));

            // Create header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(70, 130, 180));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            JLabel headerLabel = new JLabel("Your Selected Books");
            headerLabel.setFont(new Font("Georgia", Font.BOLD, 16));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            cartDialog.add(headerPanel, BorderLayout.NORTH);

            // Create book list
            DefaultListModel<Book> cartModel = new DefaultListModel<>();
            for (Book book : user.getCart().getItems()) {
                cartModel.addElement(book);
            }
            JList<Book> cartList = new JList<>(cartModel);
            cartList.setCellRenderer(new BookCellRenderer());
            JScrollPane scrollPane = new JScrollPane(cartList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            cartDialog.add(scrollPane, BorderLayout.CENTER);

            // Create footer with total
            JPanel footerPanel = new JPanel(new BorderLayout());
            footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            footerPanel.setBackground(new Color(240, 248, 255));

            JLabel totalLabel = new JLabel("Total: ₹" + String.format("%.2f", user.getCart().getTotal()));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
            footerPanel.add(totalLabel, BorderLayout.CENTER);

            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> cartDialog.dispose());
            footerPanel.add(closeButton, BorderLayout.EAST);

            cartDialog.add(footerPanel, BorderLayout.SOUTH);
            cartDialog.setLocationRelativeTo(frame);
            cartDialog.setVisible(true);
        }

        private void checkout() {
            JDialog checkoutDialog = new JDialog(frame, "Checkout", true);
            checkoutDialog.setSize(450, 400);
            checkoutDialog.setLayout(new BorderLayout());
            checkoutDialog.getContentPane().setBackground(new Color(240, 248, 255));

            // Create header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(70, 130, 180));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            JLabel headerLabel = new JLabel("Order Summary");
            headerLabel.setFont(new Font("Georgia", Font.BOLD, 16));
            headerLabel.setForeground(Color.WHITE);
            headerPanel.add(headerLabel);
            checkoutDialog.add(headerPanel, BorderLayout.NORTH);

            // Create book list
            DefaultListModel<Book> checkoutModel = new DefaultListModel<>();
            for (Book book : user.getCart().getItems()) {
                checkoutModel.addElement(book);
            }
            JList<Book> checkoutList = new JList<>(checkoutModel);
            checkoutList.setCellRenderer(new BookCellRenderer());
            JScrollPane scrollPane = new JScrollPane(checkoutList);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            checkoutDialog.add(scrollPane, BorderLayout.CENTER);

            // Create footer with total and buttons
            JPanel footerPanel = new JPanel(new BorderLayout());
            footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            footerPanel.setBackground(new Color(240, 248, 255));

            JLabel totalLabel = new JLabel("Total: ₹" + String.format("%.2f", user.getCart().getTotal()));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
            footerPanel.add(totalLabel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(new Color(240, 248, 255));

            JButton cancelButton = createStyledButton("Cancel", new Color(105, 105, 105));
            cancelButton.addActionListener(e -> checkoutDialog.dispose());

            JButton confirmButton = createStyledButton("Confirm Purchase", new Color(34, 139, 34));
            confirmButton.addActionListener(e -> {
                user.getCart().clear();
                checkoutDialog.dispose();
                showMessage("Thank You", "Your purchase has been completed successfully!", false);
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(confirmButton);
            footerPanel.add(buttonPanel, BorderLayout.SOUTH);

            checkoutDialog.add(footerPanel, BorderLayout.SOUTH);
            checkoutDialog.setLocationRelativeTo(frame);
            checkoutDialog.setVisible(true);
        }

        // Custom cell renderer for book list
        class BookCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Book) {
                    Book book = (Book) value;
                    setText("<html><b>" + book.getTitle() + "</b> by " + book.getAuthor() + 
                            "<br>Price: ₹" + book.getPrice() + " | Stock: " + book.getStock() + "</html>");
                    if (book.getStock() == 0) {
                        setForeground(Color.GRAY);
                    } else {
                        setForeground(isSelected ? Color.WHITE : Color.BLACK);
                    }
                }
                if (isSelected) {
                    setBackground(new Color(70, 130, 180));
                } else {
                    setBackground(index % 2 == 0 ? Color.WHITE : new Color(240, 248, 255));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return this;
            }
        }
    }

    // --- Main Method ---
    public static void main(String[] args) {
        BookstoreSystem system = new BookstoreSystem();
        Inventory inventory = system.new Inventory();

        // Programming Books
        inventory.addBook(system.new Book("Java Basics", "James Gosling", 499.0, 5));
        inventory.addBook(system.new Book("Effective Java", "Joshua Bloch", 799.0, 3));
        inventory.addBook(system.new Book("Clean Code", "Robert C. Martin", 599.0, 2));
        inventory.addBook(system.new Book("Head First Java", "Kathy Sierra", 699.0, 4));
        inventory.addBook(system.new Book("Thinking in Java", "Bruce Eckel", 899.0, 3));
        inventory.addBook(system.new Book("Java Concurrency in Practice", "Brian Goetz", 999.0, 2));
        inventory.addBook(system.new Book("Spring in Action", "Craig Walls", 849.0, 3));
        inventory.addBook(system.new Book("Data Structures & Algorithms in Java", "Robert Lafore", 750.0, 4));
        inventory.addBook(system.new Book("Python Crash Course", "Eric Matthes", 649.0, 6));
        inventory.addBook(system.new Book("Fluent Python", "Luciano Ramalho", 899.0, 3));
        inventory.addBook(system.new Book("JavaScript: The Good Parts", "Douglas Crockford", 549.0, 5));
        inventory.addBook(system.new Book("Eloquent JavaScript", "Marijn Haverbeke", 699.0, 4));

        // Fiction Books
        inventory.addBook(system.new Book("The Alchemist", "Paulo Coelho", 399.0, 8));
        inventory.addBook(system.new Book("1984", "George Orwell", 349.0, 7));
        inventory.addBook(system.new Book("To Kill a Mockingbird", "Harper Lee", 449.0, 5));
        inventory.addBook(system.new Book("The Great Gatsby", "F. Scott Fitzgerald", 399.0, 6));
        inventory.addBook(system.new Book("Pride and Prejudice", "Jane Austen", 349.0, 7));
        inventory.addBook(system.new Book("The Hobbit", "J.R.R. Tolkien", 499.0, 4));
        inventory.addBook(system.new Book("Harry Potter and the Sorcerer's Stone", "J.K. Rowling", 599.0, 9));
        inventory.addBook(system.new Book("The Lord of the Rings", "J.R.R. Tolkien", 899.0, 3));

        // Non-Fiction Books
        inventory.addBook(system.new Book("Sapiens", "Yuval Noah Harari", 549.0, 6));
        inventory.addBook(system.new Book("Atomic Habits", "James Clear", 499.0, 7));
        inventory.addBook(system.new Book("The Psychology of Money", "Morgan Housel", 449.0, 5));
        inventory.addBook(system.new Book("Educated", "Tara Westover", 499.0, 4));
        inventory.addBook(system.new Book("Becoming", "Michelle Obama", 599.0, 5));
        inventory.addBook(system.new Book("The Subtle Art of Not Giving a F*ck", "Mark Manson", 449.0, 6));
        inventory.addBook(system.new Book("Thinking, Fast and Slow", "Daniel Kahneman", 649.0, 4));
        inventory.addBook(system.new Book("The 7 Habits of Highly Effective People", "Stephen Covey", 549.0, 5));

        // Business & Finance
        inventory.addBook(system.new Book("Rich Dad Poor Dad", "Robert Kiyosaki", 499.0, 6));
        inventory.addBook(system.new Book("The Intelligent Investor", "Benjamin Graham", 699.0, 4));
        inventory.addBook(system.new Book("Zero to One", "Peter Thiel", 549.0, 5));
        inventory.addBook(system.new Book("Good to Great", "Jim Collins", 599.0, 4));
        inventory.addBook(system.new Book("The Lean Startup", "Eric Ries", 499.0, 5));
        inventory.addBook(system.new Book("The $100 Startup", "Chris Guillebeau", 449.0, 6));

        // Science & Technology
        inventory.addBook(system.new Book("A Brief History of Time", "Stephen Hawking", 499.0, 5));
        inventory.addBook(system.new Book("Astrophysics for People in a Hurry", "Neil deGrasse Tyson", 449.0, 6));
        inventory.addBook(system.new Book("The Selfish Gene", "Richard Dawkins", 549.0, 4));
        inventory.addBook(system.new Book("Cosmos", "Carl Sagan", 599.0, 3));
        inventory.addBook(system.new Book("The Gene: An Intimate History", "Siddhartha Mukherjee", 649.0, 4));

        // Self-Help & Personal Development
        inventory.addBook(system.new Book("The Power of Now", "Eckhart Tolle", 449.0, 7));
        inventory.addBook(system.new Book("The 5 AM Club", "Robin Sharma", 499.0, 5));
        inventory.addBook(system.new Book("The Four Agreements", "Don Miguel Ruiz", 399.0, 8));
        inventory.addBook(system.new Book("Man's Search for Meaning", "Viktor Frankl", 449.0, 6));
        inventory.addBook(system.new Book("The Art of Happiness", "Dalai Lama", 499.0, 5));

        User user = system.new User("Guest");
        SwingUtilities.invokeLater(() -> system.new WelcomeScreen(inventory, user));
    }
}