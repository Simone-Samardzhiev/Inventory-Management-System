import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

// Class used to hold the attributes of an item.
class Item {
    private String name;
    private double price;
    private int quantity;
    private final int id;

    public Item(String name, double price, int quantity, int id) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.id = id;
    }

    // Getters and setters.
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }
}

// Class used to interact with the mysql server.
class Data implements Iterable<Item> {
    // Credentials to connect to the server.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/InventoryManagementSystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Simone2006";
    // Variables that will hold user id and the user items.
    private final int userId;
    private final ArrayList<Item> items = new ArrayList<>();

    // Method that is used to log in the user to the server. It will return the user id if the user is found otherwise it will return the -1.
    public static int login(String email, String password) {
        int id = -1;
        // Connecting to the server.
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement preparedStatement = connection.prepareStatement("SELECT id FROM Users WHERE email = ? AND password = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt(1);
                }
            }

            return id;
        } catch (SQLException e) {
            System.err.println("There was an error in the login method in class data " + e);
            System.exit(1);
        }
        return id;
    }

    // Method that is used to register the user. It returns true if registrations is successful and if and false if the email is already in the database.
    public static boolean register(String email, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement countUserStatement = connection.prepareStatement("SELECT COUNT(id) FROM Users WHERE email = ?"); PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO Users(email, password) VALUES (?,?)")) {
            countUserStatement.setString(1, email);

            try (ResultSet resultSet = countUserStatement.executeQuery()) {
                resultSet.next();
                if (resultSet.getInt(1) == 0) {
                    insertStatement.setString(1, email);
                    insertStatement.setString(2, password);
                    insertStatement.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("There was an error in the register method in class data " + e);
            System.exit(1);
        }
        return false;

    }

    // Constructor that takes the user id.
    public Data(int userId) {
        this.userId = userId;
        retrieveData();
    }

    // Method that is used to retrieve add the items of the user.
    private void retrieveData() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement preparedStatement = connection.prepareStatement("SELECT id,name,price,quantity FROM Items WHERE userId = ?")) {
            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    double price = resultSet.getDouble(3);
                    int quantity = resultSet.getInt(4);
                    items.add(new Item(name, price, quantity, id));
                }
            }
        } catch (SQLException e) {
            System.err.println("There was an error in retrieve data method in class data " + e);
            System.exit(1);
        }
    }

    // Method that is used to save the data when the app is closed.
    public void saveData() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Items SET name = ? , price = ? , quantity = ? WHERE id = ?")) {
            for (Item item : items) {
                preparedStatement.setString(1, item.getName());
                preparedStatement.setDouble(2, item.getPrice());
                preparedStatement.setInt(3, item.getQuantity());
                preparedStatement.setInt(4, item.getQuantity());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("There was an error in save data method in class data " + e);
            System.exit(1);
        }
    }

    // Method that is used to add item to the server.
    public void addItem(String name, double price, int quantity) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Items(name, price, quantity, userId) VALUES (?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setDouble(2, price);
            preparedStatement.setInt(3, quantity);
            preparedStatement.setInt(4, userId);
            preparedStatement.executeUpdate();

            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    items.add(new Item(name, price, quantity, id));
                }
            }
        } catch (SQLException e) {
            System.err.println("There was and error in add item method in class dara " + e);
            System.exit(1);
        }
    }

    // Method that is used to delete item form the server.
    public void deleteItem(int itemId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Items WHERE id = ?")) {
            preparedStatement.setInt(1, itemId);
            preparedStatement.executeUpdate();

            for (Item item : items) {
                if (item.getId() == itemId) {
                    items.remove(item);
                    break;
                }
            }
        } catch (SQLException sqlException) {
            System.err.println("There was an error in delete item method in class data");
            System.exit(1);
        }
    }

    // Method that will return the most expensive item. If the items are empty it will return null.
    public Item getMostExpensiveItem() {
        double price = 0;
        Item result;

        if (items.isEmpty()) {
            return null;
        } else {
            result = items.getFirst();
            for (Item item : items) {
                if (price <= item.getPrice()) {
                    price = item.getPrice();
                    result = item;
                }
            }
        }
        return result;
    }

    // Method that will return the item with most quantity. It will return null if the items are empty.
    public Item getItemWithMostQuantity() {
        int quantity = 0;
        Item result;

        if (items.isEmpty()) {
            return null;
        } else {
            result = items.getFirst();
            for (Item item : items) {
                if (quantity <= item.getQuantity()) {
                    quantity = item.getQuantity();
                    result = item;
                }
            }
        }
        return result;
    }


    // Overridden method that is used to iterate over the items
    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }
}

class AddItemWindow extends JFrame {
    private final Data data;
    private final MainWindow mainWindow;

    // Variables that will hold the JTextFields for the information about the item.
    private final JTextField nameTextField = new JTextField();
    private final JTextField priceTextField = new JTextField();
    private final JTextField quantityPriceField = new JTextField();

    public AddItemWindow(Data data, MainWindow mainWindow) {
        super();

        // Setting attributes to the window.
        this.setTitle("Add item");
        this.setSize(500, 500);
        this.setLayout(new GridBagLayout());

        // Passing the arguments.
        this.data = data;
        this.mainWindow = mainWindow;

        // Creating the UI.
        createUI();

        // Showing the window.
        this.setVisible(true);
    }

    private void createUI() {
        // Creating the widgets.
        GridBagConstraints layout = new GridBagConstraints();
        JButton addButton = new JButton("Add item");

        // Setting attributes to the widgets.
        nameTextField.setPreferredSize(new Dimension(200, 25));
        priceTextField.setPreferredSize(new Dimension(200, 25));
        quantityPriceField.setPreferredSize(new Dimension(200, 25));

        // Connecting the widgets.
        addButton.addActionListener(e -> AddItemWindow.this.onAddButtonClicked());

        // Adding the widgets.
        //noinspection DuplicatedCode
        layout.gridx = 0;
        layout.gridy = 0;
        this.add(new JLabel("Name:"), layout);

        layout.gridx = 1;
        this.add(nameTextField, layout);

        layout.gridx = 0;
        layout.gridy = 1;
        this.add(new JLabel("Price:"), layout);

        layout.gridx = 1;
        this.add(priceTextField, layout);

        layout.gridx = 0;
        layout.gridy = 2;
        this.add(new JLabel("Quantity:"), layout);

        layout.gridx = 1;
        this.add(quantityPriceField, layout);

        layout.gridx = 0;
        layout.gridy = 3;
        this.add(addButton, layout);
    }

    private void onAddButtonClicked() {
        //noinspection DuplicatedCode
        String name;
        double price;
        int quantity;

        name = nameTextField.getText();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The name can't be empty");
            return;
        }

        try {
            price = Double.parseDouble(priceTextField.getText());
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "The price can't be less that 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price");
            return;
        }

        try {
            quantity = Integer.parseInt(quantityPriceField.getText());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "The quantity can't be less than 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity");
            return;
        }

        data.addItem(name, price, quantity);
        mainWindow.onSearch();

        this.dispose();
    }
}

// Class that is used to create the window that will show information about a result.
class ItemInfoWindow extends JFrame {
    // Variable that will hold the information about the item.
    private final Item item;
    // Variable that will hold the data object to interact with the data.
    private final Data data;
    // Variable that will hold the main window, so when the item is changed the UI of the main window will be updated.
    private final MainWindow mainWindow;

    // Variables that will hold the JTextFields for the information about the item.
    private final JTextField nameTextField = new JTextField();
    private final JTextField priceTextField = new JTextField();
    private final JTextField quantityPriceField = new JTextField();


    public ItemInfoWindow(Item item, Data data, MainWindow mainWindow) {
        super();

        // Setting attributes to the window.
        this.setTitle("Item Info");
        this.setSize(400, 400);
        this.setLayout(new GridBagLayout());

        // Passing the arguments.
        this.item = item;
        this.data = data;
        this.mainWindow = mainWindow;

        // Creating the UI.
        createUI();

        // Showing the window.
        this.setVisible(true);
    }

    private void createUI() {
        // Creating the widgets.
        GridBagConstraints layout = new GridBagConstraints();
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete");

        // Setting attributes to the widgets.
        nameTextField.setPreferredSize(new Dimension(200, 25));
        nameTextField.setText(item.getName());
        priceTextField.setPreferredSize(new Dimension(200, 25));
        priceTextField.setText(Double.toString(item.getPrice()));
        quantityPriceField.setPreferredSize(new Dimension(200, 25));
        quantityPriceField.setText(Integer.toString(item.getQuantity()));

        // Connecting the widgets.
        saveButton.addActionListener(e -> ItemInfoWindow.this.onSaveButtonClicked());
        deleteButton.addActionListener(e -> ItemInfoWindow.this.onDeleteButtonClicked());

        // Adding the widgets.
        //noinspection DuplicatedCode
        layout.gridx = 0;
        layout.gridy = 0;
        this.add(new JLabel("Name:"), layout);

        layout.gridx = 1;
        this.add(nameTextField, layout);

        layout.gridx = 0;
        layout.gridy = 1;
        this.add(new JLabel("Price:"), layout);

        layout.gridx = 1;
        this.add(priceTextField, layout);

        layout.gridx = 0;
        layout.gridy = 2;
        this.add(new JLabel("Quantity:"), layout);

        layout.gridx = 1;
        this.add(quantityPriceField, layout);

        layout.gridx = 0;
        layout.gridy = 3;
        this.add(saveButton, layout);

        layout.gridy = 4;
        this.add(deleteButton, layout);
    }

    // Method that will be called the user click save button. It will save the changes,update the main window UI and tell the user if there are any mistakes in the give input.
    private void onSaveButtonClicked() {
        //noinspection DuplicatedCode
        String name;
        double price;
        int quantity;

        name = nameTextField.getText();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The name can't be empty");
            return;
        }

        try {
            price = Double.parseDouble(priceTextField.getText());
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "The price can't be less that 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price");
            return;
        }

        try {
            quantity = Integer.parseInt(quantityPriceField.getText());
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "The quantity can't be less than 0");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity");
            return;
        }

        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        mainWindow.onSearch();

        this.dispose();
    }

    // Method that will be called when the user clicks delete button. It will delete the item and refresh the UI of the main Window.
    private void onDeleteButtonClicked() {
        data.deleteItem(item.getId());
        mainWindow.onSearch();

        this.dispose();
    }
}

// Class that is used to create the main window.
class MainWindow extends JFrame {
    // Variable that is used to interact with the database.
    private final Data data;
    // Variable that will hold the login window.
    private final LoginWindow loginWindow;
    // JTextField used to search in the items.
    private final JTextField searchBar = new JTextField();
    // JPanel that will hold the results.
    private final JPanel resultsPanel = new JPanel();


    public MainWindow(LoginWindow loginWindow, int userId) {
        super();

        // Setting attributes to the window.
        this.setTitle("Inventory management system");
        this.setSize(500, 500);
        this.setLayout(new GridBagLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                data.saveData();
                MainWindow.this.loginWindow.mainWindowClosed();
            }
        });

        // Using the arguments.
        data = new Data(userId);
        this.loginWindow = loginWindow;

        // Creating the UI.
        createUI();
        createMenu();

        // Showing the window
        this.setVisible(true);
    }

    // Method that is used to create the UI of the window.
    private void createUI() {
        // Creating the widgets.
        GridBagConstraints layout = new GridBagConstraints();
        JScrollPane scrollPane = new JScrollPane(resultsPanel);

        // Setting attributes to the widgets.
        searchBar.setPreferredSize(new Dimension(400, 25));
        resultsPanel.setLayout(new GridBagLayout());
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Connecting the widgets.
        searchBar.addActionListener(e -> MainWindow.this.onSearch());

        // Adding the widgets.
        layout.gridx = 0;
        layout.gridy = 0;
        this.add(searchBar, layout);

        layout.gridy = 1;
        this.add(scrollPane, layout);
    }

    // Method that is used to create the UI of the window.
    public void onSearch() {
        resultsPanel.removeAll();

        String text = searchBar.getText().strip().toLowerCase();
        GridBagConstraints layout = new GridBagConstraints();
        layout.weightx = 1;
        layout.fill = GridBagConstraints.HORIZONTAL;
        layout.gridx = 0;
        layout.gridy = 0;

        for (Item item : data) {
            if (item.getName().contains(text)) {
                JButton result = new JButton(item.getName());
                result.addActionListener(e -> MainWindow.this.onResultClicked(item));
                resultsPanel.add(result, layout);
                layout.gridy++;
            }
        }

        resultsPanel.repaint();
        resultsPanel.revalidate();
    }

    // Method that will be activated when a result is clicked. It will show a new window with information about the item.
    private void onResultClicked(Item item) {
        new ItemInfoWindow(item, data, this);
    }

    // Method that is used to create the manu of the window.
    private void createMenu() {
        // Creating the widgets.
        JMenuBar menuBar = new JMenuBar();
        JMenu itemMenu = new JMenu("Item");
        JMenuItem addItem = new JMenuItem("Add item");
        JMenu inventoryMenu = new JMenu("Inventory");
        JMenuItem getTotalValue = new JMenuItem("Get total value");
        JMenuItem getMostExpensiveItem = new JMenuItem("Get most expensive item");
        JMenuItem getTheItemWithMostQuantity = new JMenuItem("Get the item with most quantity");

        // Connecting the menu items.
        addItem.addActionListener(e -> MainWindow.this.onAddItemClicked());
        getTotalValue.addActionListener(e -> MainWindow.this.onGetTotalValueClicked());
        getMostExpensiveItem.addActionListener(e -> MainWindow.this.onGetMostExpensiveItemClicked());
        getTheItemWithMostQuantity.addActionListener(e -> MainWindow.this.getItemWithMostQuantity());

        // Adding the widgets.
        itemMenu.add(addItem);
        inventoryMenu.add(getTotalValue);
        inventoryMenu.add(getMostExpensiveItem);
        inventoryMenu.add(getTheItemWithMostQuantity);
        menuBar.add(itemMenu);
        menuBar.add(inventoryMenu);

        this.setJMenuBar(menuBar);
    }

    private void onAddItemClicked() {
        new AddItemWindow(data, this);
    }

    // Method that is called when the user clicks the show total value menu option. It will display the total value of all items.
    private void onGetTotalValueClicked() {
        double totalValue = 0;

        for (Item item : data) {
            totalValue += item.getPrice();
        }

        JOptionPane.showMessageDialog(this, String.format("The total value :%s", totalValue));
    }

    // Method that will be called when the user clicks show most expensive item. It will display the mots expensive item and say if there aren't any item.
    private void onGetMostExpensiveItemClicked() {
        Item item = data.getMostExpensiveItem();

        if (item != null) {
            JOptionPane.showMessageDialog(this, String.format("The most expensive item :%s", item.getName()));
        } else {
            JOptionPane.showMessageDialog(this, "There aren't any items");
        }
    }

    // Method that will be called when the user clicks show item with most quantity. It will display the item and say if there aren't any items.
    private void getItemWithMostQuantity() {
        Item item = data.getItemWithMostQuantity();

        if (item != null) {
            JOptionPane.showMessageDialog(this, String.format("The item with most quantity :%s", item.getName()));
        } else {
            JOptionPane.showMessageDialog(this, "There aren't any items");
        }
    }

}

// Class that is used to create the login window.
class LoginWindow extends JFrame {
    // JTextFields used for getting the email and password.
    private final JTextField emailTextField = new JTextField();
    private final JPasswordField passwordTextField = new JPasswordField();

    public LoginWindow() {
        super();

        // Setting attributes to the window
        this.setTitle("Inventory management system");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());

        // Creating the UI
        createUI();
    }

    // Method that is used to create the UI of the window.¬
    private void createUI() {
        // Creating the widgets.
        GridBagConstraints layout = new GridBagConstraints();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        // Setting attributes to the widgets.
        emailTextField.setPreferredSize(new Dimension(200, 25));
        passwordTextField.setPreferredSize(new Dimension(200, 25));
        passwordTextField.setEchoChar('*');
        loginButton.addActionListener(e -> LoginWindow.this.onLoginButtonClicked());
        registerButton.addActionListener(e -> LoginWindow.this.onRegisterButtonClicked());

        // Adding the widgets.
        layout.gridy = 0;
        layout.gridx = 0;
        this.add(new JLabel("Email:"), layout);

        layout.gridx = 1;
        this.add(emailTextField, layout);

        layout.gridx = 0;
        layout.gridy = 1;
        this.add(new JLabel("Password:"), layout);

        layout.gridx = 1;
        this.add(passwordTextField, layout);

        layout.gridx = 0;
        layout.gridy = 2;
        this.add(loginButton, layout);

        layout.gridx = 1;
        this.add(registerButton, layout);
    }

    // Method that is used when the user clicks login button. It will log the user. If the credentials are wrong it will tell the user to try again.
    private void onLoginButtonClicked() {
        String email = emailTextField.getText();
        String password = new String(passwordTextField.getPassword());
        int userId = Data.login(email, password);

        if (userId != 1) {
            JOptionPane.showMessageDialog(this, "The password or the email is wrong.");
        } else {
            new MainWindow(this, userId);
            this.setVisible(false);
        }
    }

    // Method that is used when the user clicks register button .It will register the user and if the email is already used for an account it will tell the user.
    private void onRegisterButtonClicked() {
        String email = emailTextField.getText();
        String password = new String(passwordTextField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The email or the password can't be empty");
            return;
        }

        if (Data.register(email, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. The email is already in use.");
        }
    }

    // Method that will be called when the main window is closed.
    public void mainWindowClosed() {
        emailTextField.setText("");
        passwordTextField.setText("");
        this.setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MyApp");
        LoginWindow loginWindow = new LoginWindow();
        loginWindow.setVisible(true);
    }
}