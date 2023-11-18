
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class Inventory {
    private final String PATH = "/home/simone/Desktop/My projects/Inventory Management System/java/inventory.txt";
    private final ArrayList<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    private void addItem(Item item) {
        this.items.add(item);
    }


    private void read_data() {
        this.items.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(",");

                this.addItem(new Item(attributes[0], Double.parseDouble(attributes[1]), Integer.parseInt(attributes[2])));
            }
        } catch (IOException e) {
            System.err.println("There was an error opening the file!");
        }
    }

    private void write_data() {
        try (FileWriter file = new FileWriter(PATH, false)) {
            for (Item item : this.items) {
                file.write(item.getName() + ',' + item.getPrice() + ',' + item.getQuantity() + '\n');
            }
        } catch (IOException e) {
            System.err.println("There was an error opening the file!");
        }
    }

    private boolean check_item(String name) {
        boolean found = false;

        for (Item item : this.items) {
            if (Objects.equals(name, item.getName())) {
                found = true;
                break;
            }
        }

        return found;
    }

    @Override
    public String toString() {
        this.read_data();
        StringBuilder result = new StringBuilder();

        result.append("<html>");
        for (Item item : this.items) {
            result.append("Name :").append(item.getName()).append("<br>");
            result.append("Price :").append(item.getPrice()).append("<br>");
            result.append("Quantity :").append(item.getQuantity()).append("<br><br>");

        }
        result.append("<html>");

        return result.toString();
    }

    public String search_for_item(String symbols) {
        if (Objects.equals(symbols, "")) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        result.append("<html>");

        for (Item item : this.items) {
            if (item.getName().startsWith(symbols)) {
                result.append("Name :").append(item.getName()).append(", ").append("Price :").append(item.getPrice()).append(", ").append("Quantity :").append(item.getQuantity()).append("<br>");
            }
        }
        result.append("<html>");

        return result.toString();
    }

    public void delete_item(String name) throws IllegalArgumentException {
        int index = 0;

        if (!check_item(name)) {
            throw new IllegalArgumentException("The name of the item wasn't found");
        }

        for (Item item : this.items) {
            if (Objects.equals(name, item.getName())) {
                break;
            }
            index++;
        }

        this.items.remove(index);
        write_data();
    }

    public void write_item(Item item) throws IllegalArgumentException {
        if (item.getName().isEmpty()) {
            throw new IllegalArgumentException("The name of the item is empty");
        } else if (check_item(item.getName())) {
            throw new IllegalArgumentException("The item already is in the inventory");
        }

        this.items.add(item);

        try (FileWriter file = new FileWriter(PATH, true)) {
            file.write(item.getName() + ',' + item.getPrice() + ',' + item.getQuantity() + '\n');
        } catch (IOException error) {
            System.err.println("The was an error opening the file!");
        }
    }


    public void change_item_price(String name, double price) throws IllegalArgumentException {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name of the item is empty");
        }
        if (!check_item(name)) {
            throw new IllegalArgumentException("The name of the item wasn't found");
        }

        for (Item item : items) {
            if (Objects.equals(item.getName(), name)) {
                item.setPrice(price);
            }
        }
        this.write_data();
    }

    public void change_item_quantity(String name, int quantity) throws IllegalArgumentException {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name of the item is empty");
        }
        if (!check_item(name)) {
            throw new IllegalArgumentException("The name of the item wasn't found");
        }

        for (Item item : items) {
            if (Objects.equals(item.getName(), name)) {
                item.setQuantity(quantity);
            }
        }
        write_data();
    }

    public void change_item_attributes(String name, double price, int quantity) throws IllegalArgumentException {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("The name of the item is empty");
        }
        if (!check_item(name)) {
            throw new IllegalArgumentException("The name of the item wasn't found");
        }
        for (Item item : items) {
            if (Objects.equals(item.getName(), name)) {
                item.setPrice(price);
                item.setQuantity(quantity);
            }
        }
        write_data();
    }


}
