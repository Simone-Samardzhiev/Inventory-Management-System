import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

class Item {
    private String name;
    private double price;
    private int quantity;
    private int id;

    public Item(String name, double price, int quantity, int id) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}

class Data implements Iterable<Item> {
    private final ArrayList<Item> items = new ArrayList<>();
    private String username;
    private String password;

    public Data() {
        getLoginInfo();
        retrieveData();
    }

    private void getLoginInfo() {
        try (FileReader reader = new FileReader("/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/java/login_info.json")) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            HashMap<String, String> map = gson.fromJson(reader, type);

            username = map.get("username");
            password = map.get("password");
        } catch (IOException e) {
            System.err.println("There was an error reading the login info !");
            System.exit(1);
        }
    }

    private void retrieveData() {
        String url = "jdbc:mysql://localhost:3306/InventoryManagementSystem";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String query = "SELECT * FROM JavaData";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                int quantity = resultSet.getInt("quantity");

                items.add(new Item(name, price, quantity, id));
            }

            connection.close();
            statement.close();
            resultSet.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("There was an error trying to read the data from the database !");
            System.exit(1);
        }
    }

    public void storeData() {
        String url = "jdbc:mysql://localhost:3306/InventoryManagementSystem";
        String username = "root";
        String password = "0646307000";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);

            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM JavaData");

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO JavaData(name, price, quantity) VALUES (?, ?, ?)");

            for (Item item : items) {
                preparedStatement.setString(1, item.getName());
                preparedStatement.setDouble(2, item.getPrice());
                preparedStatement.setInt(3, item.getQuantity());

                preparedStatement.executeUpdate();
            }

            statement.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("There was a problem writing the file from the database !");
        }
    }

    public void changeItemValues(double price, int quantity, int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                item.setPrice(price);
                item.setQuantity(quantity);
                break;
            }
        }
    }

    public void addItem(String name, double price, int quantity) {
        items.add(new Item(name, price, quantity, getNewId()));
    }

    private int getNewId() {
        if (items.isEmpty()) {
            return 0;
        } else {
            return items.getLast().getId() + 1;
        }
    }

    public void deleteItem(int id) {
        for (Item item : items) {
            if (item.getId() == id) {
                items.remove(item);
                break;
            }
        }
    }

    public boolean checkIfNameExist(String name) {
        for (Item item : items) {
            if (Objects.equals(item.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }

}

class AddItemWindow extends JFrame {
    private final Window window;
    private final Data data;

    private final JTextField nameTextField;
    private final JTextField priceTextField;
    private final JTextField quantityTextField;

    public AddItemWindow(Window window, Data data) {
        super();

        // setting attributes to the window
        this.setSize(300, 300);
        this.setTitle("Add new item");
        this.setLayout(new GridBagLayout());

        // passing the argument
        this.window = window;
        this.data = data;

        // creating the widgets
        GridBagConstraints layout = new GridBagConstraints();
        nameTextField = new JTextField();
        priceTextField = new JTextField();
        quantityTextField = new JTextField();
        JButton addButton = new JButton("Add item");

        // setting attributes and connecting the widgets
        nameTextField.setPreferredSize(new Dimension(100, 25));
        priceTextField.setPreferredSize(new Dimension(100, 25));
        quantityTextField.setPreferredSize(new Dimension(100, 25));
        addButton.addActionListener(e -> AddItemWindow.this.onAddClicked());

        // adding the widgets
        layout.gridx = 0;
        layout.gridy = 0;
        this.add(new JLabel("Name :"), layout);

        layout.gridx = 1;
        this.add(nameTextField);

        layout.gridx = 0;
        layout.gridy = 1;
        this.add(new JLabel("Price :"), layout);

        layout.gridx = 1;
        this.add(priceTextField, layout);

        layout.gridx = 0;
        layout.gridy = 2;
        this.add(new JLabel("Quantity"), layout);

        layout.gridx = 1;
        this.add(quantityTextField, layout);

        layout.gridx = 0;
        layout.gridy = 3;
        this.add(addButton, layout);

        this.setVisible(true);
    }

    void onAddClicked() {
        String name = nameTextField.getText();
        double price;
        int quantity;

        if (data.checkIfNameExist(name)) {
            JOptionPane.showMessageDialog(this, "The name already exist !");
            return;
        }

        try {
            price = Double.parseDouble(priceTextField.getText());
        } catch (NumberFormatException e) {
            priceTextField.setText("");
            JOptionPane.showMessageDialog(this, "Invalid input in price!");
            return;
        }

        try {
            quantity = Integer.parseInt(quantityTextField.getText());
        } catch (NumberFormatException e) {
            quantityTextField.setText("");
            JOptionPane.showMessageDialog(this, "Invalid input in quantity!");
            return;
        }

        data.addItem(name, price, quantity);
        window.onSearch();
        this.dispose();
    }
}

class ItemInfo extends JFrame {
    private final Window window;
    private final Data data;

    int id;
    private final JTextField priceTextField;
    private final JTextField quantityTextField;

    public ItemInfo(Window window, Data data, Item item) {
        super();

        // setting attributes to the window
        this.setSize(300, 300);
        this.setTitle(item.getName());
        this.setLayout(new GridBagLayout());

        // passing the arguments
        this.window = window;
        this.data = data;
        this.id = item.getId();

        // creating the widgets
        GridBagConstraints layout = new GridBagConstraints();
        priceTextField = new JTextField();
        quantityTextField = new JTextField();
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete");

        // connecting the widgets to the function and setting attributes
        priceTextField.setPreferredSize(new Dimension(100, 25));
        priceTextField.setText(Double.toString(item.getPrice()));
        quantityTextField.setPreferredSize(new Dimension(100, 25));
        quantityTextField.setText(Integer.toString(item.getQuantity()));
        saveButton.addActionListener(e -> ItemInfo.this.onSaveClicked());
        deleteButton.addActionListener(e -> ItemInfo.this.onDeleteClicked());


        // adding the widgets
        layout.gridx = 0;
        layout.gridy = 0;
        this.add(new JLabel("Price :"), layout);

        layout.gridx = 1;
        this.add(priceTextField, layout);

        layout.gridx = 0;
        layout.gridy = 1;
        this.add(new JLabel("Quantity :"), layout);

        layout.gridx = 1;
        this.add(quantityTextField, layout);


        layout.gridx = 0;
        layout.gridy = 2;
        this.add(saveButton, layout);

        layout.gridy = 3;
        this.add(deleteButton, layout);

        this.setVisible(true);
    }

    void onSaveClicked() {
        double price;
        int quantity;

        try {
            price = Double.parseDouble(priceTextField.getText());
        } catch (NumberFormatException e) {
            priceTextField.setText("");
            JOptionPane.showMessageDialog(this, "Invalid input in price!");
            return;
        }

        try {
            quantity = Integer.parseInt(quantityTextField.getText());

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input in quantity!");
            return;
        }

        data.changeItemValues(price, quantity, id);
        window.onSearch();
        this.dispose();
    }

    void onDeleteClicked() {
        data.deleteItem(id);
        window.onSearch();
        this.dispose();
    }

}

class Window extends JFrame {
    private final Data data = new Data();
    private final JTextField searchBar;

    private final ArrayList<JButton> results = new ArrayList<>();

    public Window() {
        super();

        // setting attributes to the window
        this.setSize(500, 500);
        this.setTitle("Inventory management system");
        this.setLayout(new GridBagLayout());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                data.storeData();
                System.exit(0);
            }
        });

        // creating the widgets
        GridBagConstraints layout = new GridBagConstraints();
        JButton addButton = new JButton("Add new item");
        searchBar = new JTextField();

        // connecting the widgets with function and setting attributes
        addButton.addActionListener(e -> new AddItemWindow(Window.this, Window.this.data));
        searchBar.addActionListener(e -> Window.this.onSearch());
        searchBar.setPreferredSize(new Dimension(150, 25));

        // adding the widgets
        layout.gridx = 0;
        layout.gridy = 0;
        this.add(addButton, layout);

        layout.gridy = 1;
        this.add(searchBar, layout);
    }


    void onSearch() {
        for (JButton result : results) {
            this.getContentPane().remove(result);
        }
        results.clear();

        String text = searchBar.getText();
        GridBagConstraints layout = new GridBagConstraints();
        layout.gridx = 0;
        layout.gridy = 2;

        for (Item item : data) {
            if (item.getName().startsWith(text)) {
                JButton result = new JButton(item.getName());
                results.add(result);

                result.addActionListener(e -> new ItemInfo(Window.this, Window.this.data, item));

                this.add(result, layout);
                layout.gridy++;
            }
        }

        this.revalidate();
        this.repaint();
    }

}

class Main {
    public static void main(String[] args) {
        Window window = new Window();
        window.setVisible(true);
    }
}