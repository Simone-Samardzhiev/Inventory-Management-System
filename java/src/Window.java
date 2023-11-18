import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Window extends JFrame {
    private final Inventory inventory = new Inventory();

    class TextArea extends JPanel {

        JLabel label = new JLabel(inventory.toString());

        TextArea() {
            super(new BorderLayout());


            JScrollPane scrollPane = new JScrollPane(label);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            scrollPane.setPreferredSize(new Dimension(200, 300));

            this.add(scrollPane, BorderLayout.CENTER);
        }

        public void update_text() {
            this.label.setText(inventory.toString());
        }
    }

    public class SearchArea extends JPanel {
        private String results;
        private final JTextField textField = new JTextField();
        private final JLabel label = new JLabel();

        SearchArea() {
            super(new GridBagLayout());
            this.textField.setPreferredSize(new Dimension(120, 30));

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.weightx = 1;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.weighty = 0;
            this.add(textField, gridBagConstraints);

            gridBagConstraints.gridy = 1;
            gridBagConstraints.weighty = 1;
            gridBagConstraints.anchor = GridBagConstraints.NORTH;
            this.add(label, gridBagConstraints);

            this.textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    results = inventory.search_for_item(textField.getText());
                    label.setText(results);
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
        }
    }


    class FunctionsArea extends JPanel {

        private final JTextField textField_for_deleting = new JTextField();
        private final JLabel label_delete_error = new JLabel();
        private final JButton button_for_deleting = new JButton();

        private final JTextField textField_for_new = new JTextField();
        private final JLabel label_new_error = new JLabel();
        private final JButton button_for_new = new JButton();

        private final JTextField textField_for_changing = new JTextField();
        private final JLabel label_change_error = new JLabel();
        private final JButton button_for_changing = new JButton();

        FunctionsArea(TextArea textArea) {
            super(new GridBagLayout());

            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;

            this.textField_for_deleting.setPreferredSize(new Dimension(120, 30));
            this.add(textField_for_deleting, gridBagConstraints);
            this.textField_for_deleting.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    label_delete_error.setText(null);
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            gridBagConstraints.gridx = 1;
            this.button_for_deleting.setPreferredSize(new Dimension(120, 30));
            this.button_for_deleting.setText("Delete");
            this.add(button_for_deleting, gridBagConstraints);

            gridBagConstraints.gridx = 2;
            this.add(label_delete_error, gridBagConstraints);

            this.button_for_deleting.addActionListener(e -> {
                try {
                    inventory.delete_item(textField_for_deleting.getText());
                    textArea.update_text();

                } catch (IllegalArgumentException error) {
                    label_delete_error.setText(error.getMessage());
                }
            });

            gridBagConstraints.gridy = 1;
            gridBagConstraints.gridx = 0;

            this.textField_for_new.setPreferredSize(new Dimension(120, 30));
            this.add(textField_for_new, gridBagConstraints);
            this.textField_for_new.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    label_new_error.setText("");
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            gridBagConstraints.gridx = 1;

            this.button_for_new.setText("Add new element");
            this.button_for_new.setPreferredSize(new Dimension(120, 30));
            this.add(button_for_new, gridBagConstraints);
            this.button_for_new.addActionListener(e -> {
                String[] attributes;
                String name;
                double price;
                int quantity;

                attributes = textField_for_new.getText().split(":");
                try {
                    name = attributes[0];
                    price = Double.parseDouble(attributes[1]);
                    quantity = Integer.parseInt(attributes[2]);
                    inventory.write_item(new Item(name, price, quantity));
                    textArea.update_text();

                } catch (NumberFormatException | IndexOutOfBoundsException error) {
                    label_new_error.setText("Invalid input");
                } catch (IllegalArgumentException error) {
                    label_new_error.setText(error.getMessage());
                }

            });

            gridBagConstraints.gridx = 2;
            this.add(label_new_error, gridBagConstraints);

            gridBagConstraints.gridy = 2;
            gridBagConstraints.gridx = 0;

            this.textField_for_changing.setPreferredSize(new Dimension(120, 30));
            this.add(textField_for_changing, gridBagConstraints);
            this.textField_for_changing.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    label_change_error.setText("");
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });

            gridBagConstraints.gridx = 1;

            this.button_for_changing.setText("Change item");
            this.button_for_changing.setPreferredSize(new Dimension(120, 30));
            this.add(button_for_changing, gridBagConstraints);
            this.button_for_changing.addActionListener(e -> {
                String[] attributes;
                String name;
                double price;
                int quantity;

                attributes = textField_for_changing.getText().split(":");

                try {
                    name = attributes[0];
                    if (!attributes[1].isEmpty() && !attributes[2].isEmpty()) {
                        price = Double.parseDouble(attributes[1]);
                        quantity = Integer.parseInt(attributes[2]);
                        inventory.change_item_attributes(name, price, quantity);
                        textArea.update_text();

                    } else if (!attributes[1].isEmpty()) {
                        price = Double.parseDouble(attributes[1]);
                        inventory.change_item_price(name, price);
                        textArea.update_text();

                    } else if (!attributes[2].isEmpty()) {
                        quantity = Integer.parseInt(attributes[2]);
                        inventory.change_item_quantity(name, quantity);
                        textArea.update_text();

                    }
                } catch (NumberFormatException | IndexOutOfBoundsException error) {
                    label_change_error.setText("Invalid input");
                } catch (IllegalArgumentException error) {
                    label_change_error.setText(error.getMessage());
                }
            });

            gridBagConstraints.gridx = 2;
            this.add(label_change_error, gridBagConstraints);
        }

    }

    public Window() {
        super();
        this.setSize(600, 400);
        this.setTitle("Inventory Management System");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());

        TextArea textArea = new TextArea();
        SearchArea searchArea = new SearchArea();
        FunctionsArea functionsArea = new FunctionsArea(textArea);

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        gridBagConstraints.weighty = 1;

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.add(textArea, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        this.add(searchArea, gridBagConstraints);

        gridBagConstraints.gridx = 2;
        this.add(functionsArea, gridBagConstraints);
    }
}
