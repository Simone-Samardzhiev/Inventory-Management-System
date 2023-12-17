import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

class Item {
    private final String name;
    private double price;
    private int quantity;

    public Item(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

class Data {
    final String PATH = "/Users/simonesamardzhiev/Desktop/My projects/Inventory Management System/java/data.json";
    private ArrayList<Item> items;

    public Data() {
        readData();
    }

    private void readData() {
        try (FileReader reader = new FileReader(PATH)) {
            Type type = new TypeToken<ArrayList<Item>>() {
            }.getType();
            Gson gson = new Gson();
            this.items = gson.fromJson(reader, type);
        } catch (IOException e) {
            System.err.println("The file for reading couldn't be opened");

        }
    }

    public void writeData() {
        try (FileWriter writer = new FileWriter(PATH)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this.items, writer);
        } catch (IOException e) {
            System.out.println("The file for writing couldn't be opened");
        }
    }

    public boolean checkIfItemExist(String name) {
        for (Item item : this.items) {
            if (Objects.equals(item.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public void addItem(String name, double price, int quantity) {
        this.items.add(new Item(name, price, quantity));
    }

    public void changeItemAttributes(String name, double price, int quantity) {
        for (Item item : this.items) {
            if (Objects.equals(item.getName(), name)) {
                item.setPrice(price);
                item.setQuantity(quantity);
                break;
            }
        }
    }

    public void deleteItem(String name) {
        for (Item item : this.items) {
            if (Objects.equals(item.getName(), name)) {
                this.items.remove(item);
                break;
            }
        }
    }

    public ArrayList<Item> iterOverItems() {
        return this.items;
    }
}

class CreateItem extends JFrame {
    CreateItem(Data data) {
        super();

        this.setSize(400, 200);
        this.setTitle("Add new item");
        this.setLayout(new GridBagLayout());
        this.setResizable(false);

        GridBagConstraints layout = new GridBagConstraints();
        layout.gridy = 0;
        layout.gridx = 0;

        this.add(new JLabel("Enter the name :"), layout);

        layout.gridx = 1;

        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(100, 20));
        this.add(nameField, layout);

        layout.gridx = 0;
        layout.gridy = 1;

        this.add(new JLabel("Enter the price :"), layout);

        layout.gridx = 1;

        JTextField priceField = new JTextField();
        priceField.setPreferredSize(new Dimension(100, 20));
        this.add(priceField, layout);

        layout.gridx = 0;
        layout.gridy = 2;

        this.add(new JLabel("Enter the quantity :"), layout);

        layout.gridx = 1;

        JTextField quantityField = new JTextField();
        quantityField.setPreferredSize(new Dimension(100, 20));
        this.add(quantityField, layout);

        layout.gridx = 0;
        layout.gridy = 3;

        JButton button = new JButton("Add item");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    String name = nameField.getText();
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());

                    if (data.checkIfItemExist(name)) {
                        JOptionPane.showMessageDialog(CreateItem.this, "The name already exists !");
                    } else {
                        data.addItem(name, price, quantity);
                        CreateItem.this.dispose();
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(CreateItem.this, "The values are not numbers !");
                }
            }
        });
        this.add(button, layout);

        this.setVisible(true);
    }
}

class ItemInfo extends JFrame {


    ItemInfo(Item item, Data data) {
        super();

        this.setSize(200, 200);
        this.setTitle(item.getName());
        this.setLayout(new GridBagLayout());
        this.setResizable(false);

        GridBagConstraints layout = new GridBagConstraints();
        layout.gridx = 0;
        layout.gridy = 0;

        this.add(new JLabel("Price :"), layout);

        layout.gridx = 1;

        JTextField priceField = new JTextField();
        priceField.setPreferredSize(new Dimension(100, 20));
        priceField.setText(Double.toString(item.getPrice()));
        this.add(priceField, layout);

        layout.gridy = 1;
        layout.gridx = 0;

        this.add(new JLabel("Quantity :"), layout);

        layout.gridx = 1;

        JTextField quantityField = new JTextField();
        quantityField.setPreferredSize(new Dimension(100, 20));
        quantityField.setText(Integer.toString(item.getQuantity()));
        this.add(quantityField, layout);

        layout.gridx = 0;
        layout.gridy = 2;

        JButton buttonForDeleting = new JButton("Delete");
        buttonForDeleting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                data.deleteItem(item.getName());
                ItemInfo.this.dispose();
            }
        });
        this.add(buttonForDeleting, layout);

        layout.gridy = 3;

        JButton buttonForSaving = new JButton("Save");
        buttonForSaving.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    data.changeItemAttributes(item.getName(), price, quantity);
                    ItemInfo.this.dispose();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(ItemInfo.this, "The values are not numbers !");
                }
            }
        });
        this.add(buttonForSaving, layout);

        this.setVisible(true);
    }
}

class Window extends JFrame {
    private final Data data = new Data();
    private final ArrayList<JButton> results = new ArrayList<>();

    JTextField searchField = new JTextField();

    Window() {
        super();

        this.setTitle("Inventory management system");
        this.setSize(600, 600);
        this.setLayout(new GridBagLayout());

        GridBagConstraints layout = new GridBagConstraints();
        layout.gridx = 0;
        layout.gridy = 0;

        JButton button = new JButton("Add item");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                new CreateItem(Window.this.data);
            }
        });
        this.add(button, layout);

        layout.gridy = 1;


        searchField.setPreferredSize(new Dimension(100, 20));
        this.add(searchField, layout);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                onSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                onSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                onSearch();
            }
        });


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Window.this.data.writeData();
                System.exit(0);
            }
        });
    }

    private void onSearch() {
        Container container = this.getContentPane();
        for (JButton button : this.results) {
            container.remove(button);
        }
        this.results.clear();
        container.repaint();
        container.revalidate();

        GridBagConstraints layout = new GridBagConstraints();
        layout.gridy = 2;
        layout.gridx = 0;
        String text = this.searchField.getText();

        for (Item item : this.data.iterOverItems()) {
            if (item.getName().startsWith(text)) {
                JButton button = new JButton(item.getName());
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        new ItemInfo(item, Window.this.data);
                    }
                });
                this.add(button, layout);
                layout.gridy++;
                this.results.add(button);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Window window = new Window();
        window.setVisible(true);
    }
}
